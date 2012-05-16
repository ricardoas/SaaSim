package saasim.sim;

import saasim.util.SimulationInfo;



/**
 * Defines simulator operations. All simulators are dynamically 
 * configurable entities.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Simulator{
	
	/**
	 * Start simulation.
	 */
	void start();

	/**
	 * 
	 * @return All simulated applications.
	 */
	DynamicConfigurable [] getApplications();

	void restore();

	SimulationInfo getSimulationInfo();
}
