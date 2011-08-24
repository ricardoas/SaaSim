/**
 * 
 */
package provisioning;

import static org.junit.Assert.fail;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.config.PropertiesTesting;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class DynamicProvisioningSystemTest {
	
	@Before
	public void setUp() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_FILE);
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#DynamicProvisioningSystem()}.
	 */
	@Test
	public void testDynamicProvisioningSystem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#registerConfigurable(provisioning.DynamicConfigurable)}.
	 */
	@Test
	public void testRegisterConfigurable() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#setAccountingSystem(commons.sim.AccountingSystem)}.
	 */
	@Test
	public void testSetAccountingSystem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#getAccountingSystem()}.
	 */
	@Test
	public void testGetAccountingSystem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#requestQueued(long, commons.cloud.Request, int)}.
	 */
	@Test
	public void testRequestQueued() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#evaluateUtilisation(long, commons.sim.provisioningheuristics.RanjanStatistics, int)}.
	 */
	@Test
	public void testEvaluateUtilisation() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#calculateUtility()}.
	 */
	@Test
	public void testCalculateUtility() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#chargeUsers(long)}.
	 */
	@Test
	public void testChargeUsers() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link provisioning.DynamicProvisioningSystem#reportLostRequest(commons.cloud.Request)}.
	 */
	@Test
	public void testReportLostRequest() {
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getUserID()).andReturn("100").times(2);
		EasyMock.replay(request);
		
		Monitor dps = new DynamicProvisioningSystem();
//		dps.reportLostRequest(request);

		EasyMock.verify(request);
	}

}
