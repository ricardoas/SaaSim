package config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.junit.Test;

import commons.cloud.Contract;
import commons.cloud.User;
import commons.config.SimulatorConfiguration;

public class ContractInputTest {
	
	private String INVALID_FILE = "src/test/resources/contracts/invalid.properties";
	private String INVALID_FILE2 = "src/test/resources/contracts/invalid2.properties";
	private String INVALID_FILE3 = "src/test/resources/contracts/invalid3.properties";
	private String INVALID_FILE4 = "src/test/resources/contracts/invalid4.properties";
	private String INEXISTENT_FILE = "src/test/resources/contracts/inexistent.dat";
	private String VALID_FILE = "src/test/resources/contracts/config.properties";
	
	/**
	 * This test verifies that a contracts file missing user associations with SaaS plans is not valid.
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationRuntimeException.class)
	public void testMandatoryPropertyWithEmptyValue() throws ConfigurationException{
			SimulatorConfiguration.buildInstance(INVALID_FILE);
	}
	
	/**
	 * This test verifies that a contracts file missing SaaS plans specifications is not valid.
	 * @throws ConfigurationException 
	 */
	@Test(expected=NumberFormatException.class)
	public void testMandatoryPropertyWithNonIntegerValue() throws ConfigurationException{
		SimulatorConfiguration.buildInstance(INVALID_FILE2);
	}
	
	/**
	 * This test verifies that the number of contracts declared is invalid
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationRuntimeException.class)
	public void testArrayPropertyWithWrongSize() throws ConfigurationException{
		SimulatorConfiguration.buildInstance(INVALID_FILE3);
	}
	
	/**
	 * This test verifies that the number of contracts declared is invalid
	 * @throws ConfigurationException 
	 */
	@Test(expected=NumberFormatException.class)
	public void testArrayPropertyWithWrongType() throws ConfigurationException{
		SimulatorConfiguration.buildInstance(INVALID_FILE4);
	}
	
	/**
	 * This test verifies that an error is thrown while trying to read a missing file.
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationException.class)
	public void testInexistentFile() throws ConfigurationException{
		SimulatorConfiguration.buildInstance(INEXISTENT_FILE);
	}
	
	@Test
	public void testValidFile() throws ConfigurationException{
		SimulatorConfiguration.buildInstance(VALID_FILE);
		SimulatorConfiguration config = SimulatorConfiguration.getInstance();
		Map<User, Contract> usersContracts = config.getContractsPerUser();
		Map<String, Contract> contracts = config.getContractsPerName();
		assertNotNull(usersContracts);
		assertEquals(3, usersContracts.size());
		assertEquals(2, contracts.size());

		Contract c1 = usersContracts.get(new User("u1"));
		assertNotNull(c1);
		assertEquals(10, c1.cpuLimit, 0.0);
		assertEquals(1, c1.extraCpuCost, 0.0);
		assertEquals("p1", c1.name);
		assertEquals(100, c1.price, 0.0);
		assertEquals(5.55, c1.setupCost, 0.0);
		assertEquals(0.0, c1.extraTransferenceCost, 0.0);//FIXME: When transference be supported fix this!
		assertEquals(0, c1.transferenceLimit, 0.0);//FIXME: When transference be supported fix this!

		Contract c2 = usersContracts.get(new User("u2"));
		assertNotNull(c2);
		assertEquals(55, c2.cpuLimit, 0.0);
		assertEquals(2, c2.extraCpuCost, 0.0);
		assertEquals("p2", c2.name);
		assertEquals(250, c2.price, 0.0);
		assertEquals(1.11, c2.setupCost, 0.0);
		assertEquals(0, c2.extraTransferenceCost, 0.0);//FIXME: When transference be supported fix this!
		assertEquals(0, c2.transferenceLimit, 0.0);//FIXME: When transference be supported fix this!

		Contract c3 = usersContracts.get(new User("u3"));
		assertNotNull(c3);
		assertEquals(10, c3.cpuLimit, 0.0);
		assertEquals(1, c3.extraCpuCost, 0.0);
		assertEquals("p1", c3.name);
		assertEquals(100, c3.price, 0.0);
		assertEquals(5.55, c3.setupCost, 0.0);
		assertEquals(0, c3.extraTransferenceCost, 0.0);//FIXME: When transference be supported fix this!
		assertEquals(0, c3.transferenceLimit, 0.0);//FIXME: When transference be supported fix this!

		assertTrue(contracts.containsKey(c1.name));
		assertTrue(contracts.containsKey(c2.name));
	}
}
