package planning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityFunction;
import commons.sim.OneTierSimulatorForPlanning;
import commons.sim.jeevent.JEEventScheduler;
import commons.util.Dashboard;

import config.GEISTMonthlyWorkloadParser;

public class Executor {

	private static final String OUTUPUT_FILE = "executor.dat";
	private final Provider provider;
	private final HashMap<User, Contract> usersContracts;
	private final GEISTMonthlyWorkloadParser workloadParser;
	private final double sla;
	private UtilityFunction utilityFunction;

	private List<String> executorData;
	

	public Executor(Map<String, Provider> providers,
			HashMap<User, Contract> usersContracts,
			GEISTMonthlyWorkloadParser workloadParser, double sla) {
		this.provider = providers.values().iterator().next();
		this.usersContracts = usersContracts;
		this.workloadParser = workloadParser;
		this.sla = sla;
		
		this.utilityFunction = new UtilityFunction();
		this.executorData = new ArrayList<String>();
	}
	
	private void verifyProperties() {
		if(this.sla <= 0){
			throw new RuntimeException("Invalid sla in Planner: "+this.sla);
		}
		if(this.usersContracts == null || this.usersContracts.size() == 0){
			throw new RuntimeException("Invalid users in Planner!");
		}
		if(this.provider == null){
			throw new RuntimeException("Invalid cloud providers in Planner!");
		}
		if(this.workloadParser == null){
			throw new RuntimeException("Invalid workload parser in Planner!");
		}
	}


	public void execute(List<String> plan) throws IOException {
		Map<User, List<Request>> currentWorkload = this.workloadParser.next();
		int periodIndex = 0;
		
		while(!currentWorkload.isEmpty()){//For each planning period
			
			//Gathering all requests
			List<Request> workload = new ArrayList<Request>();
			for(User user : currentWorkload.keySet()){
				workload.addAll(currentWorkload.get(user));
			}
			
			//Starting simulation data to start a new simulation
			OneTierSimulatorForPlanning simulator = new OneTierSimulatorForPlanning(new JEEventScheduler(), workload, this.sla);
			simulator.setOnDemandResourcesLimit(this.provider.onDemandLimit);
			int numberOfMachinesToReserve = parseNumberOfReservedResources(plan, periodIndex);
			simulator.setNumberOfReservedResources(numberOfMachinesToReserve);
			
			simulator.start();
			
			this.updateInformation(currentWorkload, simulator);//Collecting informations to calculate utility
			
			//Computing total utility
			double utility = 0d;
			double totalTransferred = 0d;
			for(User user : currentWorkload.keySet()){
				utility += this.utilityFunction.calculateTotalReceipt(this.usersContracts.get(user), user);
				totalTransferred += user.consumedTransference;
			}
			double cost = this.utilityFunction.calculateCost(totalTransferred, this.provider);
			utility -= cost;
			
			//Storing information
			storeExecution(numberOfMachinesToReserve, this.provider.onDemandResources.size(), 
					utility, cost, utility+cost, totalTransferred, this.provider.onDemandConsumption(), this.provider.reservedConsumption());
		}
		
		//Persisting executor data
		persistData();
	}


	private void persistData() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OUTUPUT_FILE)));
			for(String data : this.executorData){
				writer.write(data+"\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void storeExecution(int numberOfMachinesToReserve, int numberOfOnDemandMachines,
			double utility, double cost, double receipt, double totalTransferred,
			double onDemandConsumption, double reservedConsumption) {
		this.executorData.add(numberOfMachinesToReserve + "\t" + reservedConsumption
				+ "\t" + numberOfOnDemandMachines + "\t" + onDemandConsumption +
				"\t" + totalTransferred + "\t" + utility + "\t" + receipt
				+ "\t" + cost);
	}

	private int parseNumberOfReservedResources(List<String> plan,
			int periodIndex) {
		String string = plan.get(periodIndex);
		String[] split = string.split("\t");
		return Integer.valueOf(split[0].trim());
	}
	
	private void updateInformation(Map<User, List<Request>> currentWorkload, OneTierSimulatorForPlanning simulator) {
		//Updating users
		for(User user : currentWorkload.keySet()){
			List<Request> requests = currentWorkload.get(user);
			double totalProcessed = 0;
			double totalTransfered = 0;
			for(Request request : requests){
				totalProcessed += request.totalProcessed;
				totalTransfered += request.size;
			}
			user.consumedCpu = totalProcessed;
			user.consumedTransference = totalTransfered;
		}
		
		//Updating provider
		this.provider.onDemandResources = simulator.getOnDemandResources();
		this.provider.reservedResources = simulator.getReservedResources();
	}

}
