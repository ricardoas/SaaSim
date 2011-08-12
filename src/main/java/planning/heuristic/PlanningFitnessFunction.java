package planning.heuristic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.config.Configuration;
import commons.sim.AccountingSystem;
import commons.sim.SimpleSimulator;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.SimulatorProperties;
import commons.util.Dashboard;
import commons.util.SimulationData;

import config.GEISTMonthlyWorkloadParser;

public class PlanningFitnessFunction extends FitnessFunction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7906976193829216027L;
	private final GEISTMonthlyWorkloadParser parser;
	private final Map<User, Contract> cloudUsers;
	private final Provider cloudProvider;
	
//	private OneTierSimulatorForPlanning simulator;
//	private UtilityFunction utilityFunction;
//	private final Map<User, List<Request>> currentWorkload;
	
	private Dashboard dashboard;
	private Map<Integer, Double> solvedProblems;
	private SimpleSimulator simulator;

	
	public PlanningFitnessFunction(GEISTMonthlyWorkloadParser parser, Map<User, Contract> cloudUsers, Map<String, Provider> cloudProvider){
		this.parser = parser;
		//		this.currentWorkload = currentWorkload;
		this.cloudUsers = cloudUsers;
		this.cloudProvider = cloudProvider.values().iterator().next();
		//		this.utilityFunction = new UtilityFunction();
		
		this.dashboard = new Dashboard();//Place to store detailed information
		this.solvedProblems = new HashMap<Integer, Double>();//Used to reuse previous calculated scenarios
	}

	private void initSimulator(Integer reservedResources) {
		//Starting simulation data to start a new simulation
//		this.simulator = new OneTierSimulatorForPlanning(new JEEventScheduler(), null, workload, this.sla, null);//FIXME remove null (dont know how)
//		this.simulator.setOnDemandResourcesLimit(this.cloudProvider.onDemandLimit);
//		this.simulator.setNumberOfReservedResources(reservedResources);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		//Setting the number of machines that should be available at startup
		Configuration.getInstance().setProperty(SimulatorProperties.APPLICATION_INITIAL_SERVER_PER_TIER, reservedResources+"");
		try {
			this.simulator = new SimpleSimulator(scheduler, parser);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected double evaluate(IChromosome chrom) {
		Integer numberOfMachinesToReserve = (Integer) chrom.getGene(0).getAllele();
		Double previousResult = this.solvedProblems.get(numberOfMachinesToReserve);
		
		if(previousResult != null){//Problem already solved
			return previousResult;
		}
		
		//Simulating requests
		this.initSimulator(numberOfMachinesToReserve);
		this.simulator.start();
		
		//Updating consumption information
		Map<User, List<Request>> currentWorkload = this.parser.getWorkloadPerUser();
		this.updateInformation(currentWorkload);
		
		//Computing total utility
		double fitness = 0d;
		double totalTransferred = 0d;
		AccountingSystem accountingSystem = this.simulator.getAccounting();
		
		for(User user : currentWorkload.keySet()){
			fitness += accountingSystem.calculateTotalReceipt(this.cloudUsers.get(user), user);
			totalTransferred += user.consumedTransference;
		}
		double cost = accountingSystem.calculateCost(this.cloudProvider);
		fitness -= cost;
		
		//Storing information
		this.dashboard.createEntry(numberOfMachinesToReserve, this.cloudProvider.onDemandResources.size(), 
				fitness, cost, fitness+cost, totalTransferred, this.cloudProvider.onDemandConsumption(), this.cloudProvider.reservedConsumption());
		
		this.solvedProblems.put(numberOfMachinesToReserve, fitness);
		if(fitness < 1){
			return 1;
		}
		
		return fitness;
	}

	private void updateInformation(Map<User, List<Request>> currentWorkload) {
		//Updating users
		for(User user : currentWorkload.keySet()){
			List<Request> requests = currentWorkload.get(user);
			double totalProcessed = 0;
			double totalTransfered = 0;
			for(Request request : requests){
				totalProcessed += request.totalProcessed;
				totalTransfered += request.getSizeInBytes();
			}
			user.consumedCpu = totalProcessed;
			user.consumedTransference = totalTransfered;
		}
		
		//Updating provider
		this.cloudProvider.onDemandResources = this.simulator.getAccounting().getOnDemandMachinesData();
		this.cloudProvider.reservedResources = this.simulator.getAccounting().getReservedMachinesData();
	}

	public SimulationData getDetailedEntry(Integer reservedResources) {
		return this.dashboard.simulationData.get(reservedResources);
	}
}
