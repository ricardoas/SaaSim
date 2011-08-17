package config;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.junit.Assert;
import org.junit.Test;

import commons.cloud.Provider;
import commons.config.Configuration;

public class ProviderInputTest {
	
	/**
	 * This test verifies an invalid file with transfer data missing.
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationRuntimeException.class)
	public void testMandatoryPropertyWithEmptyValue() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_1);
	}
	
	/**
	 * This test verifies an invalid file with number of providers missing.
	 * @throws ConfigurationException 
	 */
	@Test(expected=NumberFormatException.class)
	public void testMandatoryPropertyWithNonIntegerValue() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_2);
	}
	
	/**
	 * This test verifies an invalid file with a wrong number of providers.
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationRuntimeException.class)
	public void testArrayPropertyWithWrongSize() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_3);
	}
	
	/**
	 * This test verifies an invalid file with a wrong number of providers.
	 * @throws ConfigurationException 
	 */
	@Test(expected=NumberFormatException.class)
	public void testArrayPropertyWithWrongType() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_4);
	}
	
	/**
	 * This test verifies the error thrown when an inexistent file is read.
	 * @throws ConfigurationException 
	 */
	@Test(expected=ConfigurationException.class)
	public void testInexistentFile() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.INEXISTENT_FILE);
	}
	
	/**
	 * This test verifies a valid file containing all attributes.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testValidFile() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_FILE);
		Configuration config = Configuration.getInstance();
		List<Provider> providers = config.getProviders();
		assertNotNull(providers);
		assertEquals(3, providers.size());

		Provider provider = providers.get(0);
		assertNotNull(provider);
		assertEquals("amazon", provider.getName());
		assertEquals(0.5, provider.getOnDemandCpuCost(), 0.0);
		assertEquals(20, provider.getOnDemandLimit(), 0.0);
		assertEquals(0.3, provider.getReservedCpuCost(), 0.0);
		assertEquals(100, provider.getReservationLimit(), 0.0);
		assertEquals(1000, provider.getReservationOneYearFee(), 0.0);
		assertEquals(2500, provider.getReservationThreeYearsFee(), 0.0);
		assertEquals(0.15, provider.getMonitoringCost(), 0.0);
		Assert.assertArrayEquals( new long[]{100}, provider.getTransferInLimits());
		Assert.assertArrayEquals(new double[]{0.10,0.09}, provider.getTransferInCosts(), 0.0);
		Assert.assertArrayEquals( new long[]{200}, provider.getTransferOutLimits());
		Assert.assertArrayEquals(new double[]{0.10,0.09}, provider.getTransferOutCosts(), 0.0);

		Provider provider2 = providers.get(1);
		assertNotNull(provider2);
		assertEquals("rackspace", provider2.getName());
		assertEquals(0.5, provider2.getOnDemandCpuCost(), 0.0);
		assertEquals(10, provider2.getOnDemandLimit(), 0.0);
		assertEquals(200, provider2.getReservationLimit(), 0.0);
		assertEquals(0.1, provider2.getReservedCpuCost(), 0.0);
		assertEquals(1000, provider2.getReservationOneYearFee(), 0.0);
		assertEquals(800, provider2.getReservationThreeYearsFee(), 0.0);
		assertEquals(0.15, provider2.getMonitoringCost(), 0.0);
		Assert.assertArrayEquals( new long[]{100}, provider2.getTransferInLimits());
		Assert.assertArrayEquals(new double[]{0.10,0.09}, provider2.getTransferInCosts(), 0.0);
		Assert.assertArrayEquals( new long[]{200}, provider2.getTransferOutLimits());
		Assert.assertArrayEquals(new double[]{0.10,0.09}, provider2.getTransferOutCosts(), 0.0);

		Provider provider3 = providers.get(2);
		assertNotNull(provider3);
		assertEquals("gogrid", provider3.getName());
		assertEquals(0.55, provider3.getOnDemandCpuCost(), 0.0);
		assertEquals(1, provider3.getOnDemandLimit(), 0.0);
		assertEquals(10, provider3.getReservationLimit(), 0.0);
		assertEquals(0.2, provider3.getReservedCpuCost(), 0.0);
		assertEquals(100, provider3.getReservationOneYearFee(), 0.0);
		assertEquals(80, provider3.getReservationThreeYearsFee(), 0.0);
		assertEquals(0.1, provider3.getMonitoringCost(), 0.0);
		Assert.assertArrayEquals( new long[]{10}, provider3.getTransferInLimits());
		Assert.assertArrayEquals(new double[]{0.1,0.0}, provider3.getTransferInCosts(), 0.0);
		Assert.assertArrayEquals( new long[]{20}, provider3.getTransferOutLimits());
		Assert.assertArrayEquals(new double[]{0.1,0.0}, provider3.getTransferOutCosts(), 0.0);
	}
}
