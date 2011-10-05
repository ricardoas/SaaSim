package provisioning;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import util.CleanConfigurationTest;

import commons.config.Configuration;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Configuration.class)
public class ProfitDrivenProvisioningSystemTest extends CleanConfigurationTest {
	
	/**
	 * This scenario verifies that if an event requesting a machine occurs when limits have been reached
	 * any other machine is added to simulator
	 */
	@Ignore@Test
	public void evaluateAddingMachinesWithLimitsReached(){
//		int resourcesReservationLimit = 1;
//		int onDemandLimit = 1;
//		
//		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
//		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(ProfitDrivenProvisioningSystem.class))).andReturn(1);
//		EasyMock.replay(scheduler);
//		
//		AccountingSystem accounting = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
//		accounting.buyMachine();
//		accounting.buyMachine();
//		
//		DynamicProvisioningSystem dps = new ProfitDrivenProvisioningSystem(scheduler);
//		dps.setAccountingSystem(accounting);
//		
//		//Event to add machine
//		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
//		EasyMock.replay(event);
//		
//		dps.handleEventRequestQueued(event);
//		
//		EasyMock.verify(scheduler, event);
//		
//		assertEquals(1, accounting.getOnDemandMachinesData().size());
//		assertEquals(1, accounting.getReservedMachinesData().size());
	}
	
	/**
	 * This scenario verifies that if an event requesting a machine occurs when limits have not 
	 * been reached another machine is added
	 */
	@Ignore@Test
	public void evaluateAddingMachinesWithReservedLimitNotReached(){
//		int resourcesReservationLimit = 2;
//		int onDemandLimit = 1;
//		
//		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
//		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(ProfitDrivenProvisioningSystem.class))).andReturn(1);
//		EasyMock.expect(scheduler.now()).andReturn(new JETime(0));
//		EasyMock.replay(scheduler);
//		
//		AccountingSystem accounting = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
//		accounting.buyMachine();
//		accounting.buyMachine();
//		
//		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
//		configurable.addServer(0, new MachineDescriptor(0, true, 0));
//		EasyMock.expectLastCall();
//		
//		DynamicProvisioningSystem dps = new ProfitDrivenProvisioningSystem(scheduler);
//		dps.setAccountingSystem(accounting);
//		dps.registerConfigurable(configurable);
//		
//		EasyMock.replay(configurable);
//		
//		//Event to add machine
//		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
//		EasyMock.replay(event);
//		
//		dps.handleEventRequestQueued(event);
//		
//		EasyMock.verify(scheduler, event, configurable);
//		
//		assertEquals(1, accounting.getOnDemandMachinesData().size());
//		assertEquals(2, accounting.getReservedMachinesData().size());
	}
	
	/**
	 * This scenario verifies that if an event requesting a machine occurs when limits have not 
	 * been reached another machine is added
	 */
	@Ignore@Test
	public void evaluateAddingMachinesWithOnDemandLimitNotReached(){
//		int resourcesReservationLimit = 1;
//		int onDemandLimit = 2;
//		
//		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
//		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(ProfitDrivenProvisioningSystem.class))).andReturn(1);
//		EasyMock.expect(scheduler.now()).andReturn(new JETime(0));
//		EasyMock.replay(scheduler);
//		
//		AccountingSystem accounting = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
//		accounting.buyMachine();
//		accounting.buyMachine();
//		
//		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
//		configurable.addServer(0, new MachineDescriptor(0, false, 0));
//		EasyMock.expectLastCall();
//		
//		DynamicProvisioningSystem dps = new ProfitDrivenProvisioningSystem(scheduler);
//		dps.setAccountingSystem(accounting);
//		dps.registerConfigurable(configurable);
//		
//		EasyMock.replay(configurable);
//		
//		//Event to add machine
//		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
//		EasyMock.replay(event);
//		
//		dps.handleEventRequestQueued(event);
//		
//		EasyMock.verify(scheduler, event, configurable);
//		
//		assertEquals(2, accounting.getOnDemandMachinesData().size());
//		assertEquals(1, accounting.getReservedMachinesData().size());
	}
}

