package commons.config;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.junit.Assert;
import org.junit.Test;

import commons.cloud.Contract;
import commons.cloud.User;

public class ContractInputTest{
	
	/**
	 * This test verifies that a contracts file missing user associations with SaaS plans is not valid.
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationRuntimeException.class)
	public void testMandatoryPropertyWithEmptyValue() throws ConfigurationException{
			Configuration.buildInstance(PropertiesTesting.WRONG_PLANS_FILE_1);
	}
	
	/**
	 * This test verifies that a contracts file missing SaaS plans specifications is not valid.
	 * @throws ConfigurationException 
	 */
	@Test(expected=NumberFormatException.class)
	public void testMandatoryPropertyWithNonIntegerValue() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PLANS_FILE_2);
	}
	
	/**
	 * This test verifies that the number of contracts declared is invalid
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationRuntimeException.class)
	public void testArrayPropertyWithWrongSize() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PLANS_FILE_3);
	}
	
	/**
	 * This test verifies that the number of contracts declared is invalid
	 * @throws ConfigurationException 
	 */
	@Test(expected=NumberFormatException.class)
	public void testArrayPropertyWithWrongType() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PLANS_FILE_4);
	}
	
	/**
	 * This test verifies that an error is thrown while trying to read a missing file.
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationException.class)
	public void testInexistentFile() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.INEXISTENT_CONFIG_FILE);
	}
	
}
