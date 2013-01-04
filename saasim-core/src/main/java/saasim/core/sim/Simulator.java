package saasim.core.sim;

import saasim.core.application.DynamicallyConfigurable;
import saasim.core.event.EventHandler;

/**
 * Defines simulator operations. A simulator is a composite of:
 * <br>
 * <ol>
 * <li> One or more Dyna</li>
 * </ol>
 * 
 * 
 * All applications are dynamically 
 * configurable entities.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Simulator extends EventHandler{
	
	/**
	 * Start simulation.
	 */
	void start();

	/**
	 * 
	 * @return All simulated applications.
	 */
	DynamicallyConfigurable [] getApplications();
}
