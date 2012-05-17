package saasim.sim.util;

import saasim.config.Configuration;
import saasim.provisioning.DPS;
import saasim.provisioning.Monitor;
import saasim.sim.DynamicConfigurable;
import saasim.sim.components.LoadBalancer;
import saasim.sim.core.EventScheduler;

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
	 * Builds and gets the single instance of this factory.
	 * @return the single instance of {@link ApplicationFactory}.
	 */
	public static ApplicationFactory getInstance() {
		if(instance == null){
			String className = Configuration.getInstance().getString(SaaSAppProperties.APPLICATION_FACTORY);
			try {
				instance = (ApplicationFactory) Class.forName(className).newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Something went wrong when loading "+ className, e);
			}
		}
		return instance;
	}

	/**
	 * Build the application.
	 * @param scheduler {@link EventScheduler} to represent an event scheduler
	 * @param monitor 
	 * @return An array containing the {@link LoadBalancer}s in the builded application.
	 */
	@Deprecated
	public abstract LoadBalancer[] buildApplication(EventScheduler scheduler, Monitor monitor);

	public abstract DynamicConfigurable buildApplication(EventScheduler scheduler, DPS provisioningSystem);

	@Deprecated
	public static void reset() {
		// TODO Auto-generated method stub
	}
}
