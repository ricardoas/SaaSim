package provisioning;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.config.SimulatorConfiguration;
import commons.sim.AccountingSystem;
import commons.sim.SimpleSimulator;
import commons.sim.components.ProcessorSharedMachine;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JETime;
import commons.sim.schedulingheuristics.ProfitDrivenHeuristic;
import commons.sim.schedulingheuristics.RanjanHeuristic;

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
		accounting.createMachine(1, true, 0);
		accounting.createMachine(2, false, 0);
		
		ProfitDrivenProvisioningSystem dps = new ProfitDrivenProvisioningSystem(scheduler);
		dps.setAccountingSystem(accounting);
		
		SimulatorConfiguration config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config);
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(new Class<?>[] {RanjanHeuristic.class});
		
		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(config);
		
		//Event to add machine
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.replay(event);
		
		dps.handleEventRequestQueued(event);
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(scheduler, event, config);
		
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
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(ProcessorSharedMachine.class))).andReturn(2);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(ProfitDrivenProvisioningSystem.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(ProcessorSharedMachine.class))).andReturn(2);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0));
		EasyMock.replay(scheduler);
		
		AccountingSystem accounting = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		accounting.createMachine(1, true, 0);
		accounting.createMachine(2, false, 0);
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		configurable.addServer(0, new ProcessorSharedMachine(scheduler, 0));
		EasyMock.expectLastCall();
		
		ProfitDrivenProvisioningSystem dps = new ProfitDrivenProvisioningSystem(scheduler);
		dps.setAccountingSystem(accounting);
		dps.setConfigurable(configurable);
		
		SimulatorConfiguration config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config);
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(new Class<?>[] {ProfitDrivenHeuristic.class});
		
		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(config, configurable);
		
		//Event to add machine
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.replay(event);
		
		dps.handleEventRequestQueued(event);
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(scheduler, event, config, configurable);
		
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
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(ProcessorSharedMachine.class))).andReturn(2);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(ProfitDrivenProvisioningSystem.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(ProcessorSharedMachine.class))).andReturn(3);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0));
		EasyMock.replay(scheduler);
		
		AccountingSystem accounting = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		accounting.createMachine(1, true, 0);
		accounting.createMachine(2, false, 0);
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		configurable.addServer(0, new ProcessorSharedMachine(scheduler, 0, false));
		EasyMock.expectLastCall();
		
		ProfitDrivenProvisioningSystem dps = new ProfitDrivenProvisioningSystem(scheduler);
		dps.setAccountingSystem(accounting);
		dps.setConfigurable(configurable);
		
		SimulatorConfiguration config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config);
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(new Class<?>[] {ProfitDrivenHeuristic.class});
		
		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(config, configurable);
		
		//Event to add machine
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.replay(event);
		
		dps.handleEventRequestQueued(event);
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(scheduler, event, config, configurable);
		
		assertEquals(2, accounting.getOnDemandMachinesData().size());
		assertEquals(1, accounting.getReservedMachinesData().size());
	}
}
