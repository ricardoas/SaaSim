package provisioning;

import commons.config.Configuration;
import commons.sim.OneTierSimulator;
import commons.sim.Simulator;
import commons.sim.jeevent.JEEventScheduler;

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
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		Monitor monitor = null;
		
		DPS dps = null;
		
		Simulator simulator = new OneTierSimulator(scheduler);
		
		monitor.setDPS(dps);
		
		simulator.setMonitor(monitor);
		
		dps.setConfigurable(simulator);
		
		simulator.start();
	}

}
