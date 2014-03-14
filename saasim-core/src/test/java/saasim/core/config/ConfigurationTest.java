package saasim.core.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 */
public class ConfigurationTest {
	
	@BeforeClass
	public static void setUpBefore(){
		assert !new File("saasim.properties").exists() || new File("saasim.properties").delete(): "Unwanted file saasim.properties on root directory. Please delete it!";
	}

	/**
	 * Test method for {@link saasim.core.config.Configuration#Configuration()}.
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationException.class)
	public void testConfigurationWithoutFile() throws ConfigurationException {
		new Configuration();
	}

	/**
	 * Test method for {@link saasim.core.config.Configuration#Configuration()}.
	 * @throws ConfigurationException 
	 * @throws IOException 
	 */
	@Test
	public void testConfigurationWithEmptyFile() throws ConfigurationException, IOException {
		assert new File("saasim.properties").createNewFile(): "Could not create a file for testing purposes, verify permission.";
		new Configuration();
		assert new File("saasim.properties").delete(): "Could not delete file for testing purposes, verify permission.";
	}

	/**
	 * Test method for {@link saasim.core.config.Configuration#Configuration(java.lang.String)}.
	 * @throws IOException 
	 * @throws ConfigurationException 
	 */
	@Test
	public void testConfigurationString() throws IOException, ConfigurationException {
		assert new File("saasim.config").createNewFile(): "Could not create a file for testing purposes, verify permission.";
		new Configuration("saasim.config");
		assert new File("saasim.config").delete(): "Could not delete file for testing purposes, verify permission.";
	}

}
