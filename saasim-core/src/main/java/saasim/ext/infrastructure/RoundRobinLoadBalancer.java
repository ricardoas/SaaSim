package saasim.ext.infrastructure;

import java.util.LinkedList;

import saasim.core.application.Request;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.AbstractLoadBalancer;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Machine;

import com.google.inject.Inject;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {
	
	private final LinkedList<InstanceDescriptor> roundRobinQueue;
	private int roundRobinIndex;

	/**
	 * Default constructor.
	 * @param scheduler {@link EventScheduler}
	 */
	@Inject
	public RoundRobinLoadBalancer(EventScheduler scheduler) {
		super(scheduler);
		this.roundRobinQueue = new LinkedList<InstanceDescriptor>();
		this.roundRobinIndex = -1;
	}
	

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.LoadBalancer#queue(saasim.core.application.Request)
	 */
	@Override
	public void queue(Request request) {
		machines.get(getNextAvailableMachine()).queue(request);
	}
	
	private InstanceDescriptor getNextAvailableMachine() {
		assert !roundRobinQueue.isEmpty(): "There's no machine registered in this load balancer";
		
		roundRobinIndex = (++roundRobinIndex) % machines.size();
		return roundRobinQueue.get(roundRobinIndex);
	}
	
	@Override
	public void addMachine(InstanceDescriptor descriptor, Machine machine,
			boolean useStartUpDelay) {
		super.addMachine(descriptor, machine, useStartUpDelay);
		roundRobinQueue.add(descriptor);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.LoadBalancer#removeMachine(saasim.ext.cloud.InstanceDescriptor)
	 */
	@Override
	public void removeMachine(InstanceDescriptor descriptor) {
		int index = roundRobinQueue.indexOf(descriptor);
		if(index <= roundRobinIndex){
			roundRobinIndex--;
		}
		roundRobinQueue.remove(index);
		super.removeMachine(descriptor);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.LoadBalancer#reconfigureMachine(saasim.ext.cloud.InstanceDescriptor, boolean)
	 */
	@Override
	public void reconfigureMachine(InstanceDescriptor descriptor, boolean force) {
		super.reconfigureMachine(descriptor, force);
		
		roundRobinQueue.set(roundRobinQueue.indexOf(descriptor), descriptor);
	}

}
