/**
 * 
 */
package provisioning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.cloud.Contract;
import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.cloud.UtilityResultEntry;
import commons.config.Configuration;
import commons.config.PropertiesTesting;
import commons.io.WorkloadParser;
import commons.sim.AccountingSystem;
import commons.sim.components.MachineDescriptor;
import commons.sim.util.SaaSAppProperties;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
@RunWith(PowerMockRunner.class)
public class DynamicProvisioningSystemTest {
	
	@Before
	public void setUp() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_FILE);
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#DynamicProvisioningSystem()}.
	 */
	@Test
	public void testDynamicProvisioningSystemWithValidConfigurationData() {
		assertNotNull(new DynamicProvisioningSystem());
	}
	
	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#DynamicProvisioningSystem()}.
	 */
	@Test
	@PrepareForTest(Configuration.class)
	public void testDynamicProvisioningSystemWithEmptyUsersAndProviders() {
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{});
		EasyMock.expect(config.getUsers()).andReturn(new User[]{});
		PowerMock.replayAll(config);
		
		assertNotNull(new DynamicProvisioningSystem());
		
		PowerMock.verifyAll();
	}
	
	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#DynamicProvisioningSystem()}.
	 */
	@Test(expected=NullPointerException.class)
	@PrepareForTest(Configuration.class)
	public void testDynamicProvisioningSystemWithNullProviders() {
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getProviders()).andReturn(null);
		EasyMock.expect(config.getUsers()).andReturn(new User[]{});
		PowerMock.replayAll(config);
		
		assertNotNull(new DynamicProvisioningSystem());
		
		PowerMock.verifyAll();
	}
	

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#DynamicProvisioningSystem()}.
	 */
	@Test(expected=NullPointerException.class)
	@PrepareForTest(Configuration.class)
	public void testDynamicProvisioningSystemWithNullUsers() {
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{});
		EasyMock.expect(config.getUsers()).andReturn(null);
		PowerMock.replayAll(config);
		
		assertNotNull(new DynamicProvisioningSystem());
		
		PowerMock.verifyAll();
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#registerConfigurable(provisioning.DynamicConfigurable)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testRegisterConfigurableWithMultipleServers() {
		Configuration.getInstance().setProperty(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER, "7");
		
		DynamicConfigurable configurable = EasyMock.createStrictMock(DynamicConfigurable.class);
		Capture<MachineDescriptor> [] descriptor = new Capture[7];
		for (int i = 0; i < descriptor.length; i++) {
			descriptor[i] = new Capture<MachineDescriptor>();
		}
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [0]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [1]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [2]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [3]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [4]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [5]), EasyMock.anyBoolean());
		configurable.addServer(EasyMock.anyInt(), EasyMock.capture(descriptor [6]), EasyMock.anyBoolean());
		configurable.setWorkloadParser(EasyMock.anyObject(WorkloadParser.class));
		
		EasyMock.replay(configurable);
		
		DynamicProvisioningSystem dps = new DynamicProvisioningSystem();
		dps.registerConfigurable(configurable);
		
		assertEquals(MachineType.MEDIUM, descriptor[0].getValue().getType());
		assertEquals(MachineType.MEDIUM, descriptor[1].getValue().getType());
		assertEquals(MachineType.MEDIUM, descriptor[2].getValue().getType());
		assertEquals(MachineType.MEDIUM, descriptor[3].getValue().getType());
		assertEquals(MachineType.LARGE, descriptor[4].getValue().getType());
		assertEquals(MachineType.LARGE, descriptor[5].getValue().getType());
		assertEquals(MachineType.LARGE, descriptor[6].getValue().getType());
		
		EasyMock.verify(configurable);
	}
	
	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#registerConfigurable(provisioning.DynamicConfigurable)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testRegisterConfigurableWithAnyServers() {
		Configuration.getInstance().setProperty(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER, "0");
		
		DynamicConfigurable configurable = EasyMock.createStrictMock(DynamicConfigurable.class);
		configurable.setWorkloadParser(EasyMock.isA(WorkloadParser.class));
		
		EasyMock.replay(configurable);
		
		DynamicProvisioningSystem dps = new DynamicProvisioningSystem();
		dps.registerConfigurable(configurable);
		
		EasyMock.verify(configurable);
	}
	
	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#calculateUtility()}.
	 */
	@Test
	@PrepareForTest(Configuration.class)
	public void testCalculateUtilityWithoutUsersAndProviders() {
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{});
		EasyMock.expect(config.getUsers()).andReturn(new User[]{});
		PowerMock.replayAll(config);

		DynamicProvisioningSystem dps = new DynamicProvisioningSystem();
		UtilityResult result = dps.calculateUtility();
		
		assertNotNull(result);
		assertEquals(0.0, result.getUtility(), 0.0);
	}
	
	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#calculateUtility()}.
	 */
	@Test
	public void testCalculateUtilityWithUsersAndProviders() {
		DynamicProvisioningSystem dps = new DynamicProvisioningSystem();
		UtilityResult result = dps.calculateUtility();
		
		UtilityResult currentResult = new UtilityResult(2, 3);
		for(User user : Configuration.getInstance().getUsers()){
			user.calculateOneTimeFees(currentResult);
		}
		for(Provider provider : Configuration.getInstance().getProviders()){
			provider.calculateUniqueCost(currentResult);
		}
		
		assertNotNull(result);
		assertEquals(currentResult.getUtility(), result.getUtility(), 0.0);
	}
	
	@Test
	@PrepareForTest(AccountingSystem.class)
	public void testChargeUsers() throws Exception{
		Provider[] providers = Configuration.getInstance().getProviders();
		User[] users = Configuration.getInstance().getUsers();

		UtilityResultEntry entry = PowerMock.createStrictMockAndExpectNew(UtilityResultEntry.class, 0L, users, providers);
		entry.addToReceipt(EasyMock.anyInt(), EasyMock.anyObject(String.class), EasyMock.anyLong(), 
				EasyMock.anyDouble(), EasyMock.anyLong(), EasyMock.anyDouble(), EasyMock.anyDouble());
		PowerMock.expectLastCall().times(2);
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
		
		DynamicProvisioningSystem dps = new DynamicProvisioningSystem();
		dps.chargeUsers(0);

		PowerMock.verifyAll();
	}
	
	@Test
	@PrepareForTest(Configuration.class)
	public void testReportLostRequest(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getSaasClient()).andReturn(0).times(2);
		
		Contract contract = EasyMock.createStrictMock(Contract.class);

		//Configuration mock
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{});
		
		User user = EasyMock.createStrictMock(User.class);
		user.reportLostRequest(request);
		User[] users = new User[1];
		users[0] = user;
		EasyMock.expect(config.getUsers()).andReturn(users);
		
		PowerMock.replayAll(request, contract, config, user);
		
		//Creating dps
		DynamicProvisioningSystem dps = new DynamicProvisioningSystem();
		dps.reportLostRequest(request);
		
		PowerMock.verifyAll();
	}
	
	@Test
	@PrepareForTest(Configuration.class)
	public void testReportFinishedRequest(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getSaasClient()).andReturn(0).times(2);
		
		Contract contract = EasyMock.createStrictMock(Contract.class);

		//Configuration mock
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{});
		
		User user = EasyMock.createStrictMock(User.class);
		user.reportFinishedRequest(request);
		User[] users = new User[1];
		users[0] = user;
		EasyMock.expect(config.getUsers()).andReturn(users);
		
		PowerMock.replayAll(request, contract, config, user);
		
		//Creating dps
		DynamicProvisioningSystem dps = new DynamicProvisioningSystem();
		dps.reportRequestFinished(request);
		
		PowerMock.verifyAll();
	}
}
