/**
 * 
 */
package commons.sim.schedulingheuristics;

import commons.sim.provisioningheuristics.MachineStatistics;
import commons.util.TimeUnit;


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
	
	/**
	 * Default constructor
	 */
	public RoundRobinHeuristicForUrgaonkar() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public MachineStatistics getStatistics(long eventTime) {
		
		MachineStatistics statistics = tierStatistics;
		tierStatistics.totalNumberOfServers = getNumberOfMachines();
		
		if( eventTime % TimeUnit.HOUR.getMillis() == 0 ){
			resetCounters();
			tierStatistics = new MachineStatistics();//statistics);
		}
		
		
		return statistics;
	}

}
