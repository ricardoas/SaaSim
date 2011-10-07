/**
 * 
 */
package commons.sim;

import static org.junit.Assert.*;

import java.io.File;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import planning.util.PlanIOHandler;
import util.ValidConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.cloud.UtilityResultEntry;
import commons.config.Configuration;
import commons.io.Checkpointer;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(AccountingSystem.class)
public class AccountingSystemTest extends ValidConfigurationTest {
	
	private void cleanDumpFiles() {
		new File(Checkpointer.MACHINE_DATA_DUMP).delete();
		new File(Checkpointer.MACHINES_DUMP).delete();
		new File(Checkpointer.PROVIDERS_DUMP).delete();
		new File(Checkpointer.SIMULATION_DUMP).delete();
		new File(Checkpointer.USERS_DUMP).delete();
		new File(PlanIOHandler.NUMBER_OF_MACHINES_FILE).delete();
	}
	
	@After
	public void tearDown(){
		cleanDumpFiles();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		cleanDumpFiles();
		buildFullConfiguration();
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#accountPartialUtility(long)}.
	 */
	@Test
	public void testAccountPartialUtilityWithoutUsersAndProviders() {
		User [] users = new User[]{};
		Provider[] providers = new Provider[]{};
		
		AccountingSystem acc = new AccountingSystem(0, 0);
		acc.accountPartialUtility(0, users, providers);
		
		UtilityResult result = acc.calculateUtility(users, providers);
		assertTrue(result.iterator().hasNext());
		UtilityResultEntry entry = result.iterator().next();
		assertEquals(0.0, entry.getUtility(), 0.0);
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#accountPartialUtility(long)}.
	 * @throws Exception 
	 */
	@Test
	public void testAccountPartialUtility() throws Exception {
		Provider[] providers = Configuration.getInstance().getProviders();
		User[] users = Configuration.getInstance().getUsers();

		UtilityResultEntry entry = PowerMock.createStrictMockAndExpectNew(UtilityResultEntry.class, 0L, users, providers);
		entry.addPenalty(0.0);
		entry.addToReceipt(EasyMock.anyInt(), EasyMock.anyObject(String.class), EasyMock.anyLong(), 
				EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyDouble());
		entry.addPenalty(0.0);
		entry.addToReceipt(EasyMock.anyInt(), EasyMock.anyObject(String.class), EasyMock.anyLong(), 
				EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyDouble());
		//RACKSPACE
		entry.addUsageToCost(EasyMock.anyInt(), EasyMock.anyObject(MachineType.class), 
				EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyDouble());
		PowerMock.expectLastCall().times(2);
		entry.addTransferenceToCost(EasyMock.anyInt(), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble());
		//AMAZON
		entry.addUsageToCost(EasyMock.anyInt(), EasyMock.anyObject(MachineType.class), 
				EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyDouble());
		PowerMock.expectLastCall().times(3);
		entry.addTransferenceToCost(EasyMock.anyInt(), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble());
		//GOGRID
		entry.addUsageToCost(EasyMock.anyInt(), EasyMock.anyObject(MachineType.class), 
				EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyDouble());
		PowerMock.expectLastCall().times(2);
		entry.addTransferenceToCost(EasyMock.anyInt(), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble());
		PowerMock.replayAll();
		
		AccountingSystem acc = new AccountingSystem(2, 3);
		acc.accountPartialUtility(0, users, providers);

		PowerMock.verifyAll();
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#calculateUtility()}.
	 */
	@Test
	public void testCalculateUtilityWithoutUsersAndProviders() {
		User [] users = new User[]{};
		Provider[] providers = new Provider[]{};
		
		AccountingSystem acc = new AccountingSystem(0, 0);
		UtilityResult utility = acc.calculateUtility(users, providers);
		assertFalse(utility.iterator().hasNext());
		assertEquals(0.0, utility.getUtility(), 0.0);
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#calculateUtility()}.
	 * @throws Exception 
	 */
	@Test
	public void testCalculateUtility() throws Exception {
		
		Provider[] providers = Configuration.getInstance().getProviders();
		User[] users = Configuration.getInstance().getUsers();

		UtilityResult result = PowerMock.createStrictMockAndExpectNew(UtilityResult.class, 2, 3);
		result.addProviderUniqueCost(EasyMock.anyInt(), EasyMock.anyObject(MachineType.class), EasyMock.anyDouble());
		PowerMock.expectLastCall().times(7);
		result.addUserUniqueFee(EasyMock.anyInt(), EasyMock.anyDouble());
		PowerMock.expectLastCall().times(2);
		
		PowerMock.replayAll();

		AccountingSystem acc = new AccountingSystem(2, 3);
		acc.calculateUtility(users, providers);
		
		PowerMock.verifyAll();
	}

}
