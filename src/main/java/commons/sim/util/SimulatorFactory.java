package commons.sim.util;


import commons.sim.SimpleSimulator;
import commons.sim.Simulator;
import commons.sim.jeevent.JEEventScheduler;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimulatorFactory {
	
	/**
	 * @param scheduler 
	 */
	public static Simulator buildSimulator(JEEventScheduler scheduler){
		return new SimpleSimulator(scheduler, ApplicationFactory.getInstance().buildApplication(scheduler));
	}
}
