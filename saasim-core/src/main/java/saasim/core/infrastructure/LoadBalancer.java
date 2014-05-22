package saasim.core.infrastructure;

import saasim.core.application.Request;

public interface LoadBalancer{
	
	/**
	 * Send request to {@link LoadBalancer} processing queue.
	 * @param request new {@link Request}
	 */
	void queue(Request request);

	/** 
	 * Registers a new {@link Machine} to this {@link LoadBalancer} according to {@link InstanceDescriptor}.
	 * @param machine TODO
	 * @param useStartUpDelay <code>true</code> if use start up delay
	 */
	void addMachine(InstanceDescriptor descriptor, Machine machine, boolean useStartUpDelay);

	/**
	 * Removes a specific {@link Machine} from this {@link LoadBalancer}.
	 */
	void removeMachine(InstanceDescriptor descriptor);

	/**
	 * Removes a specific {@link Machine} from this {@link LoadBalancer}.
	 * @param force <code>true</code> if use force
	 */
	void reconfigureMachine(InstanceDescriptor descriptor, boolean force);
}
