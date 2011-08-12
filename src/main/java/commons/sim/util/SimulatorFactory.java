package commons.sim.util;

import provisioning.Monitor;

import commons.sim.SimpleSimulator;
import commons.sim.Simulator;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimulatorFactory {
	
	/**
	 * Unique instance
	 */
	private static SimulatorFactory instance;
	
	/**
	 * Builds and gets the single instance of thsi factory.
	 * @return
	 */
	public static SimulatorFactory getInstance(){
		
		if(instance == null){
			instance = new SimulatorFactory();
		}
		return instance;
	}
	
	private SimulatorFactory() {
		
	}

	/**
	 * @param monitor TODO
	 * 
	 */
	public Simulator buildSimulator(Monitor monitor){
		return new SimpleSimulator(monitor);
	}
}
