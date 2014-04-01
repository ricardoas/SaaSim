package saasim.core.application;

import saasim.core.config.Configuration;
import saasim.core.config.SaaSAppProperties;

public abstract class AbstractFactory<T>{
	
	/**
	 * Unique instance
	 */
	private static AbstractFactory instance;
	
	/**
	 * Starts up factory.
	 * 
	 * @return {@link ApplicationFactory} instance ready to assemble.
	 */
	public static AbstractFactory getInstance(String factoryName) {
		if(instance == null){
			try {
				instance = (AbstractFactory) Class.forName(factoryName).newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Something went wrong when loading "+ factoryName, e);
			}
		}
		return instance;
	}


	public abstract T build();

}