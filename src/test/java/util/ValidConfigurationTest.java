package util;

import org.apache.commons.configuration.ConfigurationException;

import commons.config.Configuration;
import commons.config.PropertiesTesting;

/**
 * Super class of tests using a valid configuration file. Classes extending it should 
 * provide and implementation for {@link ValidConfigurationTest#getConfigurationFile()}.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class ValidConfigurationTest extends CleanConfigurationTest{

	protected static void buildFullConfiguration() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_SINGLE_WORKLOAD_FILE);
	}

	protected static void buildFullRanjanConfiguration() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_RANJAN_FILE);
	}
	
	protected static void buildUndefinedWorkloadIdiomConfiguration() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.INVALID_IDIOM_FILE);
	}
	
	protected void buildMultiFileGEISTFullConfiguration() throws ConfigurationException {
		Configuration.buildInstance(PropertiesTesting.VALID_MULTI_WORKLOAD_FILE);
	}
}
