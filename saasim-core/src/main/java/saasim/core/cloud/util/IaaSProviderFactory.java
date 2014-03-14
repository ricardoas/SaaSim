package saasim.core.cloud.util;

import saasim.core.cloud.IaaSProvider;
import saasim.core.config.Configuration;

public abstract class IaaSProviderFactory {
	
	public abstract IaaSProvider[] buildIaaSProviders();

	@SuppressWarnings("unchecked")
	public static <T extends IaaSProviderFactory> T getInstance(){
		try {
			return (T) Configuration.getInstance().getIaaSProviderFactoryClass().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
