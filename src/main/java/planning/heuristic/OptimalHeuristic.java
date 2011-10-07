package planning.heuristic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

import planning.io.PlanningWorkloadParser;
import planning.util.PlanIOHandler;
import planning.util.Summary;
import provisioning.Monitor;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.io.HistoryBasedWorkloadParser;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEEventScheduler;

public class OptimalHeuristic implements PlanningHeuristic{
	
	private static final long YEAR_IN_HOURS = 8640;
	
	private Map<User, List<Summary>> summaries;
	private List<MachineType> types;
	
	private IChromosome bestChromosome; 
	
//	private FileWriter writer;
//	private static final String OUTPUT_FILE = "optimal.plan";
	
	public OptimalHeuristic(JEEventScheduler scheduler, Monitor monitor, LoadBalancer[] loadBalancers){
		this.types = new ArrayList<MachineType>();
		this.summaries = new HashMap<User, List<Summary>>();
		this.bestChromosome = null;
//		try {
//			writer = new FileWriter(new File(OUTPUT_FILE));
//		} catch (IOException e) {
//			throw new RuntimeException("Invalid optimal output file!");
//		}
	}
	
	@Override
	public void findPlan(Provider[] cloudProviders, User[] cloudUsers){
		
		//Reading workload data
		readWorkloadData(cloudUsers);
	
		//Configuring genetic algorithm
		Configuration config = new DefaultConfiguration();
		try {

			PlanningFitnessFunction myFunc = createFitnessFunction(cloudUsers, cloudProviders);
			
			Map<MachineType, Integer> limits = findReservationLimits(cloudProviders[0]);
			for(MachineType type : limits.keySet()){
				types.add(type);
			}
			int [] currentValues = new int[limits.size()];
			
			evaluateGenes(config, myFunc, cloudProviders[0], limits, 0, currentValues);//Searching best configuration
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		Map<MachineType, Integer> plan = this.getPlan(cloudUsers);
		try {
			PlanIOHandler.createPlanFile(plan, cloudProviders);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void evaluateGenes(Configuration config, PlanningFitnessFunction function, Provider provider, Map<MachineType, Integer> limits, int typesIndex, int[] currentValues) throws InvalidConfigurationException {
		if(typesIndex < this.types.size()){
			MachineType machineType = this.types.get(typesIndex);
			Integer limit = limits.get(machineType);
			for(int i = 0; i <= limit; i++){
				currentValues[typesIndex] = i;
				evaluateGenes(config, function, provider, limits, typesIndex+1, currentValues);
			}
		}else{
			Gene[] genes = new IntegerGene[provider.getAvailableTypes().length];
			for(int i = 0; i < provider.getAvailableTypes().length; i++){
				genes[i] = new IntegerGene(config);
				genes[i].setAllele(currentValues[i]);
			}
			Chromosome chrom = new Chromosome(config, genes);
			double fitness = function.evaluate(chrom);
			if(this.bestChromosome == null || this.bestChromosome.getFitnessValue() < fitness){
				this.bestChromosome = chrom;
			}
//			persistData(chrom, fitness);
		}
	}

//	private void persistData(Chromosome key, double fitness) {
//		StringBuilder result = new StringBuilder();
//		for(Gene gene : key.getGenes()){
//			result.append(gene.getAllele()+"\t");
//		}
//		result.append(fitness+"\n");
//		try {
//			writer.write(result.toString());
//		} catch (IOException e) {
//			throw new RuntimeException("Could not write in optimal output file");
//		}
//	}

	private void readWorkloadData(User[] cloudUsers) {
		commons.config.Configuration simConfig = commons.config.Configuration.getInstance();
		String[] workloads = simConfig.getWorkloads();
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

	private long calcTotalDemand() {
		long totalDemand = 0;
		for(List<Summary> summaries : this.summaries.values()){
			for(Summary summary : summaries){
				totalDemand += summary.getTotalCpuHrs();
			}
		}
		
		return totalDemand;
	}

	private PlanningFitnessFunction createFitnessFunction(User[] cloudUsers, Provider[] cloudProviders) {
		return new PlanningFitnessFunction(this.summaries, cloudUsers, cloudProviders, this.types);
	}

	@Override
	public double getEstimatedProfit(int period) {
		return 0;
	}

	@Override
	public Map<MachineType, Integer> getPlan(User[] cloudUsers) {
		Map<MachineType, Integer> plan = new HashMap<MachineType, Integer>();
		Gene[] genes = this.bestChromosome.getGenes();
		int index = 0;
		
		for(MachineType type : this.types){
			plan.put(type, (Integer) genes[index++].getAllele());
		}
		System.out.println("CONFIG: "+genes[0].getAllele()+" "+genes[1].getAllele()+" "+genes[2].getAllele());
		System.out.println("BEST: "+bestChromosome.getFitnessValue());
		
//		try {
//			writer.close();
//		} catch (IOException e) {
//			throw new RuntimeException(e.getMessage());
//		}
		return plan;
	}
}
