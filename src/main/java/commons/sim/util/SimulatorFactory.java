package commons.sim.util;

import provisioning.Monitor;

import commons.sim.SimpleSimulator;
import commons.sim.Simulator;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimulatorFactory {
	
	/**
	 * @param monitor TODO
	 */
	public static Simulator buildSimulator(Monitor monitor){
		return new SimpleSimulator(monitor);
	}
}
