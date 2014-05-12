package saasim.core.infrastructure;

import saasim.core.application.Request;
import saasim.core.application.ResponseListener;

public interface LoadBalancer extends ResponseListener{
	
	/**
	 * Send request to {@link LoadBalancer} processing queue.
	 * @param request new {@link Request}
	 */
	void queue(Request request);

	/** 
	 * Registers a new {@link Machine} to this {@link LoadBalancer} according to {@link InstanceDescriptor}.
	 * @param useStartUpDelay <code>true</code> if use start up delay
	 */
	void addMachine(InstanceDescriptor descriptor, boolean useStartUpDelay);

	/**
	 * Removes a specific {@link Machine} from this {@link LoadBalancer}.
	 * @param force <code>true</code> if use force
	 */
	void removeMachine(InstanceDescriptor descriptor, boolean force);

	/**
	 * Removes a specific {@link Machine} from this {@link LoadBalancer}.
	 * @param force <code>true</code> if use force
	 */
	void reconfigureMachine(InstanceDescriptor descriptor, boolean force);
}
