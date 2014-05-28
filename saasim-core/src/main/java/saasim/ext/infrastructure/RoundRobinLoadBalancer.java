package saasim.ext.infrastructure;

import java.util.Deque;
import java.util.LinkedList;

import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.LoadBalancer;
import saasim.core.infrastructure.Machine;
import saasim.core.saas.Request;

import com.google.inject.Inject;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
	
	private final Deque<Machine> roundRobinQueue;

	/**
	 * Default constructor.
	 * @param scheduler {@link EventScheduler}
	 */
	@Inject
	public RoundRobinLoadBalancer(EventScheduler scheduler) {
		this.roundRobinQueue = new LinkedList<>();
	}
	

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.LoadBalancer#queue(saasim.core.saas.Request)
	 */
	public void queue(Request request) {
		getNextAvailableMachine().queue(request);
	}
	
	private Machine getNextAvailableMachine() {
		assert !roundRobinQueue.isEmpty(): "There's no machine registered in this load balancer";
		
		Machine machine = roundRobinQueue.poll();
		roundRobinQueue.addLast(machine);
		return machine;
	}
	
	public void addMachine(Machine machine) {
		roundRobinQueue.addLast(machine);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.LoadBalancer#removeMachine(Machine)
	 */
	public void removeMachine(Machine machine) {
		roundRobinQueue.remove(machine);
	}


	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.LoadBalancer#reconfigureMachine(Machine)
	 */
	@Override
	public void reconfigureMachine(Machine machine) {
		// Nothing to do in this case. Still a simple round robin.
	}
}
