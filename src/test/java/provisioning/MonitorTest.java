/**
 * 
 */
package provisioning;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.cloud.UtilityResult;
import commons.io.Checkpointer;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.MachineStatistics;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class MonitorTest extends ValidConfigurationTest {

	private Monitor monitor;

	@Override
	public void setUp() throws Exception{
		super.setUp();
		buildFullConfiguration();
		monitor = new DynamicProvisioningSystem();
	}
	
	/**
	 * Test method for {@link provisioning.Monitor#requestFinished(commons.cloud.Request)}.
	 */
	@Test(expected=AssertionError.class)
	public void testReportRequestFinished() {
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getSaasClient()).andReturn(100).times(2);
		EasyMock.replay(request);
		
		monitor.requestFinished(request);
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
		monitor.machineTurnedOff(new MachineDescriptor(111, true, MachineType.M1_SMALL, 5));
	}

	/**
	 * Test method for {@link provisioning.Monitor#machineTurnedOff(MachineDescriptor)}.
	 */
	@Test
	public void testMachineTurnedOffWithExistentMachine() {
		MachineDescriptor machineDescriptor = Checkpointer.loadProviders()[0].buyMachine(false, MachineType.M1_LARGE);
		monitor.machineTurnedOff(machineDescriptor);
	}

	/**
	 * Test method for {@link provisioning.Monitor#chargeUsers(long)}.
	 */
	@Test
	public void testChargeUsers() {
		UtilityResult utilityBefore = Checkpointer.loadAccountingSystem().calculateUtility();
		new DynamicProvisioningSystem().chargeUsers(0);
		UtilityResult utilityAfter = Checkpointer.loadAccountingSystem().calculateUtility();
		
		double usersFee = 0;
		double providersFee = 0;
		assertEquals(utilityBefore.getUtility() + usersFee + providersFee, utilityAfter.getUtility(), 0.0001);
	}

}
