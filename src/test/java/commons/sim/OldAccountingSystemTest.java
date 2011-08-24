package commons.sim;

import static commons.sim.util.SimulatorProperties.PLANNING_PERIOD;
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
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.TypeProvider;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.config.Configuration;
import commons.sim.components.MachineDescriptor;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Configuration.class)
public class OldAccountingSystemTest {
	
	private long ONE_HOUR_IN_MILLIS = 1000 * 60 * 60; 
	
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getLong(PLANNING_PERIOD)).andReturn(1l);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		acc.buyMachine();
		acc.buyMachine();
		
		//Utility calculus depends on current time!
		double reservedCost = reservationOneYearFee + reservedCpuCost + monitoringCost;
		double onDemandCost = onDemandCpuCost + monitoringCost;
		
		UtilityResult result = acc.accountPartialUtility(ONE_HOUR_IN_MILLIS);
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getLong(PLANNING_PERIOD)).andReturn(1l);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		acc.buyMachine();
		acc.buyMachine();
		
		//Utility calculus depends on current time!
		double reservedCost = reservationOneYearFee + 3 * reservedCpuCost + 3 * monitoringCost;
		double onDemandCost = 3 * onDemandCpuCost + 3 * monitoringCost;
		
		UtilityResult result = acc.accountPartialUtility(3 * ONE_HOUR_IN_MILLIS);
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		EasyMock.expect(config.getUsers()).andReturn(new ArrayList<User>());
		EasyMock.expect(config.getLong(PLANNING_PERIOD)).andReturn(1l);
		
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
		
		UtilityResult result = acc.accountPartialUtility(20 * ONE_HOUR_IN_MILLIS);
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
		user.update((long)(11.5 * ONE_HOUR_IN_MILLIS), 0, 0);//Partial hour is billed as a full hour
		
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		
		//Setting users that create the receipt!
		ArrayList<User> users = new ArrayList<User>();
		users.add(user);
		EasyMock.expect(config.getUsers()).andReturn(users);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		AccountingSystem acc = new AccountingSystem();
		
		UtilityResult result = acc.accountPartialUtility(12 * ONE_HOUR_IN_MILLIS);
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		
		//Setting users that create the receipt!
		ArrayList<User> users = new ArrayList<User>();
		Contract contract = new Contract("p1", 0, 0, price, cpuLimitInHours, extraCpuCost, new long[]{}, new double[]{});
		User user = new User(contract);
		user.update(11 * ONE_HOUR_IN_MILLIS, 0, 0);//This user exceeds the limit!
		users.add(user);
		users.add(new User(contract));
		EasyMock.expect(config.getUsers()).andReturn(users);
		
		EasyMock.expect(config.getLong(PLANNING_PERIOD)).andReturn(1l);
		
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
		
		UtilityResult result = acc.accountPartialUtility(20 * ONE_HOUR_IN_MILLIS);
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		
		//Setting users that create the receipt!
		ArrayList<User> users = new ArrayList<User>();
		Contract contract = new Contract("p1", 0, 0, price, cpuLimitInHours, extraCpuCost, new long[]{}, new double[]{});
		User user = new User(contract);
		user.update(11 * ONE_HOUR_IN_MILLIS, 0, 0);//This user exceeds the limit!
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
		
		UtilityResult result = acc.accountPartialUtility(20 * ONE_HOUR_IN_MILLIS);
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
		Provider provider = new Provider("p1", onDemandLimit, reservationLimit, monitoringCost, new long[]{}, new double[]{}, new long[]{}, new double[]{}, new ArrayList<TypeProvider>());
		providers.add(provider);
		EasyMock.expect(config.getProviders()).andReturn(providers);
		
		//Setting users that create the receipt!
		ArrayList<User> users = new ArrayList<User>();
		Contract contract = new Contract("p1", 0, 0, price, cpuLimitInHours, extraCpuCost, new long[]{}, new double[]{});
		User user = new User(contract);
		user.update(11 * ONE_HOUR_IN_MILLIS, 0, 0);//This user exceeds the limit!
		users.add(user);
		users.add(new User(contract));
		EasyMock.expect(config.getUsers()).andReturn(users);
		
		EasyMock.expect(config.getLong(PLANNING_PERIOD)).andReturn(1l);
		
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
		
		UtilityResult result = acc.accountPartialUtility(20 * ONE_HOUR_IN_MILLIS);
		assertEquals(0, result.getCost(), 0.00001);
		assertEquals(receipt, result.getReceipt(), 0.0);
		assertEquals(0, result.getPenalty(), 0.0);
		assertEquals(receipt, result.getUtility(), 0.00001);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
}
