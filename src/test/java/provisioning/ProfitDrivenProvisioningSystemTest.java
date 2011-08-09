package provisioning;

import static org.junit.Assert.assertEquals;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.config.SimulatorConfiguration;
import commons.sim.AccountingSystem;
import commons.sim.SimpleSimulator;
import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JETime;

@RunWith(PowerMockRunner.class)
public class ProfitDrivenProvisioningSystemTest {
	
	/**
	 * This scenario verifies that if an event requesting a machine occurs when limits have been reached
	 * any other machine is added to simulator
	 */
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void evaluateAddingMachinesWithLimitsReached(){
		int resourcesReservationLimit = 1;
		int onDemandLimit = 1;
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(ProfitDrivenProvisioningSystem.class))).andReturn(1);
		EasyMock.replay(scheduler);
		
		AccountingSystem accounting = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		accounting.createMachine(new MachineDescriptor(1, true, 0));
		accounting.createMachine(new MachineDescriptor(2, false, 0));
		
		ProfitDrivenProvisioningSystem dps = new ProfitDrivenProvisioningSystem(scheduler);
		dps.setAccountingSystem(accounting);
		
		//Event to add machine
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.replay(event);
		
		dps.handleEventRequestQueued(event);
		
		EasyMock.verify(scheduler, event);
		
		assertEquals(1, accounting.getOnDemandMachinesData().size());
		assertEquals(1, accounting.getReservedMachinesData().size());
	}
	
	/**
	 * This scenario verifies that if an event requesting a machine occurs when limits have not 
	 * been reached another machine is added
	 */
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void evaluateAddingMachinesWithReservedLimitNotReached(){
		int resourcesReservationLimit = 2;
		int onDemandLimit = 1;
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(ProfitDrivenProvisioningSystem.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0));
		EasyMock.replay(scheduler);
		
		AccountingSystem accounting = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		accounting.createMachine(new MachineDescriptor(1, true, 0));
		accounting.createMachine(new MachineDescriptor(2, false, 0));
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		configurable.addServer(0, new MachineDescriptor(0, true, 0));
		EasyMock.expectLastCall();
		
		ProfitDrivenProvisioningSystem dps = new ProfitDrivenProvisioningSystem(scheduler);
		dps.setAccountingSystem(accounting);
		dps.setConfigurable(configurable);
		
		EasyMock.replay(configurable);
		
		//Event to add machine
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.replay(event);
		
		dps.handleEventRequestQueued(event);
		
		EasyMock.verify(scheduler, event, configurable);
		
		assertEquals(1, accounting.getOnDemandMachinesData().size());
		assertEquals(2, accounting.getReservedMachinesData().size());
	}
	
	/**
	 * This scenario verifies that if an event requesting a machine occurs when limits have not 
	 * been reached another machine is added
	 */
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void evaluateAddingMachinesWithOnDemandLimitNotReached(){
		int resourcesReservationLimit = 1;
		int onDemandLimit = 2;
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(ProfitDrivenProvisioningSystem.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0));
		EasyMock.replay(scheduler);
		
		AccountingSystem accounting = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		accounting.createMachine(new MachineDescriptor(1, true, 0));
		accounting.createMachine(new MachineDescriptor(2, false, 0));
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		configurable.addServer(0, new MachineDescriptor(0, false, 0));
		EasyMock.expectLastCall();
		
		ProfitDrivenProvisioningSystem dps = new ProfitDrivenProvisioningSystem(scheduler);
		dps.setAccountingSystem(accounting);
		dps.setConfigurable(configurable);
		
		EasyMock.replay(configurable);
		
		//Event to add machine
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.replay(event);
		
		dps.handleEventRequestQueued(event);
		
		EasyMock.verify(scheduler, event, configurable);
		
		assertEquals(2, accounting.getOnDemandMachinesData().size());
		assertEquals(1, accounting.getReservedMachinesData().size());
	}
}

