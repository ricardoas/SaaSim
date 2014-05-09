package saasim.core.infrastructure;

import saasim.core.application.Request;

/**
 * Application monitor. Interface for reporting information.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Monitor{
	
	void requestArrived(Request request);
	
	void requestRejected(Request request);

	/**
	 * Report when a specific {@link Request} has been finished.
	 * @param requestFinished A {@link Request} finished.
	 */
	void requestFinished(Request requestFinished);

	/**
	 * Report when a specific {@link Request} was lost.
	 * TODO Report which tier was responsible for loosing this request.
	 * 
	 * @param request {@link Request} that was lost
	 */
	void requestFailedAtMachine(Request request, InstanceDescriptor descriptor);

	
	
	
	
	
	
	
	
	void requestRejectedAtLoadBalancer(Request request, int tier);
	
	void requestAcceptedAtLoadBalancer(Request request, int tier);

	Statistics collect(long now, long elapsedTime);
	
	void reset(int numberOfTiers);
	
	
	
	
	/********************************************/
	
	/**
	 * Report when a specific {@link Machine} was shutdown.
	 * @param machineDescriptor {@link InstanceDescriptor} turned off.
	 */
	@Deprecated void machineTurnedOff(InstanceDescriptor machineDescriptor);

	/**
	 * Report the calculated utility to users.
	 * @param currentTimeInMillis the current time in milliseconds
	 */
	@Deprecated void chargeUsers(long currentTimeInMillis);

	/**
	 * 
	 * @return
	 */
	@Deprecated boolean isOptimal();
}
