package saasim.planning.heuristic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
//import org.jgap.Chromosome;
//import org.jgap.Configuration;
//import org.jgap.Gene;
//import org.jgap.IChromosome;
//import org.jgap.InvalidConfigurationException;
//import org.jgap.impl.DefaultConfiguration;
//import org.jgap.impl.IntegerGene;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.User;
import saasim.planning.io.PlanningWorkloadParser;
import saasim.planning.util.PlanIOHandler;
import saasim.planning.util.Summary;
import saasim.provisioning.Monitor;
import saasim.sim.components.LoadBalancer;
import saasim.sim.jeevent.JEEventScheduler;
import saasim.sim.util.SimulatorProperties;


/**
 * This {@link PlanningHeuristic} makes capacity planning based on search for the best planning's configuration.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public class OptimalHeuristic implements PlanningHeuristic{
	
	private static final long YEAR_IN_HOURS = 8640;
	
	private Map<User, List<Summary>> summaries;
	private List<MachineType> types;
	
//	private IChromosome bestChromosome; 
	
	/**
	 * Default constructor.
	 * @param scheduler {@link JEEventScheduler} event scheduler
	 * @param monitor {@link Monitor} for reporting information
	 * @param loadBalancers a set of {@link LoadBalancer}s of the application
	 */
	public OptimalHeuristic(JEEventScheduler scheduler, Monitor monitor, LoadBalancer[] loadBalancers){
		this.types = new ArrayList<MachineType>();
		this.summaries = new HashMap<User, List<Summary>>();
//		this.bestChromosome = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void findPlan(Provider[] cloudProviders, User[] cloudUsers){
//		//Reading workload data
//		readWorkloadData(cloudUsers);
//	
//		//Configuring genetic algorithm
//		Configuration config = new DefaultConfiguration();
//		try {
//			PlanningFitnessFunction myFunc = createFitnessFunction(cloudUsers, cloudProviders);
//			
//			Map<MachineType, Integer> limits = findReservationLimits(cloudProviders[0]);
//			for(MachineType type : limits.keySet()){
//				types.add(type);
//			}
//			int [] currentValues = new int[limits.size()];
//			
//			evaluateGenes(config, myFunc, cloudProviders[0], limits, 0, currentValues);//Searching best configuration
//		} catch (InvalidConfigurationException e) {
//			e.printStackTrace();
//		}
//		
//		Map<MachineType, Integer> plan = this.getPlan(cloudUsers);
//		try {
//			PlanIOHandler.createPlanFile(plan, cloudProviders);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
	}

//	/**
//	 * Searches the best configuration for the planning.
//	 * @param config {@link Configuration} of the application
//	 * @param function the {@link PlanningFitnessFunction}
//	 * @param provider the {@link Provider} of application
//	 * @param limits the limits of reservation for this {@link OptimalHeuristic}
//	 * @param typesIndex the index of types
//	 * @param currentValues the current values based on the limits of reservation
//	 * @throws InvalidConfigurationException
//	 */
//	private void evaluateGenes(Configuration config, PlanningFitnessFunction function, Provider provider, Map<MachineType, Integer> limits, int typesIndex, int[] currentValues) throws InvalidConfigurationException {
//		if(typesIndex < this.types.size()){
//			MachineType machineType = this.types.get(typesIndex);
//			Integer limit = limits.get(machineType);
//			for(int i = 0; i <= limit; i++){
//				currentValues[typesIndex] = i;
//				evaluateGenes(config, function, provider, limits, typesIndex+1, currentValues);
//			}
//		}else{
//			Gene[] genes = new IntegerGene[provider.getAvailableTypes().length];
//			for(int i = 0; i < provider.getAvailableTypes().length; i++){
//				genes[i] = new IntegerGene(config);
//				genes[i].setAllele(currentValues[i]);
//			}
//			Chromosome chrom = new Chromosome(config, genes);
//			double fitness = function.evaluate(chrom);
//			if(this.bestChromosome == null || this.bestChromosome.getFitnessValue() < fitness){
//				this.bestChromosome = chrom;
//			}
//		}
//	}

	/**
	 * Reads the workload data through the {@link User}s of application.  
	 * @param cloudUsers the {@link User}s
	 */
	private void readWorkloadData(User[] cloudUsers) {
		saasim.config.Configuration simConfig = saasim.config.Configuration.getInstance();
		String[] workloads = simConfig.getWorkloads();
		
		//Applying a planning error in workload if it was defined!
		try{
			double error = simConfig.getDouble(SimulatorProperties.PLANNING_ERROR);
			int totalWorkloads = (int)Math.round(workloads.length * (1+error));
			String [] newWorkloads = new String[totalWorkloads];
			
			if(totalWorkloads > workloads.length){//Should add some workloads
				for(int i = 0; i < workloads.length; i++){
					newWorkloads[i] = workloads[i];
				}
				int difference = totalWorkloads - workloads.length;
				int index = workloads.length;
				for(int i = 0; i < difference; i++){
					newWorkloads[index++] = workloads[i];
				}
			}else{//Should remove some workloads
				for(int i = 0; i < totalWorkloads; i++){
					newWorkloads[i] = workloads[i];
				}
			}
			workloads = newWorkloads;
		}catch(NoSuchElementException e){
		}
		
		this.summaries = new HashMap<User, List<Summary>>();
		
		int index = 0;
		for(String workload : workloads){
			PlanningWorkloadParser parser;
			try {
				parser = new PlanningWorkloadParser(workload);
				parser.readData();
				this.summaries.put(cloudUsers[index++], parser.getSummaries());
				
			} catch (ConfigurationException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}

	/**
	 * Finds the reservation limits through the {@link Provider} of application.
	 * @param cloudProvider {@link Provider} of application
	 * @return A {@link Map} containing the {@link MachineType}s and the several maximum number of machines.
	 */
	private Map<MachineType, Integer> findReservationLimits(Provider cloudProvider) {
		Map<MachineType, Integer> typesLimits = new HashMap<MachineType, Integer>();
		
		MachineType[] machineTypes = cloudProvider.getAvailableTypes();
		long totalDemand = calcTotalDemand();
		
		for(MachineType type : machineTypes){
			double yearFee = cloudProvider.getReservationOneYearFee(type);
			double reservedCpuCost = cloudProvider.getReservedCpuCost(type);
			double onDemandCpuCost = cloudProvider.getOnDemandCpuCost(type);
			
			long minimumHoursToBeUsed = Math.round(yearFee / (onDemandCpuCost - reservedCpuCost));
			double usageProportion = 1.0 * minimumHoursToBeUsed / YEAR_IN_HOURS;
			
			int maximumNumberOfMachines = (int)Math.round(totalDemand / (usageProportion * YEAR_IN_HOURS));
			typesLimits.put(type, maximumNumberOfMachines);
		}
		return typesLimits;
	}

	/**
	 * Calculates the total demand in this {@link OptimalHeuristic}.
	 * @return The total demand.
	 */
	private long calcTotalDemand() {
		double totalDemand = 0;
		for(List<Summary> summaries : this.summaries.values()){
			for(Summary summary : summaries){
				totalDemand += summary.getTotalCpuHrs();
			}
		}
		return (long)Math.ceil(totalDemand);
	}

	/**
	 * Create a new {@link PlanningFitnessFunction}.
	 * @param cloudUsers the {@link User}s of application
	 * @param cloudProviders the {@link Provider}s of application
	 * @return A new {@link PlanningFitnessFunction}.
	 */
	private PlanningFitnessFunction createFitnessFunction(User[] cloudUsers, Provider[] cloudProviders) {
		return new PlanningFitnessFunction(this.summaries, cloudUsers, cloudProviders, this.types);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getEstimatedProfit(int period) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<MachineType, Integer> getPlan(User[] cloudUsers) {
		Map<MachineType, Integer> plan = new HashMap<MachineType, Integer>();
//		Gene[] genes = this.bestChromosome.getGenes();
		int index = 0;
		
		for(MachineType type : this.types){
//			plan.put(type, (Integer) genes[index++].getAllele());
		}
//		System.out.println("CONFIG: "+genes[0].getAllele()+" "+genes[1].getAllele()+" "+genes[2].getAllele());
//		System.out.println("BEST: "+bestChromosome.getFitnessValue());
		return plan;
	}
}
