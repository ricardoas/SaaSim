package saasim.sim.util;

import saasim.sim.SimpleSimulator;
import saasim.sim.Simulator;
import saasim.sim.jeevent.JEEventScheduler;

/**
 * This factory builds a {@link Simulator} of the application.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimulatorFactory {
	
	/**
	 * Builds a {@link Simulator}.
	 * @param scheduler {@link JEEventScheduler} to represent a event scheduler.
	 * @return {@link Simulator} builded.
	 */
	public static Simulator buildSimulator(JEEventScheduler scheduler){
		return new SimpleSimulator(scheduler, ApplicationFactory.getInstance().buildApplication(scheduler));
	}
}
