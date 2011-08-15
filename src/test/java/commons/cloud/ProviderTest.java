package commons.cloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.config.Configuration;
import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.SimulatorProperties;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Configuration.class)
public class ProviderTest {
	
	private String name = "prov";
	private static final long HOUR_IN_MILLIS = 1000 * 60 * 60;
	private JEEventScheduler scheduler;
	
	@Before
	public void setUp() throws ClassNotFoundException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		scheduler = new JEEventScheduler();
		Class c = Class.forName("commons.cloud.Provider");
		Field field = c.getDeclaredField("machineIDGenerator");
		field.set(null, 0);
	}

	@Test
	public void providerWithInvalidCpuCost(){
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.01;
		double reservationOneYearFee = 100;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.2;
		long[] transferInLimits = {};
		double[] transferInCosts = {};
		long[] transferOutLimits = {};
		double[] transferOutCosts = {};
		
		try{
			new Provider(name, -0.09, onDemandLimit, reservationLimit, 
					reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void providerWithInvalidCpuLimit(){
		double onDemandCpuCost = 0.1;
		int reservationLimit = 20;
		double reservedCpuCost = 0.01;
		double reservationOneYearFee = 100;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.2;
		long[] transferInLimits = {};
		double[] transferInCosts = {};
		long[] transferOutLimits = {};
		double[] transferOutCosts = {};
		
		try{
			new Provider(name, onDemandCpuCost, -99999, reservationLimit, 
					reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
		
		try{
			new Provider(name, onDemandCpuCost, 0, reservationLimit, 
					reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void providerWithInvalidReservedCpuLimit(){
		double onDemandCpuCost = 0.1;
		int onDemandLimit = 20;
		double reservedCpuCost = 0.01;
		double reservationOneYearFee = 100;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.2;
		long[] transferInLimits = {};
		double[] transferInCosts = {};
		long[] transferOutLimits = {};
		double[] transferOutCosts = {};
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, 0, 
					reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, -777, 
					reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void providerWithInvalidReservedCpuCost(){
		double onDemandCpuCost = 0.1;
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.01;
		double reservationOneYearFee = 100;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.2;
		long[] transferInLimits = {};
		double[] transferInCosts = {};
		long[] transferOutLimits = {};
		double[] transferOutCosts = {};
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					-reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					-13.9787656, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void providerWithInvalidReservationFee(){
		double onDemandCpuCost = 0.1;
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.01;
		double reservationOneYearFee = 100;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.2;
		long[] transferInLimits = {};
		double[] transferInCosts = {};
		long[] transferOutLimits = {};
		double[] transferOutCosts = {};
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					reservedCpuCost, -reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					reservedCpuCost, 0, reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		}catch(RuntimeException e){
			fail("Invalid provider!");
		}
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					reservedCpuCost, reservationOneYearFee, -reservationThreeYearsFee, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					reservedCpuCost, reservationOneYearFee, 0, monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		}catch(RuntimeException e){
			fail("Invalid provider!");
		}
	}
	
	@Test
	public void providerWithInvalidMonitoringCost(){
		double onDemandCpuCost = 0.1;
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.01;
		double reservationOneYearFee = 100;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.2;
		long[] transferInLimits = {};
		double[] transferInCosts = {};
		long[] transferOutLimits = {};
		double[] transferOutCosts = {};
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, -monitoringCost, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
		
		try{
			new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
					reservedCpuCost, 0, reservationThreeYearsFee, -0.0000001, 
					transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
			fail("Invalid provider!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void calculateCostForReservedResourcesAlreadyFinished(){
		double onDemandCpuCost = 0.1;
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.01;
		double reservationOneYearFee = 100;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.2;
		long[] transferInLimits = {};
		double[] transferInCosts = {};
		long[] transferOutLimits = {};
		double[] transferOutCosts = {};
		
		Provider provider = new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		
		//Adding reserved resources
		Long machine1ID = 0l;
		provider.buyMachine(true);
		MachineDescriptor descriptor = new MachineDescriptor(machine1ID, true);
		descriptor.setStartTimeInMillis(1 * HOUR_IN_MILLIS);
		descriptor.setFinishTimeInMillis(5 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor);
		
		Long machine2ID = 1l;
		provider.buyMachine(true);
		MachineDescriptor descriptor2 = new MachineDescriptor(machine2ID, true);
		descriptor2.setStartTimeInMillis(0);
		descriptor2.setFinishTimeInMillis(5 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor2);
		
		Long machine3ID = 2l;
		provider.buyMachine(true);
		MachineDescriptor descriptor3 = new MachineDescriptor(machine3ID, true);
		descriptor3.setStartTimeInMillis(3 * HOUR_IN_MILLIS);
		descriptor3.setFinishTimeInMillis(18 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor3);
		
		Long machine4ID = 3l;
		provider.buyMachine(true);
		MachineDescriptor descriptor4 = new MachineDescriptor(machine4ID, true);
		descriptor4.setStartTimeInMillis(0);
		descriptor4.setFinishTimeInMillis(15 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor4);
		
		Long machine5ID = 4l;
		provider.buyMachine(true);
		MachineDescriptor descriptor5 = new MachineDescriptor(machine5ID, true);
		descriptor5.setStartTimeInMillis(5 * HOUR_IN_MILLIS);
		descriptor5.setFinishTimeInMillis((long)(17.5d * HOUR_IN_MILLIS));
		provider.shutdownMachine(descriptor5);
		
		Long machine6ID = 5l;
		provider.buyMachine(true);
		MachineDescriptor descriptor6 = new MachineDescriptor(machine6ID, true);
		descriptor6.setStartTimeInMillis((long)(0.5d * HOUR_IN_MILLIS));
		descriptor6.setFinishTimeInMillis((long)(15.73d * HOUR_IN_MILLIS));
		provider.shutdownMachine(descriptor6);
		
		//Mocks
		PowerMock.mockStatic(Configuration.class);
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getInt(SimulatorProperties.PLANNING_PERIOD)).andReturn(1);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		assertEquals(6 * reservationOneYearFee + 68 * reservedCpuCost + 68 * monitoringCost, provider.calculateCost(20 * HOUR_IN_MILLIS, 0), 0.0d);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	@Test
	public void calculateCostForReservedResourcesNotFinished(){
		double onDemandCpuCost = 0.1;
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.01;
		double reservationOneYearFee = 100;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.2;
		long[] transferInLimits = {};
		double[] transferInCosts = {};
		long[] transferOutLimits = {};
		double[] transferOutCosts = {};
		
		Provider provider = new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		
		//Adding reserved resources
		provider.buyMachine(true);
		provider.buyMachine(true);
		provider.buyMachine(true);
		provider.buyMachine(true);
		provider.buyMachine(true);
		provider.buyMachine(true);
		
		//Mocks
		PowerMock.mockStatic(Configuration.class);
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getInt(SimulatorProperties.PLANNING_PERIOD)).andReturn(1);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		assertEquals(6 * reservationOneYearFee + 120 * reservedCpuCost + 120 * monitoringCost, provider.calculateCost(20 * HOUR_IN_MILLIS, 0), 0.0d);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
	
	@Test
	public void calculateCostForReservedResourcesWithInvalidDuration(){
		double onDemandCpuCost = 0.1;
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.01;
		double reservationOneYearFee = 100;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.2;
		long[] transferInLimits = {};
		double[] transferInCosts = {};
		long[] transferOutLimits = {};
		double[] transferOutCosts = {};
		
		Provider provider = new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		
		//Adding reserved resources
		Long machine1ID = 0l;
		provider.buyMachine(true);
		MachineDescriptor descriptor = new MachineDescriptor(machine1ID, true);
		descriptor.setStartTimeInMillis(0);
		descriptor.setFinishTimeInMillis((long)(-5d * HOUR_IN_MILLIS));
		provider.shutdownMachine(descriptor);
		
		try{
			provider.calculateCost(1 * HOUR_IN_MILLIS, 0);
			fail("Invalid resource consumption!");
		}catch(RuntimeException e){
		}
	}
	
	@Test
	public void calculateCostOnDemandResourcesAlreadyFinished(){
		double onDemandCpuCost = 0.85;
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.35;
		double reservationOneYearFee = 99.123;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.15;
		long[] transferInLimits = {};
		double[] transferInCosts = {};
		long[] transferOutLimits = {};
		double[] transferOutCosts = {};
		
		Provider provider = new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		
		//Adding on-demand resources
		Long machine1ID = 0l;
		provider.buyMachine(false);
		MachineDescriptor descriptor = new MachineDescriptor(machine1ID, false);
		descriptor.setStartTimeInMillis(0);
		descriptor.setFinishTimeInMillis(2 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor);
		
		Long machine2ID = 1l;
		provider.buyMachine(false);
		MachineDescriptor descriptor2 = new MachineDescriptor(machine2ID, false);
		descriptor2.setStartTimeInMillis(1 * HOUR_IN_MILLIS);
		descriptor2.setFinishTimeInMillis(2 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor2);
		
		Long machine3ID = 2l;
		provider.buyMachine(false);
		MachineDescriptor descriptor3 = new MachineDescriptor(machine3ID, false);
		descriptor3.setStartTimeInMillis(0);
		descriptor3.setFinishTimeInMillis(15 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor3);
		
		Long machine4ID = 3l;
		provider.buyMachine(false);
		MachineDescriptor descriptor4 = new MachineDescriptor(machine4ID, false);
		descriptor4.setStartTimeInMillis(1 * HOUR_IN_MILLIS);
		descriptor4.setFinishTimeInMillis((long)(2.2d * HOUR_IN_MILLIS));
		provider.shutdownMachine(descriptor4);
		
		Long machine5ID = 4l;
		provider.buyMachine(false);
		MachineDescriptor descriptor5 = new MachineDescriptor(machine5ID, false);
		descriptor5.setStartTimeInMillis(2 * HOUR_IN_MILLIS);
		descriptor5.setFinishTimeInMillis((long)(7.14d * HOUR_IN_MILLIS));
		provider.shutdownMachine(descriptor5);
		
		assertEquals( 26 * onDemandCpuCost + 26 * monitoringCost, provider.calculateCost(20 * HOUR_IN_MILLIS, 0), 0.0001d);
	}
	
	@Test
	public void calculateCostOnDemandResourcesNotFinished(){
		double onDemandCpuCost = 0.85;
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.35;
		double reservationOneYearFee = 99.123;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.15;
		long[] transferInLimits = {};
		double[] transferInCosts = {};
		long[] transferOutLimits = {};
		double[] transferOutCosts = {};
		
		Provider provider = new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		
		//Adding on-demand resources
		provider.buyMachine(false);
		provider.buyMachine(false);
		provider.buyMachine(false);
		provider.buyMachine(false);
		provider.buyMachine(false);
		
		assertEquals( 100 * onDemandCpuCost + 100 * monitoringCost, provider.calculateCost(20 * HOUR_IN_MILLIS, 0), 0.0001d);
	}
	
	@Test
	public void calculateCostOnDemandResourcesWithInvalidDuration(){
		double onDemandCpuCost = 0.85;
		int onDemandLimit = 20;
		int reservationLimit = 20;
		double reservedCpuCost = 0.35;
		double reservationOneYearFee = 99.123;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.15;
		long[] transferInLimits = {};
		double[] transferInCosts = {};
		long[] transferOutLimits = {};
		double[] transferOutCosts = {};
		
		Provider provider = new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		
		//Adding reserved resources
		Long machine1ID = 0l;
		provider.buyMachine(false);
		MachineDescriptor descriptor = new MachineDescriptor(machine1ID, true);
		descriptor.setStartTimeInMillis(0);
		descriptor.setFinishTimeInMillis((long)(-6d * HOUR_IN_MILLIS));
		provider.shutdownMachine(descriptor);
		
		try{
			provider.calculateCost(1 * HOUR_IN_MILLIS, 0);
			fail("Invalid resource consumption!");
		}catch(RuntimeException e){
			System.err.println(e.getMessage());
		}
		
		//FIXME: Double value explodes with large value!
		provider = new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		provider.buyMachine(false);
		Long machine2ID = 1l;
		MachineDescriptor descriptor2 = new MachineDescriptor(machine2ID, true);
		descriptor2.setStartTimeInMillis(1000 * HOUR_IN_MILLIS);
		descriptor2.setFinishTimeInMillis(400 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor2);
		
		try{
			provider.calculateCost(500 * HOUR_IN_MILLIS, 0);
			fail("Invalid resource consumption!");
		}catch(RuntimeException e){
			System.err.println(e.getMessage());
		}
	}
	
	@Test
	public void calculateCostForReservedAndOnDemandResources(){
		double onDemandCpuCost = 0.12;
		int onDemandLimit = 30;
		int reservationLimit = 20;
		double reservedCpuCost = 0.085;
		double reservationOneYearFee = 227.50;
		double reservationThreeYearsFee = 70;
		double monitoringCost = 0.15;
		long[] transferInLimits = {};
		double[] transferInCosts = {};
		long[] transferOutLimits = {};
		double[] transferOutCosts = {};
		
		Provider provider = new Provider(name, onDemandCpuCost, onDemandLimit, reservationLimit, 
				reservedCpuCost, reservationOneYearFee, reservationThreeYearsFee, monitoringCost, 
				transferInLimits, transferInCosts, transferOutLimits, transferOutCosts);
		
		//Adding reserved resources
		Long machine1ID = 0l;
		provider.buyMachine(true);
		MachineDescriptor descriptor = new MachineDescriptor(machine1ID, true);
		descriptor.setStartTimeInMillis(0);
		descriptor.setFinishTimeInMillis(10 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor);
		
		Long machine2ID = 1l;
		provider.buyMachine(true);
		MachineDescriptor descriptor2 = new MachineDescriptor(machine2ID, true);
		descriptor2.setStartTimeInMillis(10 * HOUR_IN_MILLIS);
		descriptor2.setFinishTimeInMillis(30 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor2);
		
		Long machine3ID = 2l;
		provider.buyMachine(true);
		MachineDescriptor descriptor3 = new MachineDescriptor(machine3ID, true);
		descriptor3.setStartTimeInMillis(1 * HOUR_IN_MILLIS);
		descriptor3.setFinishTimeInMillis(16 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor3);
		
		Long machine4ID = 3l;
		provider.buyMachine(true);
		MachineDescriptor descriptor4 = new MachineDescriptor(machine4ID, true);
		descriptor4.setStartTimeInMillis(50 * HOUR_IN_MILLIS);
		descriptor4.setFinishTimeInMillis(65 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor4);
		
		Long machine5ID = 4l;
		provider.buyMachine(true);
		MachineDescriptor descriptor5 = new MachineDescriptor(machine5ID, true);
		descriptor5.setStartTimeInMillis(0);
		descriptor5.setFinishTimeInMillis(15 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor5);
		
		Long machine6ID = 5l;
		provider.buyMachine(true);
		MachineDescriptor descriptor6 = new MachineDescriptor(machine6ID, true);
		descriptor6.setStartTimeInMillis((long)(11.5d * HOUR_IN_MILLIS));
		descriptor6.setFinishTimeInMillis(24 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor6);
		
		Long machine7ID = 6l;
		provider.buyMachine(true);
		MachineDescriptor descriptor7 = new MachineDescriptor(machine7ID, true);
		descriptor7.setStartTimeInMillis(0);
		descriptor7.setFinishTimeInMillis((long)(15.23d * HOUR_IN_MILLIS));
		provider.shutdownMachine(descriptor7);
		
		//On-demand resources
		Long machine8ID = 7l;
		provider.buyMachine(false);
		MachineDescriptor descriptor8 = new MachineDescriptor(machine8ID, false);
		descriptor8.setStartTimeInMillis(18 * HOUR_IN_MILLIS);
		descriptor8.setFinishTimeInMillis(36 * HOUR_IN_MILLIS);
		provider.shutdownMachine(descriptor8);
		
		Long machine9ID = 8l;
		provider.buyMachine(false);
		MachineDescriptor descriptor9 = new MachineDescriptor(machine9ID, false);
		descriptor9.setStartTimeInMillis(0);
		descriptor9.setFinishTimeInMillis((long)(78.5d * HOUR_IN_MILLIS));
		provider.shutdownMachine(descriptor9);
		
		Long machine10ID = 9l;
		provider.buyMachine(false);
		MachineDescriptor descriptor10 = new MachineDescriptor(machine10ID, false);
		descriptor10.setStartTimeInMillis((long)(2.4d * HOUR_IN_MILLIS));
		descriptor10.setFinishTimeInMillis((long)(3.6d * HOUR_IN_MILLIS));
		provider.shutdownMachine(descriptor10);
		
		Long machine11ID = 10l;
		provider.buyMachine(false);
		MachineDescriptor descriptor11 = new MachineDescriptor(machine11ID, false);
		descriptor11.setStartTimeInMillis(0);
		descriptor11.setFinishTimeInMillis((long)(5.14d * HOUR_IN_MILLIS));
		provider.shutdownMachine(descriptor11);
		
		//Mocks
		PowerMock.mockStatic(Configuration.class);
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getInt(SimulatorProperties.PLANNING_PERIOD)).andReturn(1);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		assertEquals( 7 * reservationOneYearFee + 104 * reservedCpuCost + 104 * monitoringCost +
				105 * onDemandCpuCost + 105 * monitoringCost, provider.calculateCost(80 * HOUR_IN_MILLIS, 0), 0.00001d);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(config);
	}
}