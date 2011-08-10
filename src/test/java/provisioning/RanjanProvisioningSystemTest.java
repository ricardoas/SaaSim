package provisioning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.config.SimulatorConfiguration;
import commons.sim.AccountingSystem;
import commons.sim.SimpleSimulator;
import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JETime;
import commons.sim.provisioningheuristics.RanjanStatistics;

@RunWith(PowerMockRunner.class)
public class RanjanProvisioningSystemTest {
	
	private static final long ONE_HOUR_IN_MILLIS = 1000 * 60 * 60;
	private RanjanProvisioningSystem heuristic;

	@Test
	public void testWithEmptyStatistics(){
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(RanjanProvisioningSystem.class))).andReturn(1);
		EasyMock.replay(scheduler);
		
		this.heuristic = new RanjanProvisioningSystem(scheduler);
		try{
			this.heuristic.evaluateNumberOfServersForNextInterval(null);
			fail("Null statistics!");
		}catch(NullPointerException e){
		}
		
		EasyMock.verify(scheduler);
	}
	
	/**
	 * This scenario verifies that an utilization that is not so much greater than the target
	 * utilization (0.66) indicates that a small number of servers should be added
	 */
	@Test
	public void evaluateNumberOfServersWithASingleServer(){
		double totalUtilization = 0.7;
		long totalRequestsArrivals = 10;
		long totalRequestsCompletions = 10;
		long totalNumberOfServers = 1;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(RanjanProvisioningSystem.class))).andReturn(1);
		EasyMock.replay(scheduler);
		this.heuristic = new RanjanProvisioningSystem(scheduler);
		
		long serversForNextInterval = this.heuristic.evaluateNumberOfServersForNextInterval(statistics);
		assertEquals(1, serversForNextInterval);
		
		EasyMock.verify(scheduler);
	}
	
	/**
	 * This scenario verifies that an utilization that is so much greater than the target
	 * utilization (0.66), and so the number of completions is small, indicates that a 
	 * large number of servers should be added
	 */
	@Test
	public void evaluateNumberOfServersWithMultipleServers(){
		double totalUtilization = 3.0;
		long totalRequestsArrivals = 55;
		long totalRequestsCompletions = 15;
		long totalNumberOfServers = 3;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(RanjanProvisioningSystem.class))).andReturn(1);
		EasyMock.replay(scheduler);
		this.heuristic = new RanjanProvisioningSystem(scheduler);
		
		long serversForNextInterval = this.heuristic.evaluateNumberOfServersForNextInterval(statistics);
		assertEquals(14, serversForNextInterval);
		
		EasyMock.verify(scheduler);
	}
	
	/**
	 * This scenario verifies that an utilization that is lower than the target
	 * utilization (0.66), and so the number of completions is high, indicates that a 
	 * large number of servers should be removed
	 */
	@Test
	public void evaluateNumberOfServersConsideringMultipleServersWithLowUtilization(){
		double totalUtilization = 6;
		long totalRequestsArrivals = 333;
		long totalRequestsCompletions = 279;
		long totalNumberOfServers = 20;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(RanjanProvisioningSystem.class))).andReturn(1);
		EasyMock.replay(scheduler);
		this.heuristic = new RanjanProvisioningSystem(scheduler);
		
		long serversForNextInterval = this.heuristic.evaluateNumberOfServersForNextInterval(statistics);
		assertEquals(-9, serversForNextInterval);
		
		EasyMock.verify(scheduler);
	}
	
	/**
	 * This scenario verifies that an utilization that is so much lower than the target
	 * utilization (0.66), and so the number of completions is high, indicates that a 
	 * minimal amount of servers should remain in the infrastructure
	 */
	@Test
	public void evaluateNumberOfServersConsideringMultipleServersWithLowUtilization2(){
		double totalUtilization = 0.2;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 100;
		long totalNumberOfServers = 20;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(RanjanProvisioningSystem.class))).andReturn(1);
		EasyMock.replay(scheduler);
		this.heuristic = new RanjanProvisioningSystem(scheduler);
		
		long serversForNextInterval = this.heuristic.evaluateNumberOfServersForNextInterval(statistics);
		assertEquals(-19, serversForNextInterval);
		
		EasyMock.verify(scheduler);
	}
	
	/**
	 * This scenario verifies a scenario where no demand has occurred
	 */
	@Test
	public void evaluateNumberOfServersConsideringMultipleServersWithLowUtilization3(){
		double totalUtilization = 0.0;
		long totalRequestsArrivals = 0;
		long totalRequestsCompletions = 0;
		long totalNumberOfServers = 20;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(RanjanProvisioningSystem.class))).andReturn(1);
		EasyMock.replay(scheduler);
		this.heuristic = new RanjanProvisioningSystem(scheduler);
		
		long serversForNextInterval = this.heuristic.evaluateNumberOfServersForNextInterval(statistics);
		assertEquals(-20, serversForNextInterval);
		
		EasyMock.verify(scheduler);
	}
	
	/**
	 * This scenario verifies a scenario where no machines were available and a high demand
	 * has arrived
	 */
	@Test
	public void evaluateNumberOfServersWithoutPreviousMachines(){
		double totalUtilization = 0.0;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 0;
		long totalNumberOfServers = 0;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(RanjanProvisioningSystem.class))).andReturn(1);
		EasyMock.replay(scheduler);
		this.heuristic = new RanjanProvisioningSystem(scheduler);
		
		long serversForNextInterval = this.heuristic.evaluateNumberOfServersForNextInterval(statistics);
		assertEquals(1, serversForNextInterval);
		
		EasyMock.verify(scheduler);
	}
	
	/**
	 * This scenarios verifies that after evaluating that a machine should be added, the RANJAN provisioning
	 * system creates a machine and adds it to simulator.
	 */
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void handleEventEvaluateUtilizationWithOneServerToBeAdded(){
		int resourcesReservationLimit = 20;
		int onDemandLimit = 20;

		double totalUtilization = 0.0;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 0;
		long totalNumberOfServers = 0;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(RanjanProvisioningSystem.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0));
		EasyMock.replay(scheduler);
		
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.expect(event.getValue()).andReturn(new RanjanStatistics[]{statistics});
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		configurable.addServer(0, new MachineDescriptor(0, true, 0));
		EasyMock.expectLastCall();
		
		EasyMock.replay(event, configurable);
		
		this.heuristic = new RanjanProvisioningSystem(scheduler);
		this.heuristic.setConfigurable(configurable);
		this.heuristic.setAccountingSystem(new AccountingSystem(resourcesReservationLimit, onDemandLimit));
		
		this.heuristic.handleEventEvaluateUtilization(event);
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(scheduler, event, configurable);
		
		assertEquals(1, this.heuristic.getAccountingSystem().getReservedMachinesData().size());
		assertEquals(0, this.heuristic.getAccountingSystem().getOnDemandMachinesData().size());
	}
	
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void handleEventEvaluateUtilizationWithOneServerToBeAddedAndLimitsReached(){
		int resourcesReservationLimit = 1;
		int onDemandLimit = 1;

		double totalUtilization = 0.0;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 0;
		long totalNumberOfServers = 0;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(RanjanProvisioningSystem.class))).andReturn(1);
		EasyMock.replay(scheduler);
		
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.expect(event.getValue()).andReturn(new RanjanStatistics[]{statistics});
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		
		EasyMock.replay(event, configurable);
		
		this.heuristic = new RanjanProvisioningSystem(scheduler);
		this.heuristic.setConfigurable(configurable);
		
		AccountingSystem system = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		system.createMachine(new MachineDescriptor(1, true, 0));
		system.createMachine(new MachineDescriptor(2, false, 0));
		this.heuristic.setAccountingSystem(system);
		
		this.heuristic.handleEventEvaluateUtilization(event);
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(scheduler, event, configurable);
	}
	
	/**
	 * This scenarios verifies that after evaluating that nineteen machines should be removed, some calls
	 * to simulator are performed.
	 */
	@Test
	public void handleEventEvaluateUtilizationWithServersToRemove(){
		int resourcesReservationLimit = 20;
		int onDemandLimit = 20;

		double totalUtilization = 0.2;
		long totalRequestsArrivals = 100;
		long totalRequestsCompletions = 100;
		long totalNumberOfServers = 20;
		RanjanStatistics statistics = new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, totalNumberOfServers);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(RanjanProvisioningSystem.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(ONE_HOUR_IN_MILLIS * 2)).times(6);
		EasyMock.replay(scheduler);
		
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.expect(event.getValue()).andReturn(new RanjanStatistics[]{statistics});
		
		SimpleSimulator configurable = EasyMock.createMock(SimpleSimulator.class);
		configurable.removeServer(0, new MachineDescriptor(1, true, 0), false);
		EasyMock.expectLastCall();
		configurable.removeServer(0, new MachineDescriptor(2, true, 0), false);
		EasyMock.expectLastCall();
		configurable.removeServer(0, new MachineDescriptor(3, true, 0), false);
		EasyMock.expectLastCall();
		configurable.removeServer(0, new MachineDescriptor(4, false, 0), false);
		EasyMock.expectLastCall();
		configurable.removeServer(0, new MachineDescriptor(5, false, 0), false);
		EasyMock.expectLastCall();
		configurable.removeServer(0, new MachineDescriptor(6, false, 0), false);
		EasyMock.expectLastCall();
		
		EasyMock.replay(event, configurable);
		
		//Creating some machines to be removed
		AccountingSystem accountingSystem = new AccountingSystem(resourcesReservationLimit, onDemandLimit);
		accountingSystem.createMachine(new MachineDescriptor(1, true, 0));
		accountingSystem.createMachine(new MachineDescriptor(2, true, 0));
		accountingSystem.createMachine(new MachineDescriptor(3, true, 0));
		accountingSystem.createMachine(new MachineDescriptor(4, false, ONE_HOUR_IN_MILLIS));
		accountingSystem.createMachine(new MachineDescriptor(5, false, ONE_HOUR_IN_MILLIS));
		accountingSystem.createMachine(new MachineDescriptor(6, false, ONE_HOUR_IN_MILLIS));
		
		this.heuristic = new RanjanProvisioningSystem(scheduler);
		this.heuristic.setConfigurable(configurable);
		this.heuristic.setAccountingSystem(accountingSystem);
		
		this.heuristic.handleEventEvaluateUtilization(event);
		
		EasyMock.verify(scheduler, event, configurable);
		
		assertEquals(0, this.heuristic.getAccountingSystem().getReservedMachinesData().size());
		assertEquals(0, this.heuristic.getAccountingSystem().getOnDemandMachinesData().size());
	}
}