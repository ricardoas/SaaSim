package saasim.core.infrastructure;

import saasim.core.application.Request;

public interface LoadBalancer {
	

	/**
	 * Add a new {@link InstanceDescriptor} to this tier.
	 * @param useStartUpDelay <code>true</code> if use start up delay
	 */
	void addMachine(InstanceDescriptor descriptor, boolean useStartUpDelay);

	void process(Request request);

	void registerDrop(Request request);

	void serverIsUp(InstanceDescriptor descriptor);

	void serverIsDown(InstanceDescriptor descriptor);

	/**
	 * {@inheritDoc}
	 */
	void requestWasQueued(Request request);

	/**
	 * This method is used to collect statistics of current running servers. Such statistics include: machine utilisation, number of
	 * requests that arrived, number of finished requests and current number of servers. 
	 * @param now the actual time
	 * @param timeInterval TODO the interval to collect statistics
	 * @param numberOfRequests total number of requests submitted to the system (A<sub>0</sub>)
	 * @param peakArrivalRate Peak arrival rate during last session
	 */
	void collectStatistics(long now, long timeInterval, int numberOfRequests,
			int peakArrivalRate);

	/**
	 * Report when a specific {@link Request} goes to queue.
	 * @param requestQueued {@link Request} queued
	 */
	void reportRequestQueued(Request requestQueued);

	/**
	 * Report when a specific {@link Request} has been finished.
	 * @param requestFinished the {@link Request} has been finished
	 */
	void reportRequestFinished(Request requestFinished);

	/**
	 * Remove a specific {@link Machine} to this tier.
	 * @param force <code>true</code> if use force
	 */
	void removeMachine(InstanceDescriptor descriptor, boolean force);

	void config(double threshold);

}
