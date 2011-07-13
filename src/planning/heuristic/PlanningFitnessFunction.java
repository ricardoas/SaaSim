package planning.heuristic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityFunction;
import commons.sim.OneTierSimulatorForPlanning;

public class PlanningFitnessFunction extends FitnessFunction{
	
	private OneTierSimulatorForPlanning simulator;
	private final Map<User, Contract> cloudUsers;
	private UtilityFunction utilityFunction;
	private final double sla;
	private final Provider cloudProvider;
	private final Map<User, List<Request>> currentWorkload;
	
	public PlanningFitnessFunction(Map<User, List<Request>> currentWorkload, Map<User, Contract> cloudUsers, double sla, Map<String, Provider> cloudProvider){
		this.currentWorkload = currentWorkload;
		this.cloudUsers = cloudUsers;
		this.sla = sla;
		this.cloudProvider = cloudProvider.values().iterator().next();
		this.utilityFunction = new UtilityFunction();
	}

	private void initSimulator(Map<User, List<Request>> currentWorkload, Integer reservedResources) {
		List<Request> workload = new ArrayList<Request>();
		for(User user : currentWorkload.keySet()){
			workload.addAll(currentWorkload.get(user));
		}
		this.simulator = new OneTierSimulatorForPlanning(workload);
		this.simulator.setOnDemandResourcesLimit(this.cloudProvider.onDemandLimit);
		this.simulator.setNumberOfReservedResources(reservedResources);
	}
	
	@Override
	protected double evaluate(IChromosome chrom) {
		
		//Simulating requests
		this.initSimulator(this.currentWorkload, (Integer) chrom.getGene(0).getAllele());
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
		fitness -= this.utilityFunction.calculateCost(totalTransferred, this.cloudProvider);
		
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
				totalTransfered += request.size;
			}
			user.consumedCpu = totalProcessed;
			user.consumedTransference = totalTransfered;
		}
		
		//Updating provider
		this.cloudProvider.onDemandResources = this.simulator.getOnDemandResources();
		this.cloudProvider.reservedResources = this.simulator.getReservedResources();
	}
}
