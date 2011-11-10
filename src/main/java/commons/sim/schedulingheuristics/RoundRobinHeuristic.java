/**
 * 
 */
package commons.sim.schedulingheuristics;

import commons.sim.components.Machine;

/**
 * Simple {@link SchedulingHeuristic} to choose servers in a Round Robin fashion.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class RoundRobinHeuristic extends AbstractSchedulingHeuristic {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4279289006456262500L;
	private int nextToUse;
	
	/**
	 * Default constructor
	 */
	public RoundRobinHeuristic() {
		super();
		this.nextToUse = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Machine getNextAvailableMachine() {
		int index = nextToUse++;
		return machines.isEmpty()? null: machines.get(index % machines.size());
	}
}
