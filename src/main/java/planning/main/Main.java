package planning.main;

import org.apache.commons.configuration.ConfigurationException;

import planning.Planner;
import provisioning.DPS;
import provisioning.util.DPSFactory;

import commons.config.Configuration;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEEventScheduler;

/**
 * This class is responsible for obtaining input parameters, from a configuration file, such as: workload, cloud provider
 * data (price, limits, etc.) and planning heuristics to be used!
 * @author davidcmm
 *
 */
public class Main {
	
	public static void main(String[] args) {
		if(args.length != 1){
			System.err.println("Configuration file is missing!");
			System.exit(1);
		}
		
		try {
			//Loading simulator configuration data
			Configuration.buildInstance(args[0]);
			Configuration.getInstance().enableParserError();
			
			JEEventScheduler scheduler = Configuration.getInstance().getScheduler();
			DPS dps = DPSFactory.createDPS();
			LoadBalancer[] loadBalancers = Configuration.getInstance().getApplication().getTiers();
			
			//Creating planner
			Planner planner = new Planner(scheduler, dps, loadBalancers, Configuration.getInstance().getProviders(), Configuration.getInstance().getUsers());
			planner.plan();
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
