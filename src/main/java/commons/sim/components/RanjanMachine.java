package commons.sim.components;

import static commons.sim.util.SimulatorProperties.RANJAN_HEURISTIC_BACKLOG_SIZE;
import static commons.sim.util.SimulatorProperties.RANJAN_HEURISTIC_NUMBER_OF_TOKENS;

import commons.config.Configuration;
import commons.sim.jeevent.JEEventScheduler;

/**
 * This class represents a Machine/Server in the context of the Ranjan Provisionning Heuristic. In this 
 * context a machine uses a pool of threads to process requests and it also has a backlog queue that is 
 * used to store requests waiting for a thread.
 * 
 * @author David Candeia
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class RanjanMachine extends TimeSharedMachine {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7181645412704549035L;

	/**
	 * @see commons.sim.components.ProcessorSharedMachine
	 */
	public RanjanMachine(JEEventScheduler scheduler, MachineDescriptor descriptor, LoadBalancer loadBalancer){
		super(scheduler, descriptor, loadBalancer);
		this.maxThreads = Configuration.getInstance().getLong(RANJAN_HEURISTIC_NUMBER_OF_TOKENS);
		this.maxBacklogSize = Configuration.getInstance().getLong(RANJAN_HEURISTIC_BACKLOG_SIZE);
	}
}
