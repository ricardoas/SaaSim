package commons.sim.util;

import commons.config.Configuration;
import commons.sim.components.LoadBalancer;
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
	 * @param scheduler {@link JEEventScheduler} to represent an event scheduler
	 * @return An array containing the {@link LoadBalancer}s in the builded application.
	 */
	public abstract LoadBalancer[] buildApplication(JEEventScheduler scheduler);

	@Deprecated
	public static void reset() {
		// TODO Auto-generated method stub
	}
}
