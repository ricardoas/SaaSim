package provisioning;

import org.apache.commons.configuration.ConfigurationException;

import provisioning.util.DPSFactory;

import commons.cloud.UtilityResult;
import commons.config.Configuration;
import commons.sim.Simulator;
import commons.sim.util.SimulatorFactory;

/**
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Main {
	
	/**
	 * @param args
	 * @throws ConfigurationException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws ConfigurationException {
		
		System.out.println(System.currentTimeMillis());
		Configuration.buildInstance(args[0]);
		
		DPS dps = DPSFactory.createDPS();
		
		Simulator simulator = SimulatorFactory.buildSimulator(dps);
		
		dps.registerConfigurable(simulator);
		
		simulator.start();
		
		UtilityResult utilityResult = dps.calculateUtility();
		System.out.println(System.currentTimeMillis());
		System.out.println(utilityResult.getUtility());
		System.out.println();
		System.err.println(utilityResult);
	}

}
