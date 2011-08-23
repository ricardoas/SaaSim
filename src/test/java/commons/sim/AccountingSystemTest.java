/**
 * 
 */
package commons.sim;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import commons.cloud.Request;
import commons.cloud.User;
import commons.config.Configuration;
import commons.config.PropertiesTesting;
import commons.sim.components.MachineDescriptor;

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
	 * Test method for {@link commons.sim.AccountingSystem#reportFinishedRequest(commons.cloud.Request)}.
	 */
	@Test(expected=RuntimeException.class)
	public void testReportFinishedRequestWithUnregisteredUser() {
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getUserID()).andReturn("100").times(2);
		EasyMock.replay(request);
		
		AccountingSystem accounting = new AccountingSystem();
		accounting.reportFinishedRequest(request);

		EasyMock.verify(request);
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#reportLostRequest(commons.cloud.Request)}.
	 */
	@Test(expected=RuntimeException.class)
	public void testReportLostRequestWithUnregisteredUser() {
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getUserID()).andReturn("100").times(2);
		EasyMock.replay(request);
		
		AccountingSystem accounting = new AccountingSystem();
		accounting.reportLostRequest(request);

		EasyMock.verify(request);
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#reportMachineFinish(commons.sim.components.MachineDescriptor)}.
	 */
	@PrepareForTest(Configuration.class)
	@Test(expected=RuntimeException.class)
	public void testReportMachineFinishOfInexistentMachine() {
		MachineDescriptor descriptor = new MachineDescriptor(111, true, MachineType.SMALL);
		
		Provider provider = EasyMock.createStrictMock(Provider.class);
		EasyMock.expect(provider.shutdownMachine(descriptor)).andReturn(false);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		EasyMock.expect(config.getProviders()).andReturn(Arrays.asList(provider));
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		PowerMock.replayAll(provider, config);

		AccountingSystem accounting = new AccountingSystem();
		accounting.reportMachineFinish(descriptor);
		
		PowerMock.verifyAll();
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#reportMachineFinish(commons.sim.components.MachineDescriptor)}.
	 */
	@PrepareForTest(Configuration.class)
	@Test
	public void testReportMachineFinishOfExistentMachine() {
		MachineDescriptor descriptor = new MachineDescriptor(111, true, MachineType.SMALL);
		
		Provider provider = EasyMock.createStrictMock(Provider.class);
		EasyMock.expect(provider.shutdownMachine(descriptor)).andReturn(true);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		EasyMock.expect(config.getProviders()).andReturn(Arrays.asList(provider));
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		PowerMock.replayAll(provider, config);

		AccountingSystem accounting = new AccountingSystem();
		accounting.reportMachineFinish(descriptor);
		
		PowerMock.verifyAll();
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#setUpConfigurables(provisioning.DynamicConfigurable)}.
	 */
	@Test
	public void testSetUpConfigurables() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#canBuyMachine(MachineType, boolean)}.
	 */
	@Test
	public void testCanBuyMachineOfUnavailableType() {
		AccountingSystem accounting = new AccountingSystem();
		List<Provider> providers = accounting.canBuyMachine(MachineType.HIGHCPU, false);
		assertNotNull(providers);
		assertTrue(providers.isEmpty());
		providers = accounting.canBuyMachine(MachineType.HIGHCPU, true);
		assertNotNull(providers);
		assertTrue(providers.isEmpty());
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#canBuyMachine(MachineType, boolean)}.
	 */
	@Test
	public void testCanBuyOnDemandMachineOfAvailableType() {
		AccountingSystem accounting = new AccountingSystem();
		List<Provider> providers = accounting.canBuyMachine(MachineType.LARGE, false);
		assertNotNull(providers);
		assertFalse(providers.isEmpty());
		assertEquals(3, providers.size());
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#canBuyMachine(MachineType, boolean)}.
	 */
	@Test
	public void testCanBuyReservedMachineOfAvailableType() {
		AccountingSystem accounting = new AccountingSystem();
		List<Provider> providers = accounting.canBuyMachine(MachineType.LARGE, true);
		assertNotNull(providers);
		assertFalse(providers.isEmpty());
		assertEquals(1, providers.size());
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#buyMachine()}.
	 */
	@Test
	public void testBuyMachine() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#calculateUtility(long)}.
	 */
	@Test
	public void testCalculateUtility() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link commons.sim.AccountingSystem#calculateUniqueUtility(commons.cloud.UtilityResult)}.
	 */
	@Test
	public void testCalculateUniqueUtility() {
		fail("Not yet implemented");
	}

}
