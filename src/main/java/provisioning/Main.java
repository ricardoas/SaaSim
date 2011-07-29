package provisioning;

import org.apache.commons.configuration.ConfigurationException;

import commons.config.SimulatorConfiguration;
import commons.sim.SimpleSimulator;

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
		
		new SimpleSimulator().start();
	}

}
