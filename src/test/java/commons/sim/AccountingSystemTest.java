/**
 * 
 */
package commons.sim;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.cloud.UtilityResult;
import commons.cloud.UtilityResultEntry;
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
	 * Test method for {@link commons.sim.AccountingSystem#accountPartialUtility(long)}.
	 * @throws Exception 
	 */
	@Test
	public void testAccountPartialUtility() throws Exception {
		Checkpointer.loadAccountingSystem().accountPartialUtility(0);
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#calculateUtility()}.
	 */
	@Ignore@Test
	public void testCalculateUtility() {
		UtilityResult utility = Checkpointer.loadAccountingSystem().calculateUtility();
		assertTrue(utility.iterator().hasNext());
		UtilityResultEntry entry = utility.iterator().next();
		assertEquals(0.0, utility.getUtility(), 0.0);
	}
}
