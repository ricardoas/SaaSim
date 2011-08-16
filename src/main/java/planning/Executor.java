package planning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.io.HistoryBasedWorkloadParser;
//import commons.sim.OneTierSimulatorForPlanning;
import commons.sim.jeevent.JEEventScheduler;


//FIXME!
public class Executor {

	private static final String OUTUPUT_FILE = "executor.dat";
	private final List<Provider> providers;
	private final List<User> usersContracts;
	private final HistoryBasedWorkloadParser parser;
	private final double sla;

	private List<String> executorData;
	

	public Executor(List<Provider> providers, List<User> users, HistoryBasedWorkloadParser parser, double sla) {
		this.providers = providers;
		this.usersContracts = users;
		this.parser = parser;
		this.sla = sla;
		
		this.executorData = new ArrayList<String>();
	}
	
	private void verifyProperties() {
		if(this.sla <= 0){
			throw new RuntimeException("Invalid sla in Planner: "+this.sla);
		}
		if(this.usersContracts == null || this.usersContracts.size() == 0){
			throw new RuntimeException("Invalid users in Planner!");
		}
		if(this.providers == null){
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
			for(Entry<User, List<Request>> entry : currentWorkload.entrySet()){
				workload.addAll(entry.getValue());
			}
			
			//Starting simulation data to start a new simulation
//			OneTierSimulatorForPlanning simulator = new OneTierSimulatorForPlanning(new JEEventScheduler(), null, workload, this.sla);//FIXME remove null (dont know how)
			simulator.setOnDemandResourcesLimit(this.providers.onDemandLimit);
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
			double cost = this.utilityFunction.calculateCost(totalTransferred, this.providers);
			utility -= cost;
			
			//Storing information
			storeExecution(numberOfMachinesToReserve, this.providers.onDemandResources.size(), 
					utility, cost, utility+cost, totalTransferred, this.providers.onDemandConsumption(), this.providers.reservedConsumption());
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
		for (Entry<User, List<Request>> entry : currentWorkload.entrySet()) {
			List<Request> requests = entry.getValue();
			double totalProcessed = 0;
			double totalTransfered = 0;
			for(Request request : requests){
				totalProcessed += request.totalProcessed;
				totalTransfered += request.getRequestSizeInBytes();
			}
			entry.getKey().consumedCpu = totalProcessed;
			entry.getKey().consumedTransference = totalTransfered;
		}
		
		//Updating provider
		this.providers.onDemandResources = simulator.getOnDemandResources();
		this.providers.reservedResources = simulator.getReservedResources();
	}

}
