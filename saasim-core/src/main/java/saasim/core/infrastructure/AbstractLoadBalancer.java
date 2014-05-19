package saasim.core.infrastructure;

import java.util.HashMap;
import java.util.Map;

import saasim.core.application.Request;
import saasim.core.application.Response;
import saasim.core.event.Event;
import saasim.core.event.EventPriority;
import saasim.core.event.EventScheduler;

public abstract class AbstractLoadBalancer implements LoadBalancer{

	protected final EventScheduler scheduler;
	protected final Map<InstanceDescriptor, Machine> machines;
	protected MachineFactory machineFactory;

	public AbstractLoadBalancer(EventScheduler scheduler, MonitoringService monitor, MachineFactory machineFactory) {
		this.scheduler = scheduler;
		this.machineFactory = machineFactory;
		this.machines = new HashMap<>();
		
		monitor.setMonitorable(this);
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
				machineTurnedOn(descriptor, machine);
			}
		});
	}


	protected abstract void machineTurnedOn(InstanceDescriptor descriptor, Machine machine);
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.LoadBalancer#removeMachine(saasim.ext.cloud.InstanceDescriptor, boolean)
	 */
	@Override
	public void removeMachine(InstanceDescriptor descriptor, boolean force) {
		if(force){
			machines.remove(descriptor);
		}else{
			//TODO wait until session is over
		}
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
	}

	@Override
	public void processDone(Request request, Response response) {
		request.getResponseListener().processDone(request, response);
	}
	
	@Override
	public Map<String, Double> collect(long now, long elapsedTime) {
		return new HashMap<String, Double>();
	}

}