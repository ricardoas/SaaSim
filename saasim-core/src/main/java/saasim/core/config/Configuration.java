package saasim.core.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Configuration extends PropertiesConfiguration{

	private static final String DEFAULT_CONFIG_FILEPATH = "saasim.properties";

	/**
	 * Default empty constructor
	 * @throws ConfigurationException 
	 */
	public Configuration() throws ConfigurationException {
		this(DEFAULT_CONFIG_FILEPATH);
	}
	
	/**
	 * @param propertiesFilepath
	 * @throws ConfigurationException
	 */
	public Configuration(String propertiesFilepath) throws ConfigurationException {
		super(propertiesFilepath);
	}
}
