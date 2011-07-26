package planning.heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityFunction;
import commons.sim.OneTierSimulatorForPlanning;
import commons.sim.jeevent.JEEventScheduler;
import commons.util.Dashboard;
import commons.util.SimulationData;

public class PlanningFitnessFunction extends FitnessFunction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7906976193829216027L;
	
	private OneTierSimulatorForPlanning simulator;
	private final Map<User, Contract> cloudUsers;
	private UtilityFunction utilityFunction;
	private final double sla;
	private final Provider cloudProvider;
	private final Map<User, List<Request>> currentWorkload;
	
	private Dashboard dashboard;
	private Map<Integer, Double> solvedProblems;
	
	public PlanningFitnessFunction(Map<User, List<Request>> currentWorkload, Map<User, Contract> cloudUsers, double sla, Map<String, Provider> cloudProvider){
		this.currentWorkload = currentWorkload;
		this.cloudUsers = cloudUsers;
		this.sla = sla;
		this.cloudProvider = cloudProvider.values().iterator().next();
		this.utilityFunction = new UtilityFunction();
		
		this.dashboard = new Dashboard();//Place to store detailed information
		this.solvedProblems = new HashMap<Integer, Double>();//Used to reuse previous calculated scenarios
	}

	private void initSimulator(Map<User, List<Request>> currentWorkload, Integer reservedResources) {
		List<Request> workload = new ArrayList<Request>();
		for (Entry<User, List<Request>> entry : currentWorkload.entrySet()) {
			workload.addAll(entry.getValue());
		}
		
		//TODO: CHANGE ME! Starting simulation data to start a new simulation
		this.simulator = new OneTierSimulatorForPlanning(new JEEventScheduler(), null, workload, this.sla, null);//FIXME remove null (dont know how)
		this.simulator.setOnDemandResourcesLimit(this.cloudProvider.onDemandLimit);
		this.simulator.setNumberOfReservedResources(reservedResources);
	}
	
	@Override
	protected double evaluate(IChromosome chrom) {
		Integer numberOfMachinesToReserve = (Integer) chrom.getGene(0).getAllele();
		Double previousResult = this.solvedProblems.get(numberOfMachinesToReserve);
		
		if(previousResult != null){//Problem already solved
			return previousResult;
		}
		
		//Simulating requests
		this.initSimulator(this.currentWorkload, numberOfMachinesToReserve);
		this.simulator.start();
		
		//Updating consumption information
		this.updateInformation();
		
		//Computing total utility
		double fitness = 0d;
		double totalTransferred = 0d;
		for(User user : currentWorkload.keySet()){
			fitness += this.utilityFunction.calculateTotalReceipt(this.cloudUsers.get(user), user);
			totalTransferred += user.consumedTransference;
		}
		double cost = this.utilityFunction.calculateCost(totalTransferred, this.cloudProvider);
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

	private void updateInformation() {
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
		this.cloudProvider.onDemandResources = this.simulator.getOnDemandResources();
		this.cloudProvider.reservedResources = this.simulator.getReservedResources();
	}

	public SimulationData getDetailedEntry(Integer reservedResources) {
		return this.dashboard.simulationData.get(reservedResources);
	}
}
