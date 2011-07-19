package provisioning;

import java.util.List;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.config.WorkloadParser;
import commons.sim.OneTierSimulator;
import commons.sim.Simulator;
import commons.sim.jeevent.JEEventScheduler;

/**
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
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
		
		WorkloadParser<List<Request>> parser = null;
		
		Simulator simulator = new OneTierSimulator(scheduler, monitor, parser);
		
		monitor.setDPS(dps);
		
		dps.setConfigurable(simulator);
		
		simulator.start();
	}

}
