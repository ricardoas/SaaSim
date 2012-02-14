/**
 * 
 */
package saasim.sim.schedulingheuristics;

import saasim.sim.provisioningheuristics.MachineStatistics;
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
	public MachineStatistics getStatistics(long eventTime) {
		
		MachineStatistics statistics = tierStatistics;
		statistics.totalNumberOfServers = getNumberOfMachines();
		
		if( eventTime % predictionTickInMillis == 0 ){
			resetCounters();
			tierStatistics = new MachineStatistics();
		}else{
			tierStatistics = new MachineStatistics(statistics);
		}
		
		return statistics;
	}

}
