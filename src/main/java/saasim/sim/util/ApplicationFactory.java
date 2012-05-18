package saasim.sim.util;

import saasim.config.AppArchitectureValues;
import saasim.config.Configuration;
import saasim.provisioning.DPS;
import saasim.sim.DynamicConfigurable;
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
	@Deprecated
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

	public static DynamicConfigurable buildApplication(EventScheduler scheduler,
			DPS dps) {
		
		try {
			return (DynamicConfigurable) Class.forName(AppArchitectureValues.MULTITIER.getClassName()).getConstructors()[0].newInstance(scheduler, dps);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
