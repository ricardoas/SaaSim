package saasim.sim.util;

import saasim.cloud.Provider;
import saasim.cloud.User;
import saasim.provisioning.DPS;
import saasim.provisioning.Monitor;
import saasim.provisioning.util.DPSFactory;
import saasim.sim.SimpleMultiTierApplication;
import saasim.sim.SimpleSimulator;
import saasim.sim.Simulator;
import saasim.sim.components.LoadBalancer;
import saasim.sim.core.EventScheduler;

/**
 * This factory builds a {@link Simulator} of the application.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimulatorFactory {
	
	/**
	 * Builds a {@link Simulator}.
	 * @param scheduler {@link EventScheduler} to represent a event scheduler.
	 * @param users TODO
	 * @param providers TODO
	 * @return {@link Simulator} builded.
	 */
	public static Simulator buildSimulator(EventScheduler scheduler, User[] users, Provider[] providers){
		DPS dps = DPSFactory.createDPS(users, providers);
		return new SimpleSimulator(scheduler, 
					dps, 
					new SimpleMultiTierApplication(scheduler, 
							dps, buildApplication(scheduler, dps)));
	}

	private static LoadBalancer[] buildApplication(EventScheduler scheduler, Monitor monitor) {
		return ApplicationFactory.getInstance().buildApplication(scheduler, monitor);
	}
}
