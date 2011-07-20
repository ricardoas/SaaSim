package provisioning;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import commons.cloud.Request;
import commons.config.SimulatorConfiguration;
import commons.config.WorkloadParser;
import commons.sim.SimpleSimulator;
import commons.sim.Simulator;
import commons.sim.jeevent.JEEventScheduler;
import config.GEISTSimpleWorkloadParser;

/**
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Main {
	
	/**
	 * @param args
	 * @throws ConfigurationException 
	 */
	public static void main(String[] args) throws ConfigurationException {
		
		SimulatorConfiguration.buildInstance(args[0]);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		Monitor monitor = null;
		
		DPS dps = null;
		
		WorkloadParser<List<Request>> parser = new GEISTSimpleWorkloadParser();
		
		Simulator simulator = new SimpleSimulator(scheduler, monitor, parser, dps.getSetupMachines());
		
		monitor.setDPS(dps);
		
		dps.setConfigurable(simulator);
		
		simulator.start();
	}

}
