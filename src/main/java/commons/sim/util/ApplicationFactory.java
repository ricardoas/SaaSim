package commons.sim.util;

import static commons.sim.util.SimulatorProperties.APPLICATION_FACTORY;

import java.util.List;

import provisioning.Monitor;

import commons.config.SimulatorConfiguration;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEEventScheduler;

/**
 * Factory to encapsulate Application creation. An application is composed of tiers, each
 * represented by a load balancer linked to pool of servers.
 *  
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class ApplicationFactory {
	
	/**
	 * Unique instance
	 */
	private static ApplicationFactory instance;
	
	/**
	 * Builds and gets the single instance of thsi factory.
	 * @return
	 */
	public static ApplicationFactory getInstance(){
		
		if(instance == null){
			String className = SimulatorConfiguration.getInstance().getString(APPLICATION_FACTORY);
			try {
				instance = (ApplicationFactory) Class.forName(className).newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Something went wrong when loading "+ className, e);
			}
		}
		return instance;
	}

	/**
	 * 
	 * @param scheduler
	 * @param monitor
	 * @param setupMachines 
	 * @return
	 */
	public abstract List<LoadBalancer> createNewApplication(JEEventScheduler scheduler, Monitor monitor, List<Machine> setupMachines);
}
