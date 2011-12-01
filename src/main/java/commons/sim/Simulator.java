package commons.sim;

import commons.sim.components.LoadBalancer;

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

	/**
	 * Gets the tiers of simulation.
	 * @return The tiers of simulation.
	 */
	LoadBalancer[] getTiers();
}
