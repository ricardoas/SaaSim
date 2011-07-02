package provisioning;

import commons.config.Configuration;
import commons.sim.Simulator;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class Main {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Configuration config;// = Configuration.INSTANCE;
		
		Monitor monitor = null;
		
		DPS dps = null;
		
		Simulator simulator = null;
		
		monitor.setDPS(dps);
		
		simulator.setMonitor(monitor);
		
		simulator.start();
	}

}
