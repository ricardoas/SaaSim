package planning.heuristic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.InvalidConfigurationException;
import org.jgap.impl.ChromosomePool;
import org.jgap.impl.CrossoverOperator;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;
import org.jgap.impl.MutationOperator;
import org.jgap.impl.StockRandomGenerator;
import org.jgap.impl.WeightedRouletteSelector;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;

public class AGHeuristic implements PlanningHeuristic{
	
	private int POPULATION_SIZE = 1000;
	private double CROSSOVER_RATE = 0.7;
	private int MUTATION_DENOMINATOR = 1000/5;//0.5%
	private double MINIMUM_IMPROVEMENT = 0.01;//1%
	private int MINIMUM_NUMBER_OF_EVOLUTIONS = 200;
	
	private int resourcesReservationLimit = 0;
	
	private List<IChromosome> bestConfigs = new ArrayList<IChromosome>();
	
	@Override
	public void findPlan(Map<User, List<Request>> currentWorkload,
			Map<String, Provider> cloudProvider, Map<User, Contract> cloudUsers, double sla) {
		
		initProperties(cloudProvider);
		
		//Configuring genetic algorithm
		Configuration config = new DefaultConfiguration();
		try {
			config.setPopulationSize(POPULATION_SIZE);
			config.setPreservFittestIndividual(true);
			config.setRandomGenerator(new StockRandomGenerator());
			config.setChromosomePool(new ChromosomePool());
			
			config.addGeneticOperator(new CrossoverOperator(config, CROSSOVER_RATE));
			config.addGeneticOperator(new MutationOperator(config, MUTATION_DENOMINATOR));
			config.removeNaturalSelectors(true);
			config.removeNaturalSelectors(false);
			config.addNaturalSelector(new WeightedRouletteSelector(), true);
	//		config.setKeepPopulationSizeConstant(true);
	//		config.setNaturalSelector(null);//Tournament, WeightedRoullete

			FitnessFunction myFunc = createFitnessFunction(currentWorkload, cloudUsers, sla, cloudProvider);
			config.setFitnessFunction(myFunc);
			
			IChromosome sampleChromosome = createSampleChromosome(config);
			config.setSampleChromosome(sampleChromosome);
			
			Genotype population = Genotype.randomInitialGenotype(config);

			//evaluating population
			for(int i = 0; i < MINIMUM_NUMBER_OF_EVOLUTIONS; i++){
				population.evolve();
			}
			IChromosome fittestChromosome = population.getFittestChromosome();
			population.evolve();
			IChromosome lastFittestChromosome = population.getFittestChromosome();
			while(!isEvolutionComplete(fittestChromosome, lastFittestChromosome)){
				population.evolve();
				fittestChromosome = lastFittestChromosome;
				lastFittestChromosome = population.getFittestChromosome();
			}
			
			//store best config
			this.bestConfigs.add(population.getFittestChromosome());
			
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	private boolean isEvolutionComplete(IChromosome fittestChromosome,
			IChromosome lastFittestChromosome) {
		double firstFitnessValue = fittestChromosome.getFitnessValue();
		double difference = lastFittestChromosome.getFitnessValue() - firstFitnessValue;
		if(Math.abs(difference/firstFitnessValue) < MINIMUM_IMPROVEMENT){
			return true;
		}
		return false;
	}

	private void initProperties(Map<String, Provider> cloudProvider) {
		resourcesReservationLimit = cloudProvider.values().iterator().next().reservationLimit;
	}

	private IChromosome createSampleChromosome(Configuration config) throws InvalidConfigurationException {
		Gene[] genes = new IntegerGene[1];
		genes[0] = new IntegerGene(config, 0, resourcesReservationLimit);
		IChromosome sampleChromosome = new Chromosome(config, genes);
		return sampleChromosome;
	}

	private FitnessFunction createFitnessFunction(Map<User, List<Request>> currentWorkload, Map<User, Contract> cloudUsers, double sla, Map<String, Provider> cloudProvider) {
		return new PlanningFitnessFunction(currentWorkload, cloudUsers, sla, cloudProvider);
	}

	@Override
	public double getEstimatedProfit(int period) {
		if(period > 0 && period < this.bestConfigs.size()){
			return this.bestConfigs.get(period).getFitnessValue();
		}
		
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public List getPlan() {
		List<Integer> amountsToReserve = new ArrayList<Integer>();
		for(IChromosome chrom : this.bestConfigs){
			amountsToReserve.add((Integer) chrom.getGene(0).getAllele());
		}
		return amountsToReserve;
	}
}
