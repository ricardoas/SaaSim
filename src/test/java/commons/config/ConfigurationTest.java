package commons.config;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.junit.Assert;
import org.junit.Test;

import commons.cloud.Contract;
import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.User;


public class ConfigurationTest {

	@Test(expected=ConfigurationException.class)
	public void testBuildInstanceWithNullArgument() throws ConfigurationException {
		Configuration.buildInstance(null);
	}

	@Test(expected=ConfigurationException.class)
	public void testBuildInstanceWithEmptyArgument() throws ConfigurationException {
		Configuration.buildInstance("");
	}

	@Test(expected=ConfigurationException.class)
	public void testBuildInstanceWithInexistentConfigFile() throws ConfigurationException {
		Configuration.buildInstance(PropertiesTesting.INEXISTENT_CONFIG_FILE);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testBuildInstanceWithInexistentPlansFile() throws ConfigurationException {
		Configuration.buildInstance(PropertiesTesting.INEXISTENT_PLANS_FILE);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testBuildInstanceWithInexistentAppFile() throws ConfigurationException {
		Configuration.buildInstance(PropertiesTesting.INEXISTENT_APP_FILE);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testBuildInstanceWithInexistentUsersFile() throws ConfigurationException {
		Configuration.buildInstance(PropertiesTesting.INEXISTENT_USERS_FILE);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testBuildInstanceWithInexistentIaaSProvidersFile() throws ConfigurationException {
		Configuration.buildInstance(PropertiesTesting.INEXISTENT_IAAS_PROVIDERS_FILE);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testBuildInstanceWithInexistentIaaSPlanFile() throws ConfigurationException {
		Configuration.buildInstance(PropertiesTesting.INEXISTENT_IAAS_PLAN_FILE);
	}
	
	@Test(expected=ConfigurationRuntimeException.class)
	public void testGetInstanceWithoutBuild() {
		Configuration.getInstance();
	}
	
	@Test
	public void testBuildInstanceWithValidConfiguration() throws ConfigurationException {
		Configuration.buildInstance(PropertiesTesting.VALID_FILE);
		assertNotNull(Configuration.getInstance());
	}
	
	@Test(expected=ConfigurationException.class)
	public void testSaaSUsersWrongFile1() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_USERS_FILE_1);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testSaaSUsersWrongFile2() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_USERS_FILE_2);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testSaaSUsersWrongFile3() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_USERS_FILE_3);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testSaaSUsersWrongFile4() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_USERS_FILE_4);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testSaaSUsersWrongFile5() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_USERS_FILE_5);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testSaaSUsersWrongFile6() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_USERS_FILE_6);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testSaaSUsersWrongFile7() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_USERS_FILE_7);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testSaaSPlansWrongFile1() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PLANS_FILE_1);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testSaaSPlansWrongFile2() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PLANS_FILE_2);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testSaaSPlansWrongFile3() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PLANS_FILE_3);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testSaaSPlansWrongFile4() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PLANS_FILE_4);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testSaaSPlansWrongFile5() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PLANS_FILE_5);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testIaaSProvidersWrongFile1() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_1);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testIaaSProvidersWrongFile2() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_2);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testIaaSProvidersWrongFile3() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_3);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testIaaSProvidersWrongFile4() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_4);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testIaaSProvidersWrongFile5() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_5);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testIaaSProvidersWrongFile6() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_6);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testIaaSProvidersWrongFile7() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_7);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testIaaSProvidersWrongFile8() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_8);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testIaaSProvidersWrongFile9() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.WRONG_PROVIDERS_FILE_9);
	}
	
	@Test
	public void testEmptyIaaSPlanFile() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.EMPTY_IAAS_PLANS_FILE);
		Configuration config = Configuration.getInstance();
		
		List<Provider> providers = config.getProviders();
		assertEquals(3, providers.size());
		for(Provider provider : providers){
			assertFalse(provider.canBuyMachine(true, MachineType.SMALL));
			assertFalse(provider.canBuyMachine(true, MachineType.MEDIUM));
			assertFalse(provider.canBuyMachine(true, MachineType.LARGE));
			assertFalse(provider.canBuyMachine(true, MachineType.XLARGE));
		}
	}
	
	@Test(expected=ConfigurationException.class)
	public void testInvalidIaaSPlanFile() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.INVALID_IAAS_PLANS_FILE);
	}
	
	@Test(expected=ConfigurationException.class)
	public void testInvalidIaaSPlanFile2() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.INVALID_IAAS_PLANS_FILE_2);
	}
	
	@Test
	public void testValidFile() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_FILE);
		Configuration config = Configuration.getInstance();
		config.getApplicationHeuristics();
		config.getDPSHeuristicClass();
		config.getPlanningHeuristicClass();
//		config.getProviders();
		assertEquals(1, config.getRelativePower(MachineType.SMALL), 0.0);
		assertEquals(4, config.getRelativePower(MachineType.MEDIUM), 0.0);
		assertEquals(2, config.getRelativePower(MachineType.LARGE), 0.0);
		assertEquals(4, config.getRelativePower(MachineType.XLARGE), 0.0);
//		config.getUsers();
	}

	@Test
	public void testSaaSUsersAndPlansValidFile() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_FILE);
		Configuration config = Configuration.getInstance();
		List<User> users = config.getUsers();
		assertNotNull(users);
		assertEquals(2, users.size());
		
		Contract c1 = users.get(0).getContract();
		assertNotNull(c1);
		assertEquals("bronze", c1.getName());
		assertEquals(4, c1.getPriority());
		assertEquals(24.95, c1.getPrice(), 0.0);
		assertEquals(0.0, c1.getSetupCost(), 0.0);
		assertEquals(10, c1.getCpuLimitInMillis(), 0.0);
		assertEquals(1, c1.getExtraCpuCost(), 0.0);
		Assert.assertArrayEquals(new long[]{2048}, c1.getTransferenceLimitsInBytes());
		Assert.assertArrayEquals(new double[]{0,0.005}, c1.getTransferenceCosts(), 0.0);
		assertEquals(200, c1.getStorageLimitInMB(), 0.0);
		assertEquals(0.1, c1.getStorageCostPerMB(), 0.0);

		Contract c2 = users.get(1).getContract();
		assertNotNull(c2);
		assertEquals("silver", c2.getName());
		assertEquals(3, c2.getPriority());
		assertEquals(39.95, c2.getPrice(), 0.0);
		assertEquals(0, c2.getSetupCost(), 0.0);
		assertEquals(10, c2.getCpuLimitInMillis(), 0.0);
		assertEquals(1, c2.getExtraCpuCost(), 0.0);
		Assert.assertArrayEquals(new long[]{4096}, c2.getTransferenceLimitsInBytes());
		Assert.assertArrayEquals(new double[]{0,0.005}, c2.getTransferenceCosts(), 0.0);
		assertEquals(300, c2.getStorageLimitInMB(), 0.0);
		assertEquals(0.1, c2.getStorageCostPerMB(), 0.0);
	}
	
	/**
	 * This test verifies a valid file containing all attributes.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testIaaSValidFile() throws ConfigurationException{
		Configuration.buildInstance(PropertiesTesting.VALID_FILE);
		Configuration config = Configuration.getInstance();
		List<Provider> providers = config.getProviders();
		assertNotNull(providers);
		assertEquals(3, providers.size());

		Provider provider2 = providers.get(0);
		assertNotNull(provider2);
		assertEquals("rackspace", provider2.getName());
		assertEquals(20, provider2.getOnDemandLimit(), 0.0);
		assertEquals(100, provider2.getReservationLimit(), 0.0);
		assertEquals(0.15, provider2.getMonitoringCost(), 0.0);
		Assert.assertArrayEquals( new long[]{100}, provider2.getTransferInLimits());
		Assert.assertArrayEquals(new double[]{0.10,0.09}, provider2.getTransferInCosts(), 0.0);
		Assert.assertArrayEquals( new long[]{200}, provider2.getTransferOutLimits());
		Assert.assertArrayEquals(new double[]{0.10,0.09}, provider2.getTransferOutCosts(), 0.0);
		MachineType[] availableTypes2 = provider2.getAvailableTypes();
		Arrays.sort(availableTypes2);
		Assert.assertArrayEquals(new MachineType[]{MachineType.SMALL, MachineType.LARGE}, availableTypes2);
		assertEquals(0.085, provider2.getOnDemandCpuCost(MachineType.SMALL), 0.0);
		assertEquals(0.3, provider2.getReservedCpuCost(MachineType.SMALL), 0.0);
		assertEquals(1000, provider2.getReservationOneYearFee(MachineType.SMALL), 0.0);
		assertEquals(2500, provider2.getReservationThreeYearsFee(MachineType.SMALL), 0.0);
		assertEquals(0.12, provider2.getOnDemandCpuCost(MachineType.LARGE), 0.0);
		assertEquals(0.13, provider2.getReservedCpuCost(MachineType.LARGE), 0.0);
		assertEquals(1500, provider2.getReservationOneYearFee(MachineType.LARGE), 0.0);
		assertEquals(3500, provider2.getReservationThreeYearsFee(MachineType.LARGE), 0.0);

		Provider provider = providers.get(1);
		assertNotNull(provider);
		assertEquals("amazon", provider.getName());
		assertEquals(20, provider.getOnDemandLimit(), 0.0);
		assertEquals(100, provider.getReservationLimit(), 0.0);
		assertEquals(0.15, provider.getMonitoringCost(), 0.0);
		Assert.assertArrayEquals( new long[]{0}, provider.getTransferInLimits());
		Assert.assertArrayEquals(new double[]{0,0}, provider.getTransferInCosts(), 0.0);
		Assert.assertArrayEquals( new long[]{1,10240, 51200,153600}, provider.getTransferOutLimits());
		Assert.assertArrayEquals(new double[]{0,0.12,0.09,0.07,0.05}, provider.getTransferOutCosts(), 0.0);
		MachineType[] availableTypes = provider.getAvailableTypes();
		Arrays.sort(availableTypes);
		Assert.assertArrayEquals(new MachineType[]{MachineType.SMALL,MachineType.LARGE,MachineType.XLARGE}, availableTypes);
		assertEquals(0.085, provider.getOnDemandCpuCost(MachineType.SMALL), 0.0);
		assertEquals(0.3, provider.getReservedCpuCost(MachineType.SMALL), 0.0);
		assertEquals(227.5, provider.getReservationOneYearFee(MachineType.SMALL), 0.0);
		assertEquals(350, provider.getReservationThreeYearsFee(MachineType.SMALL), 0.0);
		assertEquals(0.34, provider.getOnDemandCpuCost(MachineType.LARGE), 0.0);
		assertEquals(0.12, provider.getReservedCpuCost(MachineType.LARGE), 0.0);
		assertEquals(910, provider.getReservationOneYearFee(MachineType.LARGE), 0.0);
		assertEquals(1400, provider.getReservationThreeYearsFee(MachineType.LARGE), 0.0);
		assertEquals(0.68, provider.getOnDemandCpuCost(MachineType.XLARGE), 0.0);
		assertEquals(0.24, provider.getReservedCpuCost(MachineType.XLARGE), 0.0);
		assertEquals(1820, provider.getReservationOneYearFee(MachineType.XLARGE), 0.0);
		assertEquals(2800, provider.getReservationThreeYearsFee(MachineType.XLARGE), 0.0);

		Provider provider3 = providers.get(2);
		assertNotNull(provider3);
		assertEquals("gogrid", provider3.getName());
		assertEquals(5, provider3.getOnDemandLimit(), 0.0);
		assertEquals(10, provider3.getReservationLimit(), 0.0);
		assertEquals(0.1, provider3.getMonitoringCost(), 0.0);
		Assert.assertArrayEquals( new long[]{10}, provider3.getTransferInLimits());
		Assert.assertArrayEquals(new double[]{0.1,0.0}, provider3.getTransferInCosts(), 0.0);
		Assert.assertArrayEquals( new long[]{20}, provider3.getTransferOutLimits());
		Assert.assertArrayEquals(new double[]{0.1,0.0}, provider3.getTransferOutCosts(), 0.0);
		MachineType[] availableTypes3 = provider3.getAvailableTypes();
		Arrays.sort(availableTypes3);
		Assert.assertArrayEquals(new MachineType[]{MachineType.LARGE,MachineType.MEDIUM}, availableTypes3);
		assertEquals(0.085, provider3.getOnDemandCpuCost(MachineType.MEDIUM), 0.0);
		assertEquals(0.3, provider3.getReservedCpuCost(MachineType.MEDIUM), 0.0);
		assertEquals(1000, provider3.getReservationOneYearFee(MachineType.MEDIUM), 0.0);
		assertEquals(2500, provider3.getReservationThreeYearsFee(MachineType.MEDIUM), 0.0);
		assertEquals(0.12, provider3.getOnDemandCpuCost(MachineType.LARGE), 0.0);
		assertEquals(0.13, provider3.getReservedCpuCost(MachineType.LARGE), 0.0);
		assertEquals(1500, provider3.getReservationOneYearFee(MachineType.LARGE), 0.0);
		assertEquals(3500, provider3.getReservationThreeYearsFee(MachineType.LARGE), 0.0);
	}
}
