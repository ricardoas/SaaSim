/**
 * 
 */
package commons.sim;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.io.Checkpointer;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class AccountingSystemTest extends ValidConfigurationTest {
	
	@AfterClass
	public static void tearDown(){
		Checkpointer.clear();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Checkpointer.clear();
		buildFullConfiguration();
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#accountPartialUtility(long, User[], Provider[])}.
	 */
	@Test(expected=AssertionError.class)
	public void testAccountPartialUtility() {
		Checkpointer.loadAccountingSystem().accountPartialUtility(0, null, null);
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#calculateUtility()}.
	 * Utility cost based on reservation plan.
	 */
	@Test
	public void testCalculateUtility() {
		UtilityResult utility = Checkpointer.loadAccountingSystem().calculateUtility();
		assertEquals(-17692.5, utility.getUtility(), 0.0);
	}
}
