package provisioning;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import provisioning.util.DPSFactory;

import commons.cloud.Request;
import commons.config.SimulatorConfiguration;
import commons.config.WorkloadParser;
import commons.sim.SimpleSimulator;
import commons.sim.Simulator;
import commons.sim.jeevent.JEEventScheduler;

import config.GEISTSimpleWorkloadParser;

/**
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Main {
	
	/**
	 * @param args
	 * @throws ConfigurationException 
	 */
	public static void main(String[] args) throws ConfigurationException {
		
		SimulatorConfiguration.buildInstance(args[0]);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		DPS dps = DPSFactory.INSTANCE.createDPS();
		
		WorkloadParser<List<Request>> parser = new GEISTSimpleWorkloadParser();
		
		Simulator simulator = new SimpleSimulator(scheduler, dps, parser, dps.getSetupMachines());
		
		simulator.start();
		
				
	}

}
