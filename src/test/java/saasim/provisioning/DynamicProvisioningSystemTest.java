/**
 * 
 */
package saasim.provisioning;

import static org.junit.Assert.*;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.User;
import saasim.cloud.UtilityResult;
import saasim.config.Configuration;
import saasim.io.WorkloadParser;
import saasim.provisioning.DPS;
import saasim.provisioning.DynamicProvisioningSystem;
import saasim.provisioning.Monitor;
import saasim.sim.DynamicConfigurable;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.jeevent.JECheckpointer;
import saasim.sim.util.SaaSAppProperties;
import saasim.util.ValidConfigurationTest;


/**
 * Test class for {@link DPS}.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class DynamicProvisioningSystemTest extends ValidConfigurationTest {
	
	/**
	 * {@inheritDoc}
	 * @throws ConfigurationException 
	 */
	@Override
	public void setUp() throws Exception{
		super.setUp();
		buildFullConfiguration();
	}
	
	/**
	 * Test method for {@link saasim.provisioning.DynamicProvisioningSystem#DynamicProvisioningSystem()}.
	 */
	@Test
	public void testDynamicProvisioningSystemWithValidConfigurationData() {
		assertNotNull(new DynamicProvisioningSystem());
	}
	
	/**
	 * Test method for {@link saasim.provisioning.DPS#registerConfigurable(saasim.sim.DynamicConfigurable)}.
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
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [0]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [1]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [2]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [3]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [4]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [5]), EasyMock.anyBoolean());
		configurable.addMachine(EasyMock.anyInt(), EasyMock.capture(descriptor [6]), EasyMock.anyBoolean());
		configurable.setWorkloadParser(EasyMock.anyObject(WorkloadParser.class));
		configurable.setMonitor(EasyMock.anyObject(Monitor.class));
		
		EasyMock.replay(configurable);
		
		DynamicProvisioningSystem dps = new DynamicProvisioningSystem();
		dps.registerConfigurable(configurable);
		
		assertEquals(MachineType.C1_MEDIUM, descriptor[0].getValue().getType());
		assertEquals(MachineType.C1_MEDIUM, descriptor[1].getValue().getType());
		assertEquals(MachineType.C1_MEDIUM, descriptor[2].getValue().getType());
		assertEquals(MachineType.C1_MEDIUM, descriptor[3].getValue().getType());
		assertEquals(MachineType.C1_MEDIUM, descriptor[4].getValue().getType());
		assertEquals(MachineType.C1_MEDIUM, descriptor[5].getValue().getType());
		assertEquals(MachineType.C1_MEDIUM, descriptor[6].getValue().getType());
		
		EasyMock.verify(configurable);
	}
	
	/**
	 * Test method for {@link saasim.provisioning.DPS#calculateUtility()}.
	 * TODO This test needs redesigning.
	 */
	@Ignore @Test
	public void testCalculateUtilityWithUsersAndProviders() {
		DynamicProvisioningSystem dps = new DynamicProvisioningSystem();
		UtilityResult result = dps.calculateUtility();
		
		UtilityResult currentResult = new UtilityResult(2, 3);
		for(User user : Configuration.getInstance().getUsers()){
			user.calculateOneTimeFees();
		}
		for(Provider provider : Configuration.getInstance().getProviders()){
			provider.calculateUniqueCost();
		}
		
		assertNotNull(result);
		assertEquals(currentResult.getUtility(), result.getUtility(), 0.0);
	}
}
