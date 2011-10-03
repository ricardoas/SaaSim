package commons.sim.util;

import provisioning.DPS;
import provisioning.Monitor;

import commons.sim.SimpleSimulator;
import commons.sim.Simulator;
import commons.sim.jeevent.JEEventScheduler;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimulatorFactory {
	
	/**
	 * @param scheduler 
	 * @param monitor {@link Monitor} to collect information of this simulator. 
	 * Such information is important to {@link DPS}. 
	 */
	public static Simulator buildSimulator(JEEventScheduler scheduler, Monitor monitor){
		return new SimpleSimulator(scheduler, monitor, ApplicationFactory.getInstance().createNewApplication(scheduler, monitor));
	}
}
