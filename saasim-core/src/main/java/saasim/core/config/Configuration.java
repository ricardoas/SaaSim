package saasim.core.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import saasim.core.application.Application;
import saasim.core.cloud.IaaSProvider;
import saasim.core.provisioning.DPS;

public class Configuration extends PropertiesConfiguration{

	/**
	 * Default constructor
	 * 
	 * @param propertiesFilepath
	 * @throws ConfigurationException
	 */
	public Configuration(String propertiesFilepath) throws ConfigurationException {
		super(propertiesFilepath);
	}
	
	@SuppressWarnings("unchecked")
	public AbstractFactory<IaaSProvider> getIaaSProvidersFactory() throws ConfigurationException {
		
		return (AbstractFactory<IaaSProvider>) loadFactory(getString("iaas.factory"));
	}
	
	@SuppressWarnings("unchecked")
	public AbstractFactory<DPS> getDPSFactory() throws ConfigurationException {
		
		return (AbstractFactory<DPS>) loadFactory(getString("dps.factory"));
	}

	@SuppressWarnings("unchecked")
	public AbstractFactory<Application> getApplicationFactory() throws ConfigurationException {
		
		return (AbstractFactory<Application>) loadFactory(getString("application.factory"));
	}

	private Object loadFactory(String name) throws ConfigurationException{
		
		try {
			return Class.forName(name).newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			throw new ConfigurationException("Erro instantiating " + name, e);
		}
	}

}
