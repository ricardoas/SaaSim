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

	protected static void buildConfiguration() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.SIMPLE_CONFIGURATION);
	}

	protected static void buildFullConfiguration() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_SINGLE_WORKLOAD_FILE);
	}

	protected static void buildFullConfigurationWithDifferentUsers() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_DIFFERENT_USERS_FILE);
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
	
	protected void buildInvalidDPSConfiguration() throws ConfigurationException {
		Configuration.buildInstance(PropertiesTesting.INVALID_DPS_FILE);
	}
	
	protected void buildInvalidApplicationConfiguration() throws ConfigurationException {
		Configuration.buildInstance(PropertiesTesting.INVALID_APP_FILE);
	}
	
	protected void buildManyTiersApplicationConfiguration() throws ConfigurationException {
		Configuration.buildInstance(PropertiesTesting.VALID_MANY_TIERS_APP_FILE);
	}
	
	protected void buildTwoUsersConfiguration() throws ConfigurationException {
		Configuration.buildInstance(PropertiesTesting.VALID_TWO_USERS_FILE);
	}
	
	
}
