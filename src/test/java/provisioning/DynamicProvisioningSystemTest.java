/**
 * 
 */
package provisioning;

import static org.junit.Assert.fail;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Test;

import commons.config.Configuration;
import commons.config.PropertiesTesting;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class DynamicProvisioningSystemTest {
	
	@Before
	public void setUp() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_FILE);
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#DynamicProvisioningSystem()}.
	 */
	@Test
	public void testDynamicProvisioningSystem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#registerConfigurable(provisioning.DynamicConfigurable)}.
	 */
	@Test
	public void testRegisterConfigurable() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#calculateUtility()}.
	 */
	@Test
	public void testCalculateUtility() {
		fail("Not yet implemented");
	}

}
