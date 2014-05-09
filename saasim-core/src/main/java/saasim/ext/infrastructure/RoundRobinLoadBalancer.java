package saasim.ext.infrastructure;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import saasim.core.application.Request;
import saasim.core.application.Response;
import saasim.core.event.Event;
import saasim.core.event.EventPriority;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.LoadBalancer;
import saasim.core.infrastructure.Machine;
import saasim.core.infrastructure.MachineFactory;

import com.google.inject.Inject;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class RoundRobinLoadBalancer implements LoadBalancer {
	
	private final EventScheduler scheduler;
	private final Map<InstanceDescriptor, Machine> machines;
	private final LinkedList<InstanceDescriptor> roundRobinQueue;
	
	private int roundRobinIndex;
	private MachineFactory machineFactory;

	/**
	 * Default constructor.
	 * @param scheduler {@link EventScheduler}
	 */
	@Inject
	public RoundRobinLoadBalancer(EventScheduler scheduler, MachineFactory machineFactory) {
		this.scheduler = scheduler;
		this.machineFactory = machineFactory;
		this.machines = new HashMap<>();
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


	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.LoadBalancer#addMachine(saasim.core.cloud.InstanceDescriptor, boolean)
	 */
	@Override
	public void addMachine(final InstanceDescriptor descriptor,
			boolean useStartUpDelay) {
		final Machine machine = machineFactory.create(descriptor);
		scheduler.queueEvent(new Event(useStartUpDelay?machine.getStartUpDelay():0L, EventPriority.HIGH) {
			@Override
			public void trigger() {
				machines.put(descriptor, machine);
				roundRobinQueue.add(descriptor);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.LoadBalancer#removeMachine(saasim.ext.cloud.InstanceDescriptor, boolean)
	 */
	@Override
	public void removeMachine(InstanceDescriptor descriptor, boolean force) {
		machines.remove(descriptor);
		
		int index = roundRobinQueue.indexOf(descriptor);
		if(index <= roundRobinIndex){
			roundRobinIndex--;
		}
		roundRobinQueue.remove(index);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.LoadBalancer#reconfigureMachine(saasim.ext.cloud.InstanceDescriptor, boolean)
	 */
	@Override
	public void reconfigureMachine(InstanceDescriptor descriptor, boolean force) {
		Machine machine = machines.remove(descriptor);
		machine.reconfigure(descriptor);
		machines.put(descriptor, machine);
		
		roundRobinQueue.set(roundRobinQueue.indexOf(descriptor), descriptor);
	}

	@Override
	public void processDone(Request request, Response response) {
		request.getResponseListener().processDone(request, null);
	}

	
	
	
	
	
	
	@Override
	public void registerDrop(Request request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverIsUp(InstanceDescriptor descriptor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverIsDown(InstanceDescriptor descriptor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestWasQueued(Request request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void collectStatistics(long now, long timeInterval,
			int numberOfRequests, int peakArrivalRate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reportRequestQueued(Request requestQueued) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reportRequestFinished(Request requestFinished) {
		// TODO Auto-generated method stub

	}

	@Override
	public void config(double threshold) {
		// TODO Auto-generated method stub

	}








}
