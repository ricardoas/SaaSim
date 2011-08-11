package provisioning;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;

import provisioning.util.DPSFactory;

import commons.cloud.UtilityResult;
import commons.config.SimulatorConfiguration;
import commons.sim.Simulator;
import commons.sim.jeevent.JEEventScheduler;
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
		
		SimulatorConfiguration.buildInstance(args[0]);
		
		DPS dps = DPSFactory.INSTANCE.createDPS();
		
		Simulator simulator = SimulatorFactory.getInstance().buildSimulator(new JEEventScheduler(), dps);
		
		dps.registerConfigurable(simulator);
		
		simulator.start();
		
		UtilityResult utilityResult = dps.calculateUtility();
		System.out.println(utilityResult.getResult());
		System.err.println(utilityResult);
	}

}
