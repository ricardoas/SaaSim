package provisioning;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.BasicConfigurator;

import provisioning.util.DPSFactory;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.config.Configuration;
import commons.io.Checkpointer;
import commons.sim.Simulator;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.SimulatorFactory;
import commons.sim.util.SimulatorProperties;
import commons.util.SimulationInfo;

/**
 * Provisioning simulator execution entry point.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Main {
	
	/**
	 * Entry point.
	 * 
	 * @param args Path to the configuration file.
	 * @throws ConfigurationException Related to problems during configuration loading.
	 */
	public static void main(String[] args) throws ConfigurationException {
		
		BasicConfigurator.configure();
		
		Configuration.buildInstance(args[0]);
		
		DPS dps = DPSFactory.createDPS();
		
		Simulator simulator = SimulatorFactory.buildSimulator(JEEventScheduler.getInstance());
		
		dps.registerConfigurable(simulator);
		
		simulator.start();
		
		SimulationInfo info = Configuration.getInstance().getSimulationInfo();
		
		if(info.getSimulatedDays() 
				== Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD)){
			
			UtilityResult utilityResult = dps.calculateUtility();
			System.err.println(utilityResult);
			System.out.println(utilityResult.getUtility());
			Checkpointer.clear();
		}else{//Persisting dump
			User[] users = Configuration.getInstance().getUsers();
			Provider[] providers = Configuration.getInstance().getProviders();
			LoadBalancer[] application = simulator.getTiers();
			
			Checkpointer.save(info, users, providers, application);
		}
	}
}
