/**
 * 
 */
package saasim.provisioning;

import static org.junit.Assert.assertEquals;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
import org.junit.Test;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.Request;
import saasim.cloud.User;
import saasim.cloud.UtilityResult;
import saasim.config.Configuration;
import saasim.sim.AccountingSystem;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.schedulingheuristics.MachineStatistics;
import saasim.util.ValidConfigurationTest;


/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class MonitorTest extends ValidConfigurationTest {

	private Monitor monitor;

	@Override
	public void setUp() throws Exception{
		super.setUp();
		buildFullConfiguration();
		monitor = new DynamicProvisioningSystem(null, null);
	}
	
	/**
	 * Test method for {@link saasim.provisioning.Monitor#requestFinished(saasim.cloud.Request)}.
	 */
	@Test(expected=AssertionError.class)
	public void testReportRequestFinished() {
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getSaasClient()).andReturn(100).times(2);
		EasyMock.replay(request);
		
		monitor.requestFinished(request);
	}

	/**
	 * Test method for {@link saasim.provisioning.Monitor#requestQueued(long, saasim.cloud.Request, int)}.
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
	 * Test method for {@link saasim.provisioning.Monitor#sendStatistics(long, saasim.sim.schedulingheuristics.MachineStatistics, int)}.
	 */
	@Test
	public void testEvaluateUtilisation() {
		MachineStatistics statistics = EasyMock.createStrictMock(MachineStatistics.class);
		EasyMock.replay(statistics);
		
		monitor.sendStatistics(1000, statistics, 0);
		
		EasyMock.verify(statistics);
	}

	/**
	 * Test method for {@link saasim.provisioning.Monitor#machineTurnedOff(MachineDescriptor)}.
	 */
	@Test(expected=AssertionError.class)
	public void testMachineTurnedOffWithInexistentProvider() {
		monitor.machineTurnedOff(new MachineDescriptor(111, true, MachineType.M1_SMALL, 5));
	}

	/**
	 * Test method for {@link saasim.provisioning.Monitor#machineTurnedOff(MachineDescriptor)}.
	 */
	@Test
	public void testMachineTurnedOffWithExistentMachine() {
		MachineDescriptor machineDescriptor = Configuration.getInstance().getProviders()[0].buyMachine(false, MachineType.M1_LARGE);
		monitor.machineTurnedOff(machineDescriptor);
	}

	/**
	 * Test method for {@link saasim.provisioning.Monitor#chargeUsers(long)}.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testChargeUsers() {
		Configuration r = Configuration.getInstance();
		UtilityResult utilityBefore = new AccountingSystem(new User[]{},new Provider[]{}).calculateUtility();
		new DynamicProvisioningSystem(null, null).chargeUsers(0);
		Configuration r1 = Configuration.getInstance();
		UtilityResult utilityAfter = new AccountingSystem(new User[]{},new Provider[]{}).calculateUtility();
		
		double usersFee = 0;
		double providersFee = 0;
		assertEquals(utilityBefore.getUtility() + usersFee + providersFee, utilityAfter.getUtility(), 0.0001);
	}

}
