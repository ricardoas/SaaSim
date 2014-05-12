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

	Statistics collect(long now, long elapsedTime);
	
	void reset(int numberOfTiers);
	
}
