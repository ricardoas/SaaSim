package saasim.planning.main;

import org.apache.commons.configuration.ConfigurationException;

import saasim.config.Configuration;
import saasim.planning.Planner;
import saasim.provisioning.DPS;
import saasim.provisioning.util.DPSFactory;
import saasim.sim.components.LoadBalancer;
import saasim.sim.core.EventScheduler;


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
			
			EventScheduler scheduler = Configuration.getInstance().getScheduler();
			DPS dps = DPSFactory.createDPS();
			LoadBalancer[] loadBalancers = Configuration.getInstance().getSimulator().getApplications()[0].getTiers();
			
			//Creating planner
			Planner planner = new Planner(scheduler, dps, loadBalancers, Configuration.getInstance().getProviders(), Configuration.getInstance().getUsers());
			planner.plan();
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
