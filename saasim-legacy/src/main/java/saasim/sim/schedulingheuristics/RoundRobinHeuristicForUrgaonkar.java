/**
 * 
 */
package saasim.sim.schedulingheuristics;

import saasim.util.TimeUnit;


/**
 * Simple {@link SchedulingHeuristic} to choose servers in a Round Robin fashion.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class RoundRobinHeuristicForUrgaonkar extends RoundRobinHeuristic {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4279289006456262500L;
	private long predictionTickInMillis;
	
	/**
	 * Default constructor
	 */
	public RoundRobinHeuristicForUrgaonkar() {
		super();
		predictionTickInMillis = TimeUnit.HOUR.getMillis();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Statistics getStatistics(long eventTime) {
		
		Statistics statistics = tierStatistics;
		statistics.totalNumberOfActiveServers = getNumberOfMachines();
		
		if( eventTime % predictionTickInMillis == 0 ){
			resetCounters();
			tierStatistics = new Statistics();
		}else{
			tierStatistics = new Statistics(statistics);
		}
		
		return statistics;
	}

}
