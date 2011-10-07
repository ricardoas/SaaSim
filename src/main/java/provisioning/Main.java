package provisioning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import provisioning.util.DPSFactory;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.config.Configuration;
import commons.io.Checkpointer;
import commons.sim.Simulator;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.SimulatorFactory;
import commons.sim.util.SimulatorProperties;

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
	 * @throws IOException Related to checkpoint phase.
	 */
	public static void main(String[] args) throws ConfigurationException, IOException {
		
		long currentTimeMillis = System.currentTimeMillis();
		System.out.println(currentTimeMillis);
		Configuration.buildInstance(args[0]);
		
		DPS dps = DPSFactory.createDPS();
		
		Simulator simulator = SimulatorFactory.buildSimulator(new JEEventScheduler(), dps);
		
		dps.registerConfigurable(simulator);
		
		simulator.start();
		
		if(Configuration.getInstance().getSimulationInfo().getSimulatedDays() 
				== Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD)){
			
			UtilityResult utilityResult = dps.calculateUtility();
			utilityResult.getUtility();
			long diff = System.currentTimeMillis() - currentTimeMillis;
			System.out.println("Time: " + diff);
			System.out.println(diff < 78614? "Faster :)" : "Slower :(");
			System.out.println(utilityResult);
			
		}else{//Persisting dump
			User[] users = Configuration.getInstance().getUsers();
			Provider[] providers = Configuration.getInstance().getProviders();
			
			LoadBalancer[] loadBalancers = simulator.getTiers();
			List<Machine> machines = new ArrayList<Machine>();
			for(LoadBalancer balancer : loadBalancers){
				machines.addAll(balancer.getServers());
			}
			
			Checkpointer.dumpObjects(Configuration.getInstance().getSimulationInfo(), users, providers, machines);
		}
	}
}
