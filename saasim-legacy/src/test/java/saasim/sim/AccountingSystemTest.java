/**
 * 
 */
package saasim.sim;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Test;

import saasim.cloud.Provider;
import saasim.cloud.User;
import saasim.cloud.utility.AccountingSystem;
import saasim.cloud.utility.UtilityResult;
import saasim.config.Configuration;
import saasim.sim.core.EventCheckpointer;
import saasim.util.ValidConfigurationTest;


/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class AccountingSystemTest extends ValidConfigurationTest {
	
	@AfterClass
	public static void tearDown(){
		EventCheckpointer.clear();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		EventCheckpointer.clear();
		buildFullConfiguration();
	}

	/**
	 * Test method for {@link saasim.cloud.utility.AccountingSystem#accountPartialUtility(long, User[], Provider[])}.
	 */
	@Test(expected=AssertionError.class)
	public void testAccountPartialUtility() {
		Configuration r = Configuration.getInstance();
		new AccountingSystem(new User[]{},new Provider[]{}).accountPartialUtility(0, null, null);
	}

	/**
	 * Test method for {@link saasim.cloud.utility.AccountingSystem#calculateUtility()}.
	 * Utility cost based on reservation plan.
	 */
	@Test
	public void testCalculateUtility() {
		Configuration r = Configuration.getInstance();
		UtilityResult utility = new AccountingSystem(new User[]{},new Provider[]{}).calculateUtility();
		assertEquals(-17692.5, utility.getUtility(), 0.0);
	}
}
