/**
 * 
 */
package provisioning;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import util.ValidConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityResultEntry;
import commons.config.Configuration;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.MachineStatistics;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Configuration.class)
public class MonitorTest extends ValidConfigurationTest {

	private Monitor monitor;

	@Override
	public void setUp() throws Exception{
		super.setUp();
		buildFullConfiguration();
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
