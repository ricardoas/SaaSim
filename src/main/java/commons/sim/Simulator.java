package commons.sim;

import provisioning.DynamicallyConfigurable;


/**
 * Defines simulator operations. All simulators are dynamically 
 * configurable entities.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Simulator extends DynamicallyConfigurable{
	
	/**
	 * Start simulation.
	 */
	void start();
}
