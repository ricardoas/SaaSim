package saasim.core.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 */
public class ConfigurationTest {
	
	private static final String SINGLE_TENANT_SINGLE_TIER_CONFIGURATION = "src/test/resources/single_tenant_single_tier_configuration.properties";
	private static final String MULTI_TENANT_SINGLE_APPLICATION_SINGLE_TIER_CONFIGURATION = "src/test/resources/multi_tenant_single_application_single_tier_configuration.properties";
	private static final String MULTI_TENANT_MULTI_APPLICATION_SINGLE_TIER_CONFIGURATION = "src/test/resources/multi_tenant_multi_application_single_tier_configuration.properties";

	@BeforeClass
	public static void setUpBefore(){
		assert !new File("test.properties").exists() || new File("saasim.properties").delete(): "Unwanted file saasim.properties on root directory. Please delete it!";
	}

	@AfterClass
	public static void tearDownAfter(){
		assert !new File("test.properties").exists() || new File("saasim.properties").delete(): "Unwanted file saasim.properties on root directory. Please delete it!";
	}

	/**
	 * Test method for {@link saasim.core.config.Configuration#Configuration()}.
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationException.class)
	public void testConfigurationWithoutFile() throws ConfigurationException {
		new Configuration("test.properties");
	}

	/**
	 * Test method for {@link saasim.core.config.Configuration#Configuration()}.
	 * @throws ConfigurationException 
	 * @throws IOException 
	 */
	@Test
	public void testConfigurationWithEmptyFile() throws ConfigurationException, IOException {
		assert new File("test.properties").createNewFile(): "Could not create a file for testing purposes, verify permission.";
		new Configuration("test.properties");
		assert new File("test.properties").delete(): "Could not delete file for testing purposes, verify permission.";
	}
	
	/**
	 * Test method for {@link saasim.core.config.Configuration#Configuration()}.
	 * @throws ConfigurationException 
	 * @throws IOException 
	 */
	@Test
	public void testSingleTenantSingleTierConfiguration() throws ConfigurationException, IOException {
		new Configuration(SINGLE_TENANT_SINGLE_TIER_CONFIGURATION);
	}

	/**
	 * Test method for {@link saasim.core.config.Configuration#Configuration()}.
	 * @throws ConfigurationException 
	 * @throws IOException 
	 */
	@Test
	public void testMultiTenantSingleApplicationSingleTierConfiguration() throws ConfigurationException, IOException {
		new Configuration(MULTI_TENANT_SINGLE_APPLICATION_SINGLE_TIER_CONFIGURATION);
	}

	/**
	 * Test method for {@link saasim.core.config.Configuration#Configuration()}.
	 * @throws ConfigurationException 
	 * @throws IOException 
	 */
	@Test
	public void testMultiTenantMultiApplicationSingleTierConfiguration() throws ConfigurationException, IOException {
		new Configuration(MULTI_TENANT_MULTI_APPLICATION_SINGLE_TIER_CONFIGURATION);
	}

}
