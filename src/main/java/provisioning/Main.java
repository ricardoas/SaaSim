package provisioning;

import java.io.IOException;

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
	 * @throws IOException 
	 */
	public static void main(String[] args) throws ConfigurationException, IOException {
		
		Configuration.buildInstance(args[0]);
		
		DPS dps = DPSFactory.INSTANCE.createDPS();
		
		Simulator simulator = SimulatorFactory.getInstance().buildSimulator(dps);
		
		dps.registerConfigurable(simulator);
		
		simulator.start();
		
		UtilityResult utilityResult = dps.calculateUtility();
		System.out.println(utilityResult.getResult());
		System.err.println(utilityResult);
	}

}
