/**
 * 
 */
package saasim.sim;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Test;

import saasim.cloud.Provider;
import saasim.cloud.User;
import saasim.cloud.UtilityResult;
import saasim.config.Configuration;
import saasim.sim.jeevent.JECheckpointer;
import saasim.util.ValidConfigurationTest;


/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class AccountingSystemTest extends ValidConfigurationTest {
	
	@AfterClass
	public static void tearDown(){
		JECheckpointer.clear();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		JECheckpointer.clear();
		buildFullConfiguration();
	}

	/**
	 * Test method for {@link saasim.sim.AccountingSystem#accountPartialUtility(long, User[], Provider[])}.
	 */
	@Test(expected=AssertionError.class)
	public void testAccountPartialUtility() {
		Configuration.getInstance().getAccountingSystem().accountPartialUtility(0, null, null);
	}

	/**
	 * Test method for {@link saasim.sim.AccountingSystem#calculateUtility()}.
	 * Utility cost based on reservation plan.
	 */
	@Test
	public void testCalculateUtility() {
		UtilityResult utility = Configuration.getInstance().getAccountingSystem().calculateUtility();
		assertEquals(-17692.5, utility.getUtility(), 0.0);
	}
}
