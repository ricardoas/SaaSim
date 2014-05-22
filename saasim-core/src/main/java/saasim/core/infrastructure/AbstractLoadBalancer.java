package saasim.core.infrastructure;

import java.util.HashMap;
import java.util.Map;

import saasim.core.event.EventScheduler;

public abstract class AbstractLoadBalancer implements LoadBalancer{

	protected final Map<InstanceDescriptor, Machine> machines;
	protected MachineFactory machineFactory;

	public AbstractLoadBalancer(EventScheduler scheduler) {
		this.machines = new HashMap<>();
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.LoadBalancer#addMachine(saasim.core.cloud.InstanceDescriptor, Machine, boolean)
	 */
	@Override
	public void addMachine(final InstanceDescriptor descriptor, final Machine machine, boolean useStartUpDelay) {
		machines.put(descriptor, machine);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.LoadBalancer#removeMachine(saasim.ext.cloud.InstanceDescriptor)
	 */
	@Override
	public void removeMachine(InstanceDescriptor descriptor) {
		machines.remove(descriptor);
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
}