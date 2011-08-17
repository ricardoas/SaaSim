package commons.sim;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.cloud.Contract;
import commons.cloud.MachineType;
import commons.cloud.MachineTypeValue;
import commons.cloud.Provider;
import commons.cloud.Request;
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
	public void testCalcExtraReceiptWithExtraCpuResourcesUsed(){
		Double setupCost = 100d;
		Double price = 200d;
		long cpuLimit = 11;
		double extraCpuCost = 0.9d;
		Contract contract = new Contract("p1", 0, setupCost, price, cpuLimit, extraCpuCost, new long[]{}, new double[]{});
		
		User user = new User(contract);
		user.update((long)(11.5 * ONE_HOUR_IN_MILLIS), 0, 0, 0);//Partial hour is billed as a full hour
		
		int reservationLimit = 4;
		int onDemandLimit = 2;
		double onDemandCpuCost = 0.0;
		double reservedCpuCost = 0.0;
		int reservationOneYearFee = 0;
		double monitoringCost = 0.0;
	
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		
		//Setting users that create the receipt!
		ArrayList<User> users = new ArrayList<User>();
		users.add(user);
		EasyMock.expect(config.getUsers()).andReturn(users);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		
		UtilityResult result = acc.calculateUtility(12 * ONE_HOUR_IN_MILLIS);
		assertEquals(200 + 0.9, result.getReceipt(), 0.0);
		
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
	public void testCalcUtilityForMachinesAlreadyFinishedAndReservedMachineAlreadyCharged(){
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
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
		double reservedCost = 11 * reservedCpuCost + 11 * monitoringCost;
		double onDemandCost = 8 * onDemandCpuCost + 8 * monitoringCost;
		double receipt = 2 * (contract.getPrice()) + contract.getExtraCpuCost(); 
		
		//Setting that 1 reserved machine was already charged!
		acc.setMaximumNumberOfReservedMachinesUsed(1);
		
		UtilityResult result = acc.calculateUtility(20 * ONE_HOUR_IN_MILLIS);
		assertEquals(reservedCost + onDemandCost, result.getCost(), 0.00001);
		assertEquals(receipt, result.getReceipt(), 0.0);
		assertEquals(0, result.getPenalty(), 0.0);
		assertEquals(receipt-(reservedCost + onDemandCost), result.getUtility(), 0.00001);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	@Test
	public void testCalcUtilityForMachinesAlreadyFinishedAndNoCost(){
		int reservationLimit = 1;
		int onDemandLimit = 1;
		double onDemandCpuCost = 0.0;
		double reservedCpuCost = 0.0;
		int reservationOneYearFee = 0;
		double monitoringCost = 0.0;
	
		int price = 1500;
		int cpuLimitInHours = 10;
		double extraCpuCost = 0.1;

		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
		double receipt = 2 * (contract.getPrice()) + contract.getExtraCpuCost(); 
		
		UtilityResult result = acc.calculateUtility(20 * ONE_HOUR_IN_MILLIS);
		assertEquals(0, result.getCost(), 0.00001);
		assertEquals(receipt, result.getReceipt(), 0.0);
		assertEquals(0, result.getPenalty(), 0.0);
		assertEquals(receipt, result.getUtility(), 0.00001);
		
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
		acc.reportMachineFinish(new MachineDescriptor(111, true, MachineTypeValue.SMALL));
		
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, 0.15, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
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
	
	@Test
	public void testReportRequestFinished(){
		int reservationLimit = 4;
		int onDemandLimit = 2;
		double onDemandCpuCost = 0.0;
		double reservedCpuCost = 0.0;
		int reservationOneYearFee = 0;
		double monitoringCost = 0.0;
	
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		
		//Setting users that create the receipt!
		ArrayList<User> users = new ArrayList<User>();
		EasyMock.expect(config.getUsers()).andReturn(users);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		
		Class cls;
		try {
			cls = Class.forName("commons.sim.AccountingSystem");
			Field fld = cls.getDeclaredField("requestsFinishedPerUser");
			Map<String, List<String>> requestsFinishedPerUser = (Map<String, List<String>>) fld.get(acc);
			assertNotNull(requestsFinishedPerUser);
			assertEquals(0, requestsFinishedPerUser.size());
			
			//Finishing some requests
			String userID = "us1";
			String reqID = "1999876";
			Request request = EasyMock.createStrictMock(Request.class);
			EasyMock.expect(request.getUserID()).andReturn(userID).times(2);
			EasyMock.expect(request.getRequestID()).andReturn(reqID);
			EasyMock.replay(request);
			acc.reportRequestFinished(request);
			
			String user2ID = "us2";
			String req2ID = "11111";
			Request request2 = EasyMock.createStrictMock(Request.class);
			EasyMock.expect(request2.getUserID()).andReturn(user2ID).times(2);
			EasyMock.expect(request2.getRequestID()).andReturn(req2ID);
			EasyMock.replay(request2);
			acc.reportRequestFinished(request2);
			
			String req3ID = "999999";
			Request request3 = EasyMock.createStrictMock(Request.class);
			EasyMock.expect(request3.getUserID()).andReturn(userID);
			EasyMock.expect(request3.getRequestID()).andReturn(req3ID);
			EasyMock.replay(request3);
			acc.reportRequestFinished(request3);
			
			PowerMock.verify(Configuration.class);
			EasyMock.verify(request, request2, request3, config);
			
			//Verifying that requests for correctly computed
			requestsFinishedPerUser = (Map<String, List<String>>) fld.get(acc);
			assertNotNull(requestsFinishedPerUser);
			assertEquals(2, requestsFinishedPerUser.size());
			
			List<String> userRequests = requestsFinishedPerUser.get(userID);
			assertEquals(2, userRequests.size());
			assertTrue(userRequests.contains(reqID));
			assertTrue(userRequests.contains(req3ID));
			
			userRequests = requestsFinishedPerUser.get(user2ID);
			assertEquals(1, userRequests.size());
			assertTrue(userRequests.contains(req2ID));
			
		} catch (ClassNotFoundException e) {
			fail("Valid scenario!");
		} catch (SecurityException e) {
			fail("Valid scenario!");
		} catch (NoSuchFieldException e) {
			fail("Valid scenario!");
		} catch (IllegalArgumentException e) {
			fail("Valid scenario!");
		} catch (IllegalAccessException e) {
			fail("Valid scenario!");
		}
	}
	
	@Test
	public void testGetRequestsFinishedForInexistentUsers(){
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		EasyMock.expect(config.getProviders()).andReturn(new ArrayList<Provider>());
		
		//Setting users that create the receipt!
		ArrayList<User> users = new ArrayList<User>();
		EasyMock.expect(config.getUsers()).andReturn(users);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		
		assertEquals(0, acc.getRequestsFinished("us1"));
		assertEquals(0, acc.getRequestsFinished("us2"));
		assertEquals(0, acc.getRequestsFinished(""));
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	@Test
	public void testGetRequestsFinished(){
		int reservationLimit = 4;
		int onDemandLimit = 2;
		double onDemandCpuCost = 0.0;
		double reservedCpuCost = 0.0;
		int reservationOneYearFee = 0;
		double monitoringCost = 0.0;

		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		
		//Setting users that create the receipt!
		ArrayList<User> users = new ArrayList<User>();
		EasyMock.expect(config.getUsers()).andReturn(users);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		
		//Finishing some requests
		String userID = "us1";
		String reqID = "1";
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getUserID()).andReturn(userID).times(2);
		EasyMock.expect(request.getRequestID()).andReturn(reqID);
		EasyMock.replay(request);
		acc.reportRequestFinished(request);
		
		String user2ID = "us2";
		String req2ID = "2";
		Request request2 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request2.getUserID()).andReturn(user2ID).times(2);
		EasyMock.expect(request2.getRequestID()).andReturn(req2ID);
		EasyMock.replay(request2);
		acc.reportRequestFinished(request2);
		
		String req3ID = "3";
		Request request3 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request3.getUserID()).andReturn(userID);
		EasyMock.expect(request3.getRequestID()).andReturn(req3ID);
		EasyMock.replay(request3);
		acc.reportRequestFinished(request3);
		
		String user3ID = "us3";
		String req4ID = "3";
		Request request4 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request4.getUserID()).andReturn(user3ID).times(2);
		EasyMock.expect(request4.getRequestID()).andReturn(req4ID);
		EasyMock.replay(request4);
		acc.reportRequestFinished(request4);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(request, request2, request3, request4, config);
		
		//Verifying requests per user
		assertEquals(2, acc.getRequestsFinished(userID));
		assertEquals(1, acc.getRequestsFinished(user2ID));
		assertEquals(1, acc.getRequestsFinished(user3ID));
	}

	@Test
	public void testReportRequestLost(){
		int reservationLimit = 4;
		int onDemandLimit = 2;
		double onDemandCpuCost = 0.0;
		double reservedCpuCost = 0.0;
		int reservationOneYearFee = 0;
		double monitoringCost = 0.0;
	
		//Mocks
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		List<Provider> providers = new ArrayList<Provider>();
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<MachineType>());
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		
		//Setting users that create the receipt!
		ArrayList<User> users = new ArrayList<User>();
		EasyMock.expect(config.getUsers()).andReturn(users);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		
		Class cls;
		try {
			cls = Class.forName("commons.sim.AccountingSystem");
			Field fld = cls.getDeclaredField("requestsLostPerUser");
			Map<String, List<String>> requestsLostPerUser = (Map<String, List<String>>) fld.get(acc);
			assertNotNull(requestsLostPerUser);
			assertEquals(0, requestsLostPerUser.size());
			
			//Finishing some requests
			String userID = "us1";
			String reqID = "1999876";
			Request request = EasyMock.createStrictMock(Request.class);
			EasyMock.expect(request.getUserID()).andReturn(userID).times(2);
			EasyMock.expect(request.getRequestID()).andReturn(reqID);
			EasyMock.replay(request);
			acc.reportRequestLost(request);
			
			String user2ID = "us2";
			String req2ID = "11111";
			Request request2 = EasyMock.createStrictMock(Request.class);
			EasyMock.expect(request2.getUserID()).andReturn(user2ID).times(2);
			EasyMock.expect(request2.getRequestID()).andReturn(req2ID);
			EasyMock.replay(request2);
			acc.reportRequestLost(request2);
			
			String req3ID = "999999";
			Request request3 = EasyMock.createStrictMock(Request.class);
			EasyMock.expect(request3.getUserID()).andReturn(userID);
			EasyMock.expect(request3.getRequestID()).andReturn(req3ID);
			EasyMock.replay(request3);
			acc.reportRequestLost(request3);
			
			PowerMock.verify(Configuration.class);
			EasyMock.verify(request, request2, request3, config);
			
			//Verifying that requests for correctly computed
			requestsLostPerUser = (Map<String, List<String>>) fld.get(acc);
			assertNotNull(requestsLostPerUser);
			assertEquals(2, requestsLostPerUser.size());
			
			List<String> userRequests = requestsLostPerUser.get(userID);
			assertEquals(2, userRequests.size());
			assertTrue(userRequests.contains(reqID));
			assertTrue(userRequests.contains(req3ID));
			
			userRequests = requestsLostPerUser.get(user2ID);
			assertEquals(1, userRequests.size());
			assertTrue(userRequests.contains(req2ID));
			
		} catch (ClassNotFoundException e) {
			fail("Valid scenario!");
		} catch (SecurityException e) {
			fail("Valid scenario!");
		} catch (NoSuchFieldException e) {
			fail("Valid scenario!");
		} catch (IllegalArgumentException e) {
			fail("Valid scenario!");
		} catch (IllegalAccessException e) {
			fail("Valid scenario!");
		}
	}
}
