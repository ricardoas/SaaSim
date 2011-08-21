package commons.config;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.junit.Assert;
import org.junit.Test;

import commons.cloud.Provider;

public class ProviderInputTest {
	
	/**
	 * This test verifies an invalid file with transfer data missing.
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationRuntimeException.class)
	public void testMandatoryPropertyWithEmptyValue() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_1);
	}
	
	/**
	 * This test verifies an invalid file with number of providers missing.
	 * @throws ConfigurationException 
	 */
	@Test(expected=NumberFormatException.class)
	public void testMandatoryPropertyWithNonIntegerValue() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_2);
	}
	
	/**
	 * This test verifies an invalid file with a wrong number of providers.
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationRuntimeException.class)
	public void testArrayPropertyWithWrongSize() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_3);
	}
	
	/**
	 * This test verifies an invalid file with a wrong number of providers.
	 * @throws ConfigurationException 
	 */
	@Test(expected=NumberFormatException.class)
	public void testArrayPropertyWithWrongType() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_4);
	}
	
	/**
	 * This test verifies the error thrown when an inexistent file is read.
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationException.class)
	public void testInexistentFile() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.INEXISTENT_CONFIG_FILE);
	}
	
}
