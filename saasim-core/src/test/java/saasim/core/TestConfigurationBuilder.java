package saasim.core;

import org.apache.commons.configuration.ConfigurationException;

import saasim.core.config.Configuration;


/**
 * Super class of tests using a valid configuration file. Classes extending it should 
 * provide and implementation for {@link TestConfigurationBuilder#getConfigurationFile()}.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public final class TestConfigurationBuilder{
	
	private static final String SCENARIO_01 = "src/test/resources/single_tenant_single_tier_configuration.properties";


	public static void buildConfiguration01() throws ConfigurationException{
		new Configuration(SCENARIO_01);
	}

//	public static void buildFullConfiguration() throws ConfigurationException{
//		Configuration.buildInstance(PropertiesTesting.VALID_SINGLE_WORKLOAD_FILE);
//	}
//
//	public static void buildFullConfigurationWithDifferentUsers() throws ConfigurationException{
//		Configuration.buildInstance(PropertiesTesting.VALID_DIFFERENT_USERS_FILE);
//	}
//
//	public static void buildFullRanjanConfiguration() throws ConfigurationException{
//		Configuration.buildInstance(PropertiesTesting.VALID_RANJAN_FILE);
//	}
//	
//	public static void buildUndefinedWorkloadIdiomConfiguration() throws ConfigurationException{
//		Configuration.buildInstance(PropertiesTesting.INVALID_IDIOM_FILE);
//	}
//	
//	public static void buildMultiFileGEISTFullConfiguration() throws ConfigurationException {
//		Configuration.buildInstance(PropertiesTesting.VALID_MULTI_WORKLOAD_FILE);
//	}
//	
//	public static void buildInvalidDPSConfiguration() throws ConfigurationException {
//		Configuration.buildInstance(PropertiesTesting.INVALID_DPS_FILE);
//	}
//	
//	public static void buildInvalidApplicationConfiguration() throws ConfigurationException {
//		Configuration.buildInstance(PropertiesTesting.INVALID_APP_FILE);
//	}
//	
//	public static void buildManyTiersApplicationConfiguration() throws ConfigurationException {
//		Configuration.buildInstance(PropertiesTesting.VALID_MANY_TIERS_APP_FILE);
//	}
//	
//	public static void buildTwoUsersConfiguration() throws ConfigurationException {
//		Configuration.buildInstance(PropertiesTesting.VALID_TWO_USERS_FILE);
//	}
	
	
}
