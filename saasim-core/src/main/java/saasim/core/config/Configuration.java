package saasim.core.config;

import java.io.File;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

public class Configuration extends PropertiesConfiguration{

	public static final String TIER_ID = "default.tier.id";
	public static final String INSTANCE_DESCRIPTOR = "default.tier.descriptor";
	public static final String FORCE = "default.tier.force";
	public static final String ACTION =  "default.action";
	
	public static final String ACTION_INCREASE =  "default.action.increase";
	public static final String ACTION_DECREASE =  "default.action.decrease";
	public static final String ACTION_RECONFIGURE =  "default.action.reconfigure";
	public static final String ACTION_ADMISSION_CONTROL = "default.action.admissioncontrol";

	/**
	 * Default constructor
	 * 
	 * @param propertiesFilepath
	 * @throws ConfigurationException
	 */
	public Configuration(String propertiesFilepath) throws ConfigurationException {
		super(propertiesFilepath);
	}

	/**
	 * Default constructor
	 * 
	 * @param propertiesFilepath
	 * @throws ConfigurationException
	 */
	public Configuration(){
		super();
	}
	
	/**
	 * Default constructor
	 * @param file 
	 * 
	 * @param propertiesFilepath
	 * @throws ConfigurationException
	 */
	public Configuration(File file) throws ConfigurationException{
		super(file);
	}
	
	/**
	 * Default constructor
	 * 
	 * @param propertiesFilepath
	 * @throws ConfigurationException
	 */
	public Configuration(URL url) throws ConfigurationException{
		super(url);
	}
	
}
