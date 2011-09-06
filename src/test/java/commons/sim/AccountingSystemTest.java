/**
 * 
 */
package commons.sim;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.cloud.UtilityResultEntry;
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
	 * Test method for {@link commons.sim.AccountingSystem#accountPartialUtility(long)}.
	 */
	@Test
	public void testAccountPartialUtilityWithoutUsersAndProviders() {
		Map<String, User> users = new TreeMap<String, User>();
		Map<String, Provider> providers = new TreeMap<String, Provider>();
		
		AccountingSystem acc = new AccountingSystem();
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
	@PrepareForTest(AccountingSystem.class)
	@Test
	public void testAccountPartialUtility() throws Exception {
		List<Provider> providers = Configuration.getInstance().getProviders();
		List<User> users = Configuration.getInstance().getUsers();

		Map<String, User> usersMap = new TreeMap<String, User>();
		for (User user : users) {
			usersMap.put(user.getId(), user);
		}
		
		Map<String, Provider> providersMap = new TreeMap<String, Provider>();
		for (Provider provider : providers) {
			providersMap.put(provider.getName(), provider);
		}
		
		UtilityResultEntry entry = PowerMock.createStrictMockAndExpectNew(UtilityResultEntry.class, 0L, usersMap, providersMap);
		entry.addToReceipt(EasyMock.anyObject(String.class), EasyMock.anyObject(String.class), EasyMock.anyLong(), 
				EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyDouble());
		PowerMock.expectLastCall().times(2);
		//AMAZON
		entry.addUsageToCost(EasyMock.anyObject(String.class), EasyMock.anyObject(MachineType.class), 
				EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyDouble());
		PowerMock.expectLastCall().times(3);
		entry.addTransferenceToCost(EasyMock.anyObject(String.class), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble());
		//GOGRID
		entry.addUsageToCost(EasyMock.anyObject(String.class), EasyMock.anyObject(MachineType.class), 
				EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyDouble());
		PowerMock.expectLastCall().times(2);
		entry.addTransferenceToCost(EasyMock.anyObject(String.class), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble());
		//RACKSPACE
		entry.addUsageToCost(EasyMock.anyObject(String.class), EasyMock.anyObject(MachineType.class), 
				EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyDouble());
		PowerMock.expectLastCall().times(2);
		entry.addTransferenceToCost(EasyMock.anyObject(String.class), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble());
		PowerMock.replayAll();
		
		AccountingSystem acc = new AccountingSystem();
		acc.accountPartialUtility(0, usersMap, providersMap);

		PowerMock.verifyAll();
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#calculateUtility()}.
	 */
	@Test
	public void testCalculateUtilityWithoutUsersAndProviders() {
		Map<String, User> users = new TreeMap<String, User>();
		Map<String, Provider> providers = new TreeMap<String, Provider>();
		
		AccountingSystem acc = new AccountingSystem();
		UtilityResult utility = acc.calculateUtility(users, providers);
		assertFalse(utility.iterator().hasNext());
		assertEquals(0.0, utility.getUtility(), 0.0);
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#calculateUtility()}.
	 * @throws Exception 
	 */
	@PrepareForTest(AccountingSystem.class)
	@Test
	public void testCalculateUtility() throws Exception {
		
		List<Provider> providers = Configuration.getInstance().getProviders();
		List<User> users = Configuration.getInstance().getUsers();

		Map<String, User> usersMap = new TreeMap<String, User>();
		for (User user : users) {
			usersMap.put(user.getId(), user);
		}
		
		Map<String, Provider> providersMap = new TreeMap<String, Provider>();
		for (Provider provider : providers) {
			providersMap.put(provider.getName(), provider);
		}

		UtilityResult result = PowerMock.createStrictMockAndExpectNew(UtilityResult.class);
		result.addProviderUniqueCost(EasyMock.anyObject(String.class), EasyMock.anyObject(MachineType.class), EasyMock.anyDouble());
		PowerMock.expectLastCall().times(7);
		result.addUserUniqueFee(EasyMock.anyObject(String.class), EasyMock.anyDouble());
		PowerMock.expectLastCall().times(2);
		
		PowerMock.replayAll();

		AccountingSystem acc = new AccountingSystem();
		acc.calculateUtility(usersMap, providersMap);
		
		PowerMock.verifyAll();
	}

}
