package saasim.sim.util;

import saasim.sim.SimpleMultiTierApplication;
import saasim.sim.SimpleSimulator;
import saasim.sim.Simulator;
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
	 * @return {@link Simulator} builded.
	 */
	public static Simulator buildSimulator(EventScheduler scheduler){
		return new SimpleSimulator(scheduler, new SimpleMultiTierApplication(
				scheduler, ApplicationFactory.getInstance().buildApplication(
						scheduler)));
	}
}
