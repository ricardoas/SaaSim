package saasim.core.application;

import saasim.core.config.Configuration;
import saasim.core.config.SaaSAppProperties;

/**
 * Application assembler.
 *  
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class ApplicationFactory {
	
	/**
	 * Unique instance
	 */
	private static ApplicationFactory instance;
	
	/**
	 * Starts up factory.
	 * 
	 * @return {@link ApplicationFactory} instance ready to assemble.
	 */
	public static ApplicationFactory getInstance(Configuration config) {
		if(instance == null){
			String className = config.getString(SaaSAppProperties.APPLICATION_FACTORY);
			try {
				instance = (ApplicationFactory) Class.forName(className).newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Something went wrong when loading "+ className, e);
			}
		}
		return instance;
	}
	
	public abstract Application buildApplication();
}
