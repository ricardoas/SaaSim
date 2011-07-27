package config;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import commons.cloud.Provider;

public class ProviderConfigurationTest {
	
	private String INVALID_FILE = "src/test/resources/providers/invalid.properties";
	private String INVALID_FILE2 = "src/test/resources/providers/invalid2.properties";
	private String INVALID_FILE3 = "src/test/resources/providers/invalid3.properties";
	private String INEXISTENT_FILE = "src/test/resources/providers/inexistent.properties";
	private String VALID_FILE = "src/test/resources/providers/iaas.properties";
	
	/**
	 * This test verifies an invalid file with transfer data missing.
	 */
	@Test
	public void testInvalidFile(){
		ProviderConfiguration config = new ProviderConfiguration();
		try {
			config.loadPropertiesFromFile(INVALID_FILE);
			fail("Invalid file!");
		} catch (FileNotFoundException e) {
			fail("Invalid file!");
		} catch (IOException e) {
		}
	}
	
	/**
	 * This test verifies an invalid file with number of providers missing.
	 */
	@Test
	public void testInvalidFile2(){
		ProviderConfiguration config = new ProviderConfiguration();
		try {
			config.loadPropertiesFromFile(INVALID_FILE2);
			fail("Invalid file!");
		} catch (FileNotFoundException e) {
			fail("Invalid file!");
		} catch (IOException e) {
			fail("Invalid file!");
		} catch (NumberFormatException e){
			
		}
	}
	
	/**
	 * This test verifies an invalid file with a wrong number of providers.
	 */
	@Test
	public void testInvalidFile3(){
		ProviderConfiguration config = new ProviderConfiguration();
		try {
			config.loadPropertiesFromFile(INVALID_FILE3);
			fail("Invalid file!");
		} catch (FileNotFoundException e) {
			fail("Invalid file!");
		} catch (IOException e) {
		}
	}
	
	/**
	 * This test verifies the error thrown when an inexistent file is read.
	 */
	@Test
	public void testInexistentFile(){
		ProviderConfiguration config = new ProviderConfiguration();
		try {
			config.loadPropertiesFromFile(INEXISTENT_FILE);
			fail("Invalid file!");
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			fail("Invalid file!");
		}
	}
	
	/**
	 * This test verifies a valid file containing all attributes.
	 */
	@Test
	public void testValidFile(){
		ProviderConfiguration config = new ProviderConfiguration();
		try {
			config.loadPropertiesFromFile(VALID_FILE);
			assertNotNull(config.providers);
			assertEquals(3, config.providers.size());
			
			Provider provider = config.providers.get("p1");
			assertNotNull(provider);
			assertEquals("p1", provider.name);
			assertEquals(0.5, provider.onDemandCpuCost, 0.0);
			assertEquals(10, provider.onDemandLimit, 0.0);
			assertEquals(0.1, provider.reservedCpuCost, 0.0);
			assertEquals(100, provider.reservationLimit, 0.0);
			assertEquals(1000, provider.reservationOneYearFee, 0.0);
			assertEquals(800, provider.reservationThreeYearsFee, 0.0);
			assertEquals(0.15, provider.monitoringCost, 0.0);
			assertEquals("100", provider.transferInLimits);
			assertEquals("0.10 0.09", provider.transferInCosts);
			assertEquals("200", provider.transferOutLimits);
			assertEquals("0.10 0.09", provider.transferOutCosts);
			
			Provider provider2 = config.providers.get("p2");
			assertNotNull(provider2);
			assertEquals("p2", provider2.name);
			assertEquals(0.5, provider2.onDemandCpuCost, 0.0);
			assertEquals(10, provider2.onDemandLimit, 0.0);
			assertEquals(200, provider2.reservationLimit, 0.0);
			assertEquals(0.1, provider2.reservedCpuCost, 0.0);
			assertEquals(1000, provider2.reservationOneYearFee, 0.0);
			assertEquals(800, provider2.reservationThreeYearsFee, 0.0);
			assertEquals(0.15, provider2.monitoringCost, 0.0);
			assertEquals("100", provider2.transferInLimits);
			assertEquals("0.10 0.09", provider2.transferInCosts);
			assertEquals("200", provider2.transferOutLimits);
			assertEquals("0.10 0.09", provider2.transferOutCosts);
			
			Provider provider3 = config.providers.get("p3");
			assertNotNull(provider3);
			assertEquals("p3", provider3.name);
			assertEquals(0.55, provider3.onDemandCpuCost, 0.0);
			assertEquals(1, provider3.onDemandLimit, 0.0);
			assertEquals(10, provider3.reservationLimit, 0.0);
			assertEquals(0.2, provider3.reservedCpuCost, 0.0);
			assertEquals(100, provider3.reservationOneYearFee, 0.0);
			assertEquals(80, provider3.reservationThreeYearsFee, 0.0);
			assertEquals(0.1, provider3.monitoringCost, 0.0);
			assertEquals("10", provider3.transferInLimits);
			assertEquals("0.1 0.0", provider3.transferInCosts);
			assertEquals("20", provider3.transferOutLimits);
			assertEquals("0.1 0.0", provider3.transferOutCosts);
			
			
		} catch (FileNotFoundException e) {
			fail("Valid file! "+e.getMessage());
		} catch (IOException e) {
			fail("Valid file! "+e.getMessage());
		}
	}
	

}
