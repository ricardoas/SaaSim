package planning.heuristic;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import provisioning.DPS;
import provisioning.util.DPSFactory;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.config.Configuration;
import commons.io.HistoryBasedWorkloadParser;
import commons.io.WorkloadParser;
import commons.sim.AccountingSystem;
import commons.sim.SimpleSimulator;
import commons.sim.Simulator;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.SimulatorFactory;
import commons.sim.util.SimulatorProperties;
import commons.util.Dashboard;
import commons.util.SimulationData;

public class PlanningFitnessFunction extends FitnessFunction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7906976193829216027L;
	private final HistoryBasedWorkloadParser parser;
//	private final Map<User, Contract> cloudUsers;
//	private final Provider cloudProvider;
	
	private Dashboard dashboard;
	private Map<Integer, Double> solvedProblems;
	private Simulator simulator;
	private final int maximumReservedResources;

	
	public PlanningFitnessFunction(HistoryBasedWorkloadParser parser, List<User> cloudUsers, List<Provider> cloudProvider, int maximumReservedResources){
		this.parser = parser;
		try {
			this.parser.next();
			this.parser.setReadNextPeriod(false);
		} catch (IOException e) {
			throw new RuntimeException("Planning Fitness Constructor: "+e.getMessage());
		}
		
		//		this.currentWorkload = currentWorkload;
//		this.cloudUsers = cloudUsers;
//		this.cloudProvider = cloudProvider.values().iterator().next();
		//		this.utilityFunction = new UtilityFunction();
		
		this.maximumReservedResources = maximumReservedResources;
		this.dashboard = new Dashboard();//Place to store detailed information
		this.solvedProblems = new HashMap<Integer, Double>();//Used to reuse previous calculated scenarios
	}

	private void initSimulator(Integer reservedResources) {
		
		//Setting the number of machines that should be available at startup
		Configuration.getInstance().setProperty(SimulatorProperties.APPLICATION_INITIAL_SERVER_PER_TIER, reservedResources+"");
		
		//Creating simulator structure
		DPS dps = DPSFactory.INSTANCE.createDPS();
		dps.getAccountingSystem().setMaximumNumberOfReservedMachinesUsed(this.maximumReservedResources);
		this.simulator = SimulatorFactory.getInstance().buildSimulator(dps);
		dps.registerConfigurable(simulator);
		
		this.simulator.setWorkloadParser(parser);//Changing parser to a history based one!
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
		
		//FIXME! Retrieve UtilityResult from simulator!
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
			return (1/Math.abs(fitness))+1;
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
