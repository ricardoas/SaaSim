package saasim.core.infrastructure;

import java.util.List;

import saasim.core.application.Request;

/**
 * Application monitor. Interface for reporting information.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Monitor{
	
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
	void requestFailed(Request request);

	List<Statistics> collectSamples();	
	
	/********************************************/
	
	/**
	 * Report when a specific {@link Machine} was shutdown.
	 * @param machineDescriptor {@link InstanceDescriptor} turned off.
	 */
	void machineTurnedOff(InstanceDescriptor machineDescriptor);

	/**
	 * Report the calculated utility to users.
	 * @param currentTimeInMillis the current time in milliseconds
	 */
	void chargeUsers(long currentTimeInMillis);

	/**
	 * 
	 * @return
	 */
	boolean isOptimal();


}
