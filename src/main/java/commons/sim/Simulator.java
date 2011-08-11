package commons.sim;

import provisioning.DynamicConfigurable;


/**
 * Defines simulator operations. All simulators are dynamically 
 * configurable entities.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Simulator extends DynamicConfigurable{
	
	/**
	 * Start simulation.
	 */
	void start();
}
