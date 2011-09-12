/**
 * 
 */
package provisioning;

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
import commons.cloud.UtilityResultEntry;
import commons.config.Configuration;
import commons.config.PropertiesTesting;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.MachineStatistics;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
@RunWith(PowerMockRunner.class)
public class MonitorTest {

	private Monitor monitor;

	@Before
	public void setUp() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_FILE);
		monitor = new DynamicProvisioningSystem();
	}

	/**
	 * Test method for {@link provisioning.Monitor#reportRequestFinished(commons.cloud.Request)}.
	 */
	@Test(expected=AssertionError.class)
	public void testReportRequestFinished() {
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getSaasClient()).andReturn(100).times(2);
		EasyMock.replay(request);
		
		monitor.reportRequestFinished(request);
		
		EasyMock.verify(request);
	}

	/**
	 * Test method for {@link provisioning.Monitor#requestQueued(long, commons.cloud.Request, int)}.
	 */
	@Test
	public void testRequestQueued() {
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getSaasClient()).andReturn(0).times(2);
		EasyMock.expect(request.getTotalProcessed()).andReturn(0l);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(0l);
		EasyMock.replay(request);
		
		monitor.requestQueued(0, request, 0);
		
		EasyMock.verify(request);
	}

	/**
	 * Test method for {@link provisioning.Monitor#sendStatistics(long, commons.sim.provisioningheuristics.MachineStatistics, int)}.
	 */
	@Test
	public void testEvaluateUtilisation() {
		MachineStatistics statistics = EasyMock.createStrictMock(MachineStatistics.class);
		EasyMock.replay(statistics);
		
		monitor.sendStatistics(1000, statistics, 0);
		
		EasyMock.verify(statistics);
	}

	/**
	 * Test method for {@link provisioning.Monitor#machineTurnedOff(MachineDescriptor)}.
	 */
	@Test(expected=AssertionError.class)
	public void testMachineTurnedOffWithInexistentProvider() {
		monitor.machineTurnedOff(new MachineDescriptor(111, true, MachineType.SMALL, 5));
	}

	/**
	 * Test method for {@link provisioning.Monitor#machineTurnedOff(MachineDescriptor)}.
	 */
	@Test
	public void testMachineTurnedOffWithExistentMachine() {
		MachineDescriptor machineDescriptor = Configuration.getInstance().getProviders()[0].buyMachine(false, MachineType.LARGE);
		monitor.machineTurnedOff(machineDescriptor);
	}

	/**
	 * Test method for {@link provisioning.Monitor#chargeUsers(long)}.
	 */
	@PrepareForTest(Configuration.class)
	@Test
	public void testChargeUsers() {
		Provider provider = EasyMock.createStrictMock(Provider.class);
		EasyMock.expect(provider.getName()).andReturn("amazon");
		EasyMock.expect(provider.getAvailableTypes()).andReturn(new MachineType[]{MachineType.SMALL});
		provider.calculateCost(EasyMock.isA(UtilityResultEntry.class), EasyMock.anyLong());
		
		User user = EasyMock.createStrictMock(User.class);
		user.calculatePartialReceipt(EasyMock.isA(UtilityResultEntry.class));
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		EasyMock.expect(config.getProviders()).andReturn(new Provider[]{provider});
		EasyMock.expect(config.getUsers()).andReturn(new User[]{user});
		
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		
		PowerMock.replayAll(config, provider, user);
		
		new DynamicProvisioningSystem().chargeUsers(0);
		
		PowerMock.verifyAll();
	}

}
