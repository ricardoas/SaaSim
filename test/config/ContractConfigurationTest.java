package config;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import cloud.Contract;
import cloud.User;

public class ContractConfigurationTest {
	
	private String INVALID_FILE = "test_files/contracts/invalid.contracts";
	private String INVALID_FILE2 = "test_files/contracts/invalid2.contracts";
	private String INVALID_FILE3 = "test_files/contracts/invalid3.contracts";
	private String INEXISTENT_FILE = "test_files/contracts/inexistent.dat";
	private String VALID_FILE = "test_files/contracts/contracts.properties";
	
	/**
	 * This test verifies that a contracts file missing user associations with SaaS plans is not valid.
	 */
	@Test
	public void testIncompleteFile(){
		ContractConfiguration config = new ContractConfiguration();
		try {
			config.loadPropertiesFromFile(INVALID_FILE);
			fail("Data is incomplete!");
		} catch (FileNotFoundException e) {
			fail("Data is incomplete!");
		} catch (IOException e) {
		}
	}
	
	/**
	 * This test verifies that a contracts file missing SaaS plans specifications is not valid.
	 */
	@Test
	public void testIncompleteFile2(){
		ContractConfiguration config = new ContractConfiguration();
		try {
			config.loadPropertiesFromFile(INVALID_FILE2);
			fail("Data is incomplete!");
		} catch (FileNotFoundException e) {
			fail("Data is incomplete!");
		} catch (IOException e) {
		}
	}
	
	/**
	 * This test verifies that the number of contracts declared is invalid
	 */
	@Test
	public void testInvalidFile(){
		ContractConfiguration config = new ContractConfiguration();
		try {
			config.loadPropertiesFromFile(INVALID_FILE3);
			fail("Data is incomplete!");
		} catch (FileNotFoundException e) {
			fail("Data is incomplete!");
		} catch (IOException e) {
		}
	}
	
	/**
	 * This test verifies that an error is thrown while trying to read a missing file.
	 */
	@Test
	public void testInexistentFile(){
		ContractConfiguration config = new ContractConfiguration();
		try {
			config.loadPropertiesFromFile(INEXISTENT_FILE);
			fail("File is missing!");
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			fail("File is missing!");
		}
	}
	
	@Test
	public void testValidFile(){
		try {
			ContractConfiguration config = new ContractConfiguration();
			config.loadPropertiesFromFile(VALID_FILE);
			assertNotNull(config.usersContracts);
			assertEquals(3, config.usersContracts.size());
			assertEquals(2, config.contracts.size());
			
			Contract c1 = config.usersContracts.get(new User("u1"));
			assertNotNull(c1);
			assertEquals(10, c1.cpuLimit, 0.0);
			assertEquals(1, c1.extraCpuCost, 0.0);
			assertEquals("p1", c1.name);
			assertEquals(100, c1.price, 0.0);
			assertEquals(5.55, c1.setupCost, 0.0);
			assertEquals(0.0, c1.transferCost, 0.0);//FIXME: When transference be supported fix this!
			assertEquals(0, c1.transferLimit, 0.0);//FIXME: When transference be supported fix this!
			
			Contract c2 = config.usersContracts.get(new User("u2"));
			assertNotNull(c2);
			assertEquals(55, c2.cpuLimit, 0.0);
			assertEquals(2, c2.extraCpuCost, 0.0);
			assertEquals("p2", c2.name);
			assertEquals(250, c2.price, 0.0);
			assertEquals(1.11, c2.setupCost, 0.0);
			assertEquals(0, c2.transferCost, 0.0);//FIXME: When transference be supported fix this!
			assertEquals(0, c2.transferLimit, 0.0);//FIXME: When transference be supported fix this!
			
			Contract c3 = config.usersContracts.get(new User("u3"));
			assertNotNull(c3);
			assertEquals(10, c3.cpuLimit, 0.0);
			assertEquals(1, c3.extraCpuCost, 0.0);
			assertEquals("p1", c3.name);
			assertEquals(100, c3.price, 0.0);
			assertEquals(5.55, c3.setupCost, 0.0);
			assertEquals(0, c3.transferCost, 0.0);//FIXME: When transference be supported fix this!
			assertEquals(0, c3.transferLimit, 0.0);//FIXME: When transference be supported fix this!
			
			assertTrue(config.contracts.containsKey(c1.name));
			assertTrue(config.contracts.containsKey(c2.name));
			
		} catch (FileNotFoundException e) {
			fail("Valid file!");
		} catch (IOException e) {
			fail("Valid file!");
		}
	}
	
	

}
