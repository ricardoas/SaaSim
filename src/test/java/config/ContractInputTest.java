package config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.junit.Test;

import commons.cloud.Contract;
import commons.cloud.User;
import commons.config.Configuration;

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
			Configuration.buildInstance(INVALID_FILE);
	}
	
	/**
	 * This test verifies that a contracts file missing SaaS plans specifications is not valid.
	 * @throws ConfigurationException 
	 */
	@Test(expected=NumberFormatException.class)
	public void testMandatoryPropertyWithNonIntegerValue() throws ConfigurationException{
		Configuration.buildInstance(INVALID_FILE2);
	}
	
	/**
	 * This test verifies that the number of contracts declared is invalid
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationRuntimeException.class)
	public void testArrayPropertyWithWrongSize() throws ConfigurationException{
		Configuration.buildInstance(INVALID_FILE3);
	}
	
	/**
	 * This test verifies that the number of contracts declared is invalid
	 * @throws ConfigurationException 
	 */
	@Test(expected=NumberFormatException.class)
	public void testArrayPropertyWithWrongType() throws ConfigurationException{
		Configuration.buildInstance(INVALID_FILE4);
	}
	
	/**
	 * This test verifies that an error is thrown while trying to read a missing file.
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationException.class)
	public void testInexistentFile() throws ConfigurationException{
		Configuration.buildInstance(INEXISTENT_FILE);
	}
	
	@Test
	public void testValidFile() throws ConfigurationException{
		Configuration.buildInstance(VALID_FILE);
		Configuration config = Configuration.getInstance();
		List<User> users = config.getUsers();
		assertNotNull(users);
		assertEquals(3, users.size());
		
		Contract c1 = users.get(0).getContract();
		assertNotNull(c1);
		assertEquals(10, c1.getCpuLimit(), 0.0);
		assertEquals(1, c1.getExtraCpuCost(), 0.0);
		assertEquals("p1", c1.getName());
		assertEquals(100, c1.getPrice(), 0.0);
		assertEquals(5.55, c1.getSetupCost(), 0.0);
		assertEquals(0.0, c1.getextraTransferenceCost, 0.0);//FIXME: When transference be supported fix this!
		assertEquals(0, c1.gettransferenceLimit, 0.0);//FIXME: When transference be supported fix this!

		Contract c2 = users.get(1).getContract();
		assertNotNull(c2);
		assertEquals(55, c2.getCpuLimit(), 0.0);
		assertEquals(2, c2.getExtraCpuCost(), 0.0);
		assertEquals("p2", c2.getName());
		assertEquals(250, c2.getPrice(), 0.0);
		assertEquals(1.11, c2.getSetupCost(), 0.0);
		assertEquals(0, c2.getextraTransferenceCost, 0.0);//FIXME: When transference be supported fix this!
		assertEquals(0, c2.gettransferenceLimit, 0.0);//FIXME: When transference be supported fix this!

		Contract c3 = users.get(2).getContract();
		assertNotNull(c3);
		assertEquals(10, c3.getCpuLimit(), 0.0);
		assertEquals(1, c3.getExtraCpuCost(), 0.0);
		assertEquals("p1", c3.getName());
		assertEquals(100, c3.getPrice(), 0.0);
		assertEquals(5.55, c3.getSetupCost(), 0.0);
		assertEquals(0, c3.getextraTransferenceCost, 0.0);//FIXME: When transference be supported fix this!
		assertEquals(0, c3.gettransferenceLimit, 0.0);//FIXME: When transference be supported fix this!
	}
}
