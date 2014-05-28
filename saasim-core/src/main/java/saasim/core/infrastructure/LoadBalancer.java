package saasim.core.infrastructure;

import saasim.core.saas.Request;

public interface LoadBalancer{
	
	/**
	 * Send request to {@link LoadBalancer} processing queue.
	 * @param request new {@link Request}
	 */
	void queue(Request request);

	/** 
	 * Registers a new {@link Machine} to this {@link LoadBalancer} according to {@link InstanceDescriptor}.
	 * @param machine TODO
	 */
	void addMachine(Machine machine);

	/**
	 * Removes a specific {@link Machine} from this {@link LoadBalancer}.
	 * @param machine TODO
	 */
	void removeMachine(Machine machine);

	/**
	 * Updates {@link LoadBalancer} policy of changes in {@link Machine}'s configuration
	 */
	void reconfigureMachine(Machine machine);
}
