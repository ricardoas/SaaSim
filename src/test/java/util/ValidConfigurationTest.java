package util;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;

import commons.config.Configuration;

public abstract class ValidConfigurationTest {

	@Before
	public void setUp() throws ConfigurationException {
		Configuration.buildInstance(getConfigurationFile());
	}
	
	public abstract String getConfigurationFile();
}
