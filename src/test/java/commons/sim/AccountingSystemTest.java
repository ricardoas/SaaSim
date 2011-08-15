package commons.sim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.config.Configuration;
import commons.sim.components.MachineDescriptor;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Configuration.class)
public class AccountingSystemTest {
	
	private long ONE_HOUR_IN_MILLIS = 1000 * 60 * 60; 
	
	@Test
	public void testCreateReservedMachine(){
		int reservationLimit = 1;
		int onDemandLimit = 1;
		int reservationOneYearFee = 100;
		double reservedCpuCost = 0.05;
		double monitoringCost = 0.15;
		
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", 0.1, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, 80, monitoringCost, new long[]{}, new double[]{},  new long[]{}, new double[]{});
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		assertTrue(provider.canBuyMachine(true));
		assertTrue(provider.canBuyMachine(false));
		
		MachineDescriptor descriptor = acc.buyMachine();
		
		assertFalse(provider.canBuyMachine(true));
		assertTrue(provider.canBuyMachine(false));
		
		assertTrue(descriptor.isReserved());
		assertEquals(0, descriptor.getStartTimeInMillis());
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	@Test
	public void testCreateReservedAndOnDemandMachine(){
		int reservationLimit = 1;
		int onDemandLimit = 1;
		int reservationOneYearFee = 100;
		double reservedCpuCost = 0.05;
		double onDemandcpuCost = 0.1;
		double monitoringCost = 0.15;

		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandcpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, 80, monitoringCost, new long[]{}, new double[]{},  new long[]{}, new double[]{});
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		assertTrue(provider.canBuyMachine(true));
		assertTrue(provider.canBuyMachine(false));
		
		MachineDescriptor descriptor = acc.buyMachine();
		MachineDescriptor descriptor2 = acc.buyMachine();
		
		assertTrue(descriptor.isReserved());
		assertEquals(0, descriptor.getStartTimeInMillis());
		assertFalse(descriptor2.isReserved());
		assertEquals(0, descriptor2.getStartTimeInMillis());
		assertFalse(provider.canBuyMachine(true));
		assertFalse(provider.canBuyMachine(false));
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
		
		//Trying to create a third machine is not possible!
		assertNull(acc.buyMachine());
	}
	
	@Test
	public void testCalcUtilityForMachinesNotFinishedAndAnyReceipt(){
		int reservationLimit = 1;
		int onDemandLimit = 1;
		double onDemandCpuCost = 0.1;
		double reservedCpuCost = 0.05;
		int reservationOneYearFee = 100;
		double monitoringCost = 0.15;
	
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandCpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, 80, monitoringCost, new long[]{}, new double[]{},  new long[]{}, new double[]{});
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getPlanningPeriod()).andReturn(1l);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		acc.buyMachine();
		acc.buyMachine();
		
		//Utility calculus depends on current time!
		double reservedCost = reservationOneYearFee + reservedCpuCost + monitoringCost;
		double onDemandCost = onDemandCpuCost + monitoringCost;
		
		UtilityResult result = acc.calculateUtility(ONE_HOUR_IN_MILLIS);
		assertEquals(reservedCost + onDemandCost, result.getCost(), 0.0);
		assertEquals(0, result.getReceipt(), 0.0);
		assertEquals(0, result.getPenalty(), 0.0);
		assertEquals(-(reservedCost + onDemandCost), result.getUtility(), 0.0);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	@Test
	public void testCalcUtilityForMachinesNotFinishedAndAnyReceipt2(){
		int reservationLimit = 1;
		int onDemandLimit = 1;
		double onDemandCpuCost = 0.1;
		double reservedCpuCost = 0.05;
		int reservationOneYearFee = 100;
		double monitoringCost = 0.15;
	
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandCpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, 80, monitoringCost, new long[]{}, new double[]{},  new long[]{}, new double[]{});
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getPlanningPeriod()).andReturn(1l);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		acc.buyMachine();
		acc.buyMachine();
		
		//Utility calculus depends on current time!
		double reservedCost = reservationOneYearFee + 3 * reservedCpuCost + 3 * monitoringCost;
		double onDemandCost = 3 * onDemandCpuCost + 3 * monitoringCost;
		
		UtilityResult result = acc.calculateUtility(3 * ONE_HOUR_IN_MILLIS);
		assertEquals(reservedCost + onDemandCost, result.getCost(), 0.00001);
		assertEquals(0, result.getReceipt(), 0.0);
		assertEquals(0, result.getPenalty(), 0.0);
		assertEquals(-(reservedCost + onDemandCost), result.getUtility(), 0.00001);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	@Test
	public void testCalcUtilityForMachinesAlreadyFinishedAndNoReceipt(){
		int reservationLimit = 1;
		int onDemandLimit = 1;
		double onDemandCpuCost = 0.1;
		double reservedCpuCost = 0.05;
		int reservationOneYearFee = 100;
		double monitoringCost = 0.15;
	
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandCpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, 80, monitoringCost, new long[]{}, new double[]{},  new long[]{}, new double[]{});
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getPlanningPeriod()).andReturn(1l);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		MachineDescriptor descriptor = acc.buyMachine();
		MachineDescriptor descriptor2 = acc.buyMachine();
		
		descriptor.setFinishTimeInMillis(11 * ONE_HOUR_IN_MILLIS);
		descriptor2.setFinishTimeInMillis(8 * ONE_HOUR_IN_MILLIS);
		
		acc.reportMachineFinish(descriptor);
		acc.reportMachineFinish(descriptor2);
		
		//Utility calculus depends on descriptor finish time!
		double reservedCost = reservationOneYearFee + 11 * reservedCpuCost + 11 * monitoringCost;
		double onDemandCost = 8 * onDemandCpuCost + 8 * monitoringCost;
		
		UtilityResult result = acc.calculateUtility(20 * ONE_HOUR_IN_MILLIS);
		assertEquals(reservedCost + onDemandCost, result.getCost(), 0.00001);
		assertEquals(0, result.getReceipt(), 0.0);
		assertEquals(0, result.getPenalty(), 0.0);
		assertEquals(-(reservedCost + onDemandCost), result.getUtility(), 0.00001);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	@Test
	public void testCalcUtilityForMachinesAlreadyFinishedAndPositiveReceipt(){
		int reservationLimit = 1;
		int onDemandLimit = 1;
		double onDemandCpuCost = 0.1;
		double reservedCpuCost = 0.05;
		int reservationOneYearFee = 100;
		double monitoringCost = 0.15;
	
		int price = 1500;
		int cpuLimitInHours = 10;
		double extraCpuCost = 0.1;

		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandCpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, 80, monitoringCost, new long[]{}, new double[]{},  new long[]{}, new double[]{});
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		
		//Setting users that create the receipt!
		ArrayList<User> users = new ArrayList<User>();
		Contract contract = new Contract("p1", 0, 0, price, cpuLimitInHours, extraCpuCost, new long[]{}, new double[]{});
		User user = new User(contract);
		user.update(11 * ONE_HOUR_IN_MILLIS, 0, 0, 0);//This user exceeds the limit!
		users.add(user);
		users.add(new User(contract));
		EasyMock.expect(config.getUsers()).andReturn(users);
		
		EasyMock.expect(config.getPlanningPeriod()).andReturn(1l);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		MachineDescriptor descriptor = acc.buyMachine();
		MachineDescriptor descriptor2 = acc.buyMachine();
		
		descriptor.setFinishTimeInMillis(11 * ONE_HOUR_IN_MILLIS);
		descriptor2.setFinishTimeInMillis(8 * ONE_HOUR_IN_MILLIS);
		
		acc.reportMachineFinish(descriptor);
		acc.reportMachineFinish(descriptor2);
		
		//Utility calculus depends on descriptor finish time!
		double reservedCost = reservationOneYearFee + 11 * reservedCpuCost + 11 * monitoringCost;
		double onDemandCost = 8 * onDemandCpuCost + 8 * monitoringCost;
		double receipt = 2 * (contract.getPrice()) + contract.getExtraCpuCost(); 
		
		UtilityResult result = acc.calculateUtility(20 * ONE_HOUR_IN_MILLIS);
		assertEquals(reservedCost + onDemandCost, result.getCost(), 0.00001);
		assertEquals(receipt, result.getReceipt(), 0.0);
		assertEquals(0, result.getPenalty(), 0.0);
		assertEquals(receipt-(reservedCost + onDemandCost), result.getUtility(), 0.00001);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}

	@Test
	public void testReportReservedMachineFinished(){
		int reservationLimit = 1;
		int onDemandLimit = 1;
		int reservationOneYearFee = 100;
		double reservedCpuCost = 0.05;
		double onDemandcpuCost = 0.1;
		double monitoringCost = 0.15;

		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandcpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, 80, monitoringCost, new long[]{}, new double[]{},  new long[]{}, new double[]{});
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getPlanningPeriod()).andReturn(1l);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		
		MachineDescriptor descriptor = acc.buyMachine();
		MachineDescriptor descriptor2 = acc.buyMachine();
		assertFalse(provider.canBuyMachine(true));
		assertFalse(provider.canBuyMachine(false));
		
		//Finishing machine
		descriptor.setFinishTimeInMillis(10 * ONE_HOUR_IN_MILLIS);
		acc.reportMachineFinish(descriptor);
		
		assertTrue(provider.canBuyMachine(true));
		assertFalse(provider.canBuyMachine(false));
		
		double reservedCost = reservationOneYearFee + 10 * reservedCpuCost + 10 * monitoringCost;
		double onDemandCost = 10 * onDemandcpuCost + 10 * monitoringCost;
		assertEquals(reservedCost + onDemandCost, provider.calculateCost(10 * ONE_HOUR_IN_MILLIS, 0), 0.0001);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	@Test
	public void testReportOndDemandMachineFinished(){
		int reservationLimit = 1;
		int onDemandLimit = 1;
		int reservationOneYearFee = 100;
		double reservedCpuCost = 0.05;
		double onDemandcpuCost = 0.1;
		double monitoringCost = 0.15;

		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandcpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, 80, monitoringCost, new long[]{}, new double[]{},  new long[]{}, new double[]{});
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getPlanningPeriod()).andReturn(1l);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		
		MachineDescriptor descriptor = acc.buyMachine();
		MachineDescriptor descriptor2 = acc.buyMachine();
		assertFalse(provider.canBuyMachine(true));
		assertFalse(provider.canBuyMachine(false));
		
		//Finishing machine
		descriptor2.setFinishTimeInMillis(10 * ONE_HOUR_IN_MILLIS);
		acc.reportMachineFinish(descriptor2);
		
		assertFalse(provider.canBuyMachine(true));
		assertTrue(provider.canBuyMachine(false));
		
		double reservedCost = reservationOneYearFee + 11 * reservedCpuCost + 11 * monitoringCost;
		double onDemandCost = 10 * onDemandcpuCost + 10 * monitoringCost;
		assertEquals(reservedCost + onDemandCost, provider.calculateCost(11 * ONE_HOUR_IN_MILLIS, 0), 0.0001);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	@Test
	public void testReportReservedMachineFinishedAndCalcUtilityAfter(){
		int reservationLimit = 1;
		int onDemandLimit = 1;
		int reservationOneYearFee = 100;
		double reservedCpuCost = 0.05;
		double onDemandcpuCost = 0.1;
		double monitoringCost = 0.15;

		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandcpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, 80, monitoringCost, new long[]{}, new double[]{},  new long[]{}, new double[]{});
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getPlanningPeriod()).andReturn(1l);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		
		MachineDescriptor descriptor = acc.buyMachine();
		MachineDescriptor descriptor2 = acc.buyMachine();
		assertFalse(provider.canBuyMachine(true));
		assertFalse(provider.canBuyMachine(false));
		
		//Finishing machine
		descriptor.setFinishTimeInMillis(10 * ONE_HOUR_IN_MILLIS);
		acc.reportMachineFinish(descriptor);
		
		assertTrue(provider.canBuyMachine(true));
		assertFalse(provider.canBuyMachine(false));
		
		double reservedCost = reservationOneYearFee + 10 * reservedCpuCost + 10 * monitoringCost;
		double onDemandCost = 20 * onDemandcpuCost + 20 * monitoringCost;
		assertEquals(reservedCost + onDemandCost, provider.calculateCost(20 * ONE_HOUR_IN_MILLIS, 0), 0.0001);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	@Test
	public void testReportBothMachinesFinished(){
		int reservationLimit = 1;
		int onDemandLimit = 1;
		int reservationOneYearFee = 100;
		double reservedCpuCost = 0.05;
		double onDemandcpuCost = 0.1;
		double monitoringCost = 0.15;

		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandcpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, 80, monitoringCost, new long[]{}, new double[]{},  new long[]{}, new double[]{});
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getPlanningPeriod()).andReturn(1l);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		
		MachineDescriptor descriptor = acc.buyMachine();
		MachineDescriptor descriptor2 = acc.buyMachine();
		assertFalse(provider.canBuyMachine(true));
		assertFalse(provider.canBuyMachine(false));
		
		//Finishing machine
		descriptor.setFinishTimeInMillis(10 * ONE_HOUR_IN_MILLIS);
		acc.reportMachineFinish(descriptor);
		descriptor2.setFinishTimeInMillis(999 * ONE_HOUR_IN_MILLIS);
		acc.reportMachineFinish(descriptor2);
		
		assertTrue(provider.canBuyMachine(true));
		assertTrue(provider.canBuyMachine(false));
		
		double reservedCost = reservationOneYearFee + 10 * reservedCpuCost + 10 * monitoringCost;
		double onDemandCost = 999 * onDemandcpuCost + 999 * monitoringCost;
		assertEquals(reservedCost + onDemandCost, provider.calculateCost(20 * ONE_HOUR_IN_MILLIS, 0), 0.0001);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	@Test
	public void testReportMachineFinishOfInexistentMachine(){
		int reservationLimit = 1;
		int onDemandLimit = 1;
		int reservationOneYearFee = 100;
		double reservedCpuCost = 0.05;
		double onDemandcpuCost = 0.1;
		double monitoringCost = 0.15;

		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandcpuCost, onDemandLimit, reservationLimit, reservedCpuCost, reservationOneYearFee, 80, monitoringCost, new long[]{}, new double[]{},  new long[]{}, new double[]{});
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getPlanningPeriod()).andReturn(1l);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		MachineDescriptor descriptor = acc.buyMachine();
		assertEquals(0, descriptor.getFinishTimeInMillis());
		
		//Finishing inexistent machine
		acc.reportMachineFinish(new MachineDescriptor(111, true));
		
		assertEquals(0, descriptor.getFinishTimeInMillis());
		
		//At each calculate cost time, different results are obtained
		assertEquals(reservationOneYearFee + 111 * reservedCpuCost + 111 * monitoringCost, provider.calculateCost(111 * ONE_HOUR_IN_MILLIS, 0), 0.0001);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	/**
	 * This method verifies that after adding a number of on-demand or reserved machines that 
	 * reaches the limits defined, the accounting system informs that no more machines could
	 * be added.
	 */
	@Test
	public void testCanAddMachines(){
		int reservationLimit = 3;
		int onDemandLimit = 2;

		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", 0.1, onDemandLimit, reservationLimit, 0.05, 100, 80, 0.15, new long[]{}, new double[]{},  new long[]{}, new double[]{});
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		assertTrue(provider.canBuyMachine(true));
		assertTrue(provider.canBuyMachine(false));
		
		//Adding a number of machines below limits defined: 3 reserved machines
		acc.buyMachine();
		acc.buyMachine();
		acc.buyMachine();
		
		assertFalse(provider.canBuyMachine(true));
		assertTrue(provider.canBuyMachine(false));
		
		//Adding machines that reaches limits: 2 on-demand
		acc.buyMachine();
		acc.buyMachine();
		
		assertFalse(provider.canBuyMachine(true));
		assertFalse(provider.canBuyMachine(false));
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	@Test
	public void testCanAddMachinesAfterMachinesFinishing(){
		int reservationLimit = 4;
		int onDemandLimit = 6;

		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", 0.1, onDemandLimit, reservationLimit, 0.05, 100, 80, 0.15, new long[]{}, new double[]{},  new long[]{}, new double[]{});
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		
		//Adding a number of machines that reaches limits defined: 4 reserved machines, 6 on-demand
		MachineDescriptor descriptor = acc.buyMachine();
		acc.buyMachine();
		acc.buyMachine();
		MachineDescriptor descriptor2 = acc.buyMachine();
		MachineDescriptor descriptor3 = acc.buyMachine();
		acc.buyMachine();
		acc.buyMachine();
		MachineDescriptor descriptor4 = acc.buyMachine();
		acc.buyMachine();
		acc.buyMachine();
		
		//Verifying that no more machines can be added
		assertFalse(provider.canBuyMachine(true));
		assertFalse(provider.canBuyMachine(false));
		
		//Finishing some reserved machines
		acc.reportMachineFinish(descriptor);
		acc.reportMachineFinish(descriptor2);
		
		assertTrue(provider.canBuyMachine(true));
		assertFalse(provider.canBuyMachine(false));
		
		//Finishing some on-demand machines
		acc.reportMachineFinish(descriptor3);
		acc.reportMachineFinish(descriptor4);
		
		assertTrue(provider.canBuyMachine(true));
		assertTrue(provider.canBuyMachine(false));
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
//	@Test
//	public void testReportRequestFinished(){
//		int resourcesReservationLimit = 4;
//		int onDemandLimit = 2;
//		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
//		
//		Class cls;
//		try {
//			cls = Class.forName("commons.sim.AccountingSystem");
//			Field fld = cls.getDeclaredField("requestsFinishedPerUser");
//			Map<String, List<String>> requestsFinishedPerUser = (Map<String, List<String>>) fld.get(acc);
//			assertNotNull(requestsFinishedPerUser);
//			assertEquals(0, requestsFinishedPerUser.size());
//			
//			//Finishing some requests
//			String userID = "us1";
//			String reqID = "1999876";
//			Long reqSize = 10000l;
//			Request request = EasyMock.createStrictMock(Request.class);
//			EasyMock.expect(request.getUserID()).andReturn(userID).times(2);
//			EasyMock.expect(request.getRequestID()).andReturn(reqID);
//			EasyMock.expect(request.getSizeInBytes()).andReturn(reqSize);
//			EasyMock.replay(request);
//			acc.reportRequestFinished(request);
//			
//			String user2ID = "us2";
//			String req2ID = "11111";
//			Long req2Size = 999l;
//			Request request2 = EasyMock.createStrictMock(Request.class);
//			EasyMock.expect(request2.getUserID()).andReturn(user2ID).times(2);
//			EasyMock.expect(request2.getRequestID()).andReturn(req2ID);
//			EasyMock.expect(request2.getSizeInBytes()).andReturn(req2Size);
//			EasyMock.replay(request2);
//			acc.reportRequestFinished(request2);
//			
//			String req3ID = "999999";
//			Long req3Size = 55l;
//			Request request3 = EasyMock.createStrictMock(Request.class);
//			EasyMock.expect(request3.getUserID()).andReturn(userID);
//			EasyMock.expect(request3.getRequestID()).andReturn(req3ID);
//			EasyMock.expect(request3.getSizeInBytes()).andReturn(req3Size);
//			EasyMock.replay(request3);
//			acc.reportRequestFinished(request3);
//			
//			EasyMock.verify(request, request2, request3);
//			
//			//Verifying that requests for correctly computed
//			requestsFinishedPerUser = (Map<String, List<String>>) fld.get(acc);
//			assertNotNull(requestsFinishedPerUser);
//			assertEquals(2, requestsFinishedPerUser.size());
//			
//			List<String> userRequests = requestsFinishedPerUser.get(userID);
//			assertEquals(2, userRequests.size());
//			assertTrue(userRequests.contains(reqID));
//			assertTrue(userRequests.contains(req3ID));
//			
//			userRequests = requestsFinishedPerUser.get(user2ID);
//			assertEquals(1, userRequests.size());
//			assertTrue(userRequests.contains(req2ID));
//			
//			//Verifying total transferred
//			fld = cls.getDeclaredField("totalTransferred");
//			double totalTransferred = (Double) fld.get(acc);
//			assertEquals(reqSize+req2Size+req3Size, totalTransferred, 0.0);
//			
//		} catch (ClassNotFoundException e) {
//			fail("Valid scenario!");
//		} catch (SecurityException e) {
//			fail("Valid scenario!");
//		} catch (NoSuchFieldException e) {
//			fail("Valid scenario!");
//		} catch (IllegalArgumentException e) {
//			fail("Valid scenario!");
//		} catch (IllegalAccessException e) {
//			fail("Valid scenario!");
//		}
//	}
//	
//	@Test
//	public void testGetRequestsFinishedForInexistentUsers(){
//		int resourcesReservationLimit = 4;
//		int onDemandLimit = 2;
//
//		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
//		
//		assertEquals(0, acc.getRequestsFinished("us1"));
//		assertEquals(0, acc.getRequestsFinished("us2"));
//		assertEquals(0, acc.getRequestsFinished(""));
//	}
//	
//	@Test
//	public void testGetRquestsFinished(){
//		int resourcesReservationLimit = 4;
//		int onDemandLimit = 2;
//		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
//		
//		//Finishing some requests
//		String userID = "us1";
//		String reqID = "1";
//		Long reqSize = 876554l;
//		Request request = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request.getUserID()).andReturn(userID).times(2);
//		EasyMock.expect(request.getRequestID()).andReturn(reqID);
//		EasyMock.expect(request.getSizeInBytes()).andReturn(reqSize);
//		EasyMock.replay(request);
//		acc.reportRequestFinished(request);
//		
//		String user2ID = "us2";
//		String req2ID = "2";
//		Long req2Size = 777l;
//		Request request2 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request2.getUserID()).andReturn(user2ID).times(2);
//		EasyMock.expect(request2.getRequestID()).andReturn(req2ID);
//		EasyMock.expect(request2.getSizeInBytes()).andReturn(req2Size);
//		EasyMock.replay(request2);
//		acc.reportRequestFinished(request2);
//		
//		String req3ID = "3";
//		Long req3Size = 123l;
//		Request request3 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request3.getUserID()).andReturn(userID);
//		EasyMock.expect(request3.getRequestID()).andReturn(req3ID);
//		EasyMock.expect(request3.getSizeInBytes()).andReturn(req3Size);
//		EasyMock.replay(request3);
//		acc.reportRequestFinished(request3);
//		
//		String user3ID = "us3";
//		String req4ID = "3";
//		Long req4Size = 1000000l;
//		Request request4 = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(request4.getUserID()).andReturn(user3ID).times(2);
//		EasyMock.expect(request4.getRequestID()).andReturn(req4ID);
//		EasyMock.expect(request4.getSizeInBytes()).andReturn(req4Size);
//		EasyMock.replay(request4);
//		acc.reportRequestFinished(request4);
//		
//		EasyMock.verify(request, request2, request3, request4);
//		
//		//Verifying requests per user
//		assertEquals(2, acc.getRequestsFinished(userID));
//		assertEquals(1, acc.getRequestsFinished(user2ID));
//		assertEquals(1, acc.getRequestsFinished(user3ID));
//	}
//	
//	@Test
//	public void testExtraReceiptCalcWithExtraCpuResourcesUsed(){
//		Double setupCost = 100d;
//		Double price = 200d;
//		Double cpuLimit = 11.5d * ONE_HOUR_IN_MILLIS;
//		Double extraCpuCost = 0.9d;
//		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
//		
//		User user = new User("us1");
//		user.consumedCpu = 12d * ONE_HOUR_IN_MILLIS;//Partial hour is billed as a full hour
//		
//		int resourcesReservationLimit = 4;
//		int onDemandLimit = 2;
//		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
//		
//		assertEquals(0.9, acc.calcExtraReceipt(contract, user), 0.0);
//	}
//	
//	@Test
//	public void testCalculateCostForOnDemandAndReservedResources(){
//		User user = new User("us1");
//		JEEventScheduler scheduler = new JEEventScheduler();
//		Provider provider = this.buildProvider();
//		
//		int resourcesReservationLimit = 4;
//		int onDemandLimit = 2;
//		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
//		
//		//Adding reserved resources
//		Long machine1ID = 1l;
//		Triple<Long, Long, Double> triple = new Triple<Long, Long, Double>();
//		triple.firstValue = 0l;
//		triple.secondValue = 10 * ONE_HOUR_IN_MILLIS;
//		provider.reservedResources.put(machine1ID, triple);
//		
//		Long machine2ID = 2l;
//		Triple<Long, Long, Double> triple2 = new Triple<Long, Long, Double>();
//		triple2.firstValue = 10 * ONE_HOUR_IN_MILLIS;
//		triple2.secondValue = 30 * ONE_HOUR_IN_MILLIS;
//		provider.reservedResources.put(machine2ID, triple2);
//		
//		Long machine3ID = 3l;
//		Triple<Long, Long, Double> triple3 = new Triple<Long, Long, Double>();
//		triple3.firstValue = 1 * ONE_HOUR_IN_MILLIS;
//		triple3.secondValue = 16 * ONE_HOUR_IN_MILLIS;
//		provider.reservedResources.put(machine3ID, triple3);
//		
//		Long machine4ID = 4l;
//		Triple<Long, Long, Double> triple4 = new Triple<Long, Long, Double>();
//		triple4.firstValue = 50 * ONE_HOUR_IN_MILLIS;
//		triple4.secondValue = 65 * ONE_HOUR_IN_MILLIS;
//		provider.reservedResources.put(machine4ID, triple4);
//		
//		Long machine5ID = 5l;
//		Triple<Long, Long, Double> triple5 = new Triple<Long, Long, Double>();
//		triple5.firstValue = 0l;
//		triple5.secondValue = 15 * ONE_HOUR_IN_MILLIS;
//		provider.reservedResources.put(machine5ID, triple5);
//		
//		Long machine6ID = 6l;
//		Triple<Long, Long, Double> triple6 = new Triple<Long, Long, Double>();
//		triple6.firstValue = (long)(11.5 * ONE_HOUR_IN_MILLIS);
//		triple6.secondValue = 24 * ONE_HOUR_IN_MILLIS;
//		provider.reservedResources.put(machine6ID, triple6);
//		
//		Long machine7ID = 7l;
//		Triple<Long, Long, Double> triple7 = new Triple<Long, Long, Double>();
//		triple7.firstValue = 0l;
//		triple7.secondValue = (long)(15.23 * ONE_HOUR_IN_MILLIS);
//		provider.reservedResources.put(machine7ID, triple7);
//		
//		Long machine8ID = 8l;
//		Triple<Long, Long, Double> triple8 = new Triple<Long, Long, Double>();
//		triple8.firstValue = 18 * ONE_HOUR_IN_MILLIS;
//		triple8.secondValue = 36 * ONE_HOUR_IN_MILLIS;
//		provider.onDemandResources.put(machine8ID, triple8);
//		
//		Long machine9ID = 9l;
//		Triple<Long, Long, Double> triple9 = new Triple<Long, Long, Double>();
//		triple9.firstValue = 0l;
//		triple9.secondValue = (long)(78.5 * ONE_HOUR_IN_MILLIS);
//		provider.onDemandResources.put(machine9ID, triple9);
//		
//		Long machine10ID = 10l;
//		Triple<Long, Long, Double> triple10 = new Triple<Long, Long, Double>();
//		triple10.firstValue = (long)(2.4 * ONE_HOUR_IN_MILLIS);
//		triple10.secondValue = (long)(3.6d * ONE_HOUR_IN_MILLIS);
//		provider.onDemandResources.put(machine10ID, triple10);
//		
//		Long machine11ID = 11l;
//		Triple<Long, Long, Double> triple11 = new Triple<Long, Long, Double>();
//		triple11.firstValue = 0l;
//		triple11.secondValue = (long)(5.14d * ONE_HOUR_IN_MILLIS);
//		provider.onDemandResources.put(machine11ID, triple11);
//		
//		//Verifying total cost
//		Class cls;
//		try {
//			cls = Class.forName("commons.sim.AccountingSystem");
//			Field fld = cls.getDeclaredField("totalTransferred");
//			fld.setDouble(acc, 0d);
//			
//			assertEquals(7 * provider.reservationOneYearFee + 104 * provider.reservedCpuCost + 104 * provider.monitoringCost +
//					105 * provider.onDemandCpuCost + 105 * provider.monitoringCost, acc.calculateCost(provider), 0.0);
//		} catch (ClassNotFoundException e) {
//			fail("Valid scenario!");
//		} catch (SecurityException e) {
//			fail("Valid scenario!");
//		} catch (NoSuchFieldException e) {
//			fail("Valid scenario!");
//		} catch (IllegalArgumentException e) {
//			fail("Valid scenario!");
//		} catch (IllegalAccessException e) {
//			fail("Valid scenario!");
//		}
//	}
//	
//	@Test
//	public void testCalculateTotalReceipt(){
//		Double setupCost = 100d;
//		Double price = 200d;
//		Double cpuLimit = 1d * ONE_HOUR_IN_MILLIS;
//		Double extraCpuCost = 0.085d;
//		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
//		
//		User user = new User("us1");
//		user.consumedCpu = 199d * ONE_HOUR_IN_MILLIS;
//		
//		int resourcesReservationLimit = 4;
//		int onDemandLimit = 2;
//		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
//		
//		assertEquals(16.83 + price + setupCost, acc.calculateTotalReceipt(contract, user), 0.0);
//	}
//	
//	private Provider buildProvider(){
//		double onDemandCpuCost = 0.12;
//		int onDemandLimit = 30;
//		int reservationLimit = 20;
//		double reservedCpuCost = 0.085;
//		double reservationOneYearFee = 227.50;
//		double reservationThreeYearsFee = 70;
//		double monitoringCost = 0.15;
//		String transferInLimits = "";
//		String transferInCosts = "";
//		String transferOutLimits = "";
//		String transferOutCosts = "";
//		
//		return new Provider("prov", onDemandCpuCost, onDemandLimit, reservationLimit, 
//				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
//				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
//	}
//	
//	@Test
//	public void testCalculateUtility(){
//		Double setupCost = 100d;
//		Double price = 200d;
//		Double cpuLimit = 1d * ONE_HOUR_IN_MILLIS;
//		Double extraCpuCost = 0.9d;
//		Contract contract = new Contract("p1", setupCost, price, cpuLimit, extraCpuCost);
//		
//		User user = new User("us1");
//		user.consumedCpu = 212d * ONE_HOUR_IN_MILLIS;
//		
//		Provider provider = this.buildProvider();
//		JEEventScheduler scheduler = new JEEventScheduler();
//		
//		int resourcesReservationLimit = 1;
//		int onDemandLimit = 1;
//		AccountingSystem acc = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
//		
//		//Adding reserved resources data
//		Long mach1ID = 1l;
//		Triple<Long, Long, Double> triple = new Triple<Long, Long, Double>();
//		triple.firstValue = 0l;
//		triple.secondValue = 10 * ONE_HOUR_IN_MILLIS;
//		provider.reservedResources.put(mach1ID, triple);
//		
//		Long mach2ID = 2l;
//		Triple<Long, Long, Double> triple2 = new Triple<Long, Long, Double>();
//		triple2.firstValue = 0l;
//		triple2.secondValue = 20 * ONE_HOUR_IN_MILLIS;
//		provider.reservedResources.put(mach2ID, triple2);
//		
//		Long mach3ID = 3l;
//		Triple<Long, Long, Double> triple3 = new Triple<Long, Long, Double>();
//		triple3.firstValue = 0l;
//		triple3.secondValue = 15 * ONE_HOUR_IN_MILLIS;
//		provider.reservedResources.put(mach3ID, triple3);
//		
//		Long mach4ID = 4l;
//		Triple<Long, Long, Double> triple4 = new Triple<Long, Long, Double>();
//		triple4.firstValue = 0l;
//		triple4.secondValue = 15 * ONE_HOUR_IN_MILLIS;
//		provider.reservedResources.put(mach4ID, triple4);
//		
//		Long mach5ID = 5l;
//		Triple<Long, Long, Double> triple5 = new Triple<Long, Long, Double>();
//		triple5.firstValue = 0l;
//		triple5.secondValue = 15 * ONE_HOUR_IN_MILLIS;
//		provider.reservedResources.put(mach5ID, triple5);
//		
//		Long mach6ID = 6l;
//		Triple<Long, Long, Double> triple6 = new Triple<Long, Long, Double>();
//		triple6.firstValue = 0l;
//		triple6.secondValue = (long)(15.5d * ONE_HOUR_IN_MILLIS);
//		provider.reservedResources.put(mach6ID, triple6);
//		
//		Long mach7ID = 7l;
//		Triple<Long, Long, Double> triple7 = new Triple<Long, Long, Double>();
//		triple7.firstValue = 0l;
//		triple7.secondValue = (long)(15.23d * ONE_HOUR_IN_MILLIS);
//		provider.reservedResources.put(mach7ID, triple7);
//		
//		//On-demand resources data
//		Long mach8ID = 8l;
//		Triple<Long, Long, Double> triple8 = new Triple<Long, Long, Double>();
//		triple8.firstValue = 0l;
//		triple8.secondValue = 18 * ONE_HOUR_IN_MILLIS;
//		provider.onDemandResources.put(mach8ID, triple8);
//		
//		Long mach9ID = 9l;
//		Triple<Long, Long, Double> triple9 = new Triple<Long, Long, Double>();
//		triple9.firstValue = 0l;
//		triple9.secondValue = (long)(78.5d * ONE_HOUR_IN_MILLIS);
//		provider.onDemandResources.put(mach9ID, triple9);
//		
//		Long mach10ID = 10l;
//		Triple<Long, Long, Double> triple10 = new Triple<Long, Long, Double>();
//		triple10.firstValue = 0l;
//		triple10.secondValue = (long)(1.2d * ONE_HOUR_IN_MILLIS);
//		provider.onDemandResources.put(mach10ID, triple10);
//		
//		Long mach11ID = 11l;
//		Triple<Long, Long, Double> triple11 = new Triple<Long, Long, Double>();
//		triple11.firstValue = 0l;
//		triple11.secondValue = (long)(5.14d * ONE_HOUR_IN_MILLIS);
//		provider.onDemandResources.put(mach11ID, triple11);
//		
//		double cost = 7 * provider.reservationOneYearFee + 107 * provider.reservedCpuCost + 107 * provider.monitoringCost 
//						+ 105 * provider.onDemandCpuCost + 105 * provider.monitoringCost;
//		double receipt = Math.ceil((user.consumedCpu - cpuLimit)/ONE_HOUR_IN_MILLIS) * extraCpuCost + setupCost + price; 
//		
//		assertEquals(receipt-cost, acc.calculateUtility(), 0.0);
//	}
}
