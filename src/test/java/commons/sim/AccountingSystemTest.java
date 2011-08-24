/**
 * 
 */
package commons.sim;

import static org.junit.Assert.fail;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.config.Configuration;
import commons.config.PropertiesTesting;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
@RunWith(PowerMockRunner.class)
public class AccountingSystemTest {
	
	@Before
	public void setUp() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_FILE);
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#setUpConfigurables(provisioning.DynamicConfigurable)}.
	 */
	@Test
	public void testSetUpConfigurables() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#buyMachine()}.
	 */
	@Test
	public void testBuyMachine() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#accountPartialUtility(long)}.
	 */
	@Test
	public void testCalculateUtility() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#calculateUtility()}.
	 */
	@Test
	public void testCalculateUniqueUtility() {
		fail("Not yet implemented");
	}

}
