package commons.sim.components;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import provisioning.DPS;
import provisioning.Monitor;
import util.ValidConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.io.Checkpointer;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.schedulingheuristics.RoundRobinHeuristic;
import commons.sim.schedulingheuristics.SchedulingHeuristic;

public class LoadBalancerTest extends ValidConfigurationTest {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullConfiguration();
	}
	
	@Test
	public void testAddServerWithSetupDelay() throws ConfigurationException{
		
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(LoadBalancer.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeSharedMachine.class))).andReturn(2);
		EasyMock.expect(scheduler.now()).andReturn(0l).times(3);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(captured));
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(scheduler, schedulingHeuristic);
		
		LoadBalancer lb = new LoadBalancer(scheduler, schedulingHeuristic, Integer.MAX_VALUE, 0);

		lb.addServer(descriptor, true);
		assertEquals(0, lb.getServers().size());
		
		JEEvent event = captured.getValue();
		assertEquals(JEEventType.ADD_SERVER, event.getType());
		assertEquals(300000l, event.getScheduledTime());
		
		EasyMock.verify(scheduler, schedulingHeuristic);
	}
	
	@Test
	public void testAddServerWithoutSetupDelay(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(LoadBalancer.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeSharedMachine.class))).andReturn(2);
		EasyMock.expect(scheduler.now()).andReturn(0l).times(3);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(captured));
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(scheduler, schedulingHeuristic);
		
		LoadBalancer lb = new LoadBalancer(scheduler, schedulingHeuristic, Integer.MAX_VALUE, 0);

		lb.addServer(descriptor, false);
		assertEquals(0, lb.getServers().size());
		
		JEEvent event = captured.getValue();
		assertEquals(JEEventType.ADD_SERVER, event.getType());
		assertEquals(0, event.getScheduledTime());
		
		EasyMock.verify(scheduler, schedulingHeuristic);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveServer(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		schedulingHeuristic.updateServers(EasyMock.isA(List.class));
		schedulingHeuristic.finishServer(EasyMock.isA(Machine.class), EasyMock.anyInt(), EasyMock.isA(List.class));
		
		EasyMock.replay(schedulingHeuristic);

		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), schedulingHeuristic, Integer.MAX_VALUE, 1);
		
		lb.addServer(descriptor, false);
		Checkpointer.loadScheduler().start();
		
		//Removing a server
		lb.removeServer(descriptor, false);
		
		EasyMock.verify(schedulingHeuristic);
	}
	
	@Test
	public void testRemoveServerThatDoesNotExist(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		EasyMock.replay(schedulingHeuristic);

		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), schedulingHeuristic, Integer.MAX_VALUE, 1);
		
		//Removing a server
		lb.removeServer(descriptor, false);
		
		EasyMock.verify(schedulingHeuristic);
	}
	
	/**
	 * Scheduling a new request with one machine artificially chosen by the heuristic
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleEventNewRequestWithOneMachine(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		Request request = EasyMock.createStrictMock(Request.class);
		JEEvent newRequestEvent = EasyMock.createStrictMock(JEEvent.class);
		Machine machine = EasyMock.createStrictMock(TimeSharedMachine.class);
		EasyMock.expect(machine.getDescriptor()).andReturn(descriptor);
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		EasyMock.expect(newRequestEvent.getType()).andReturn(JEEventType.NEWREQUEST).once();
		EasyMock.expect(newRequestEvent.getValue()).andReturn(new Request [] {request}).once();
		
		schedulingHeuristic.updateServers(EasyMock.isA(List.class));
		EasyMock.expect(schedulingHeuristic.getNextServer(
				EasyMock.isA(Request.class) , EasyMock.isA(List.class))).andReturn(machine);
		machine.sendRequest(request);
		
		EasyMock.replay(newRequestEvent, schedulingHeuristic, request, machine);
		
		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), schedulingHeuristic, Integer.MAX_VALUE, 1);
		lb.addServer(descriptor, false);
		JEEvent machineIsUpEvent = new JEEvent(JEEventType.ADD_SERVER, lb, 0l, machine);
		lb.handleEvent(machineIsUpEvent);
		lb.handleEvent(newRequestEvent);
		
		assertEquals(1, lb.getServers().size());
		
		EasyMock.verify(newRequestEvent, schedulingHeuristic, request, machine);
		
	}
	
	/**
	 * Scheduling a new request while the heuristic does not chooses any machines.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleEventNewRequestWithNoAvailableMachine(){
		Request request = EasyMock.createStrictMock(Request.class);
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.requestQueued(0, request, 1);
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		EasyMock.expect(event.getType()).andReturn(JEEventType.NEWREQUEST).once();
		EasyMock.expect(event.getValue()).andReturn(new Request [] {request}).once();
		
		EasyMock.expect(schedulingHeuristic.getNextServer(
				EasyMock.isA(Request.class) , EasyMock.isA(List.class))).andReturn(null);
		
		EasyMock.replay(event, schedulingHeuristic, request, dps);
		
		//Load balancer being constructed without machines!
		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), schedulingHeuristic, Integer.MAX_VALUE, 1);
		lb.setMonitor(dps);
		
		lb.handleEvent(event);
		
		EasyMock.verify(event, schedulingHeuristic, request, dps);
	}
	
	
	@Test
	public void testHandleEventEvaluateUtilisation() throws ConfigurationException{
		buildFullConfiguration();
		
		long evaluationTime = 1000;
		double utilisation1 = 0.9;
		double utilisation2 = 0.5;
		long totalArrivals = 100l;
		long totalCompletions = 100l;
		
		//Mocking machines actions
		MachineDescriptor descriptor = new MachineDescriptor(0, false, MachineType.M1_SMALL, 0);
		MachineDescriptor descriptor2 = new MachineDescriptor(1, true, MachineType.C1_MEDIUM, 0);
		
		TimeSharedMachine machine1 = EasyMock.createStrictMock(TimeSharedMachine.class);
		TimeSharedMachine machine2 = EasyMock.createStrictMock(TimeSharedMachine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.computeUtilisation(evaluationTime)).andReturn(utilisation1);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.computeUtilisation(evaluationTime)).andReturn(utilisation2);
		
		//Mocking scheduling heuristic actions
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		schedulingHeuristic.updateServers(EasyMock.isA(List.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.expect(schedulingHeuristic.getRequestsArrivalCounter()).andReturn(totalArrivals);
		EasyMock.expect(schedulingHeuristic.getFinishedRequestsCounter()).andReturn(totalCompletions);
		schedulingHeuristic.resetCounters();
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.sendStatistics(1000, new MachineStatistics((utilisation1+utilisation2)/2, totalArrivals, totalCompletions, 2), 0);
		EasyMock.replay(machine1, machine2, schedulingHeuristic, monitor);
		
		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor);
		
		lb.addServer(descriptor, true);
		lb.addServer(descriptor2, true);
		
		JEEvent machineIsUpEvent = new JEEvent(JEEventType.ADD_SERVER, lb, 0l, machine1);
		JEEvent machineIsUpEvent2 = new JEEvent(JEEventType.ADD_SERVER, lb, 0l, machine2);
		
		lb.handleEvent(machineIsUpEvent);
		lb.handleEvent(machineIsUpEvent2);
		
		//Calculating utilisation
		lb.collectStatistics(evaluationTime);
		
		EasyMock.verify(machine1, machine2, schedulingHeuristic, monitor);
	}
	
	@Test
	public void testHandleEventEvaluateUtilisationWithoutMachines(){
		long evaluationTime = 1000;
		long totalArrivals = 100l;
		long totalCompletions = 0;
		
		//Mocking scheduling heuristic actions
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.expect(schedulingHeuristic.getRequestsArrivalCounter()).andReturn(totalArrivals);
		EasyMock.expect(schedulingHeuristic.getFinishedRequestsCounter()).andReturn(totalCompletions);
		schedulingHeuristic.resetCounters();
		EasyMock.replay(schedulingHeuristic);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.sendStatistics(0, new MachineStatistics(0, totalArrivals, totalCompletions, 0), 0);
		EasyMock.replay(monitor);
		
		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), schedulingHeuristic, Integer.MAX_VALUE, 0);
		
		//Calculating utilisation
		lb.handleEvent(new JEEvent(JEEventType.COLLECT_STATISTICS, lb, 0l, evaluationTime));
	}
	
	@Test
	public void testHandleEventMachineTurnedOff(){
		MachineDescriptor machineDescriptor = new MachineDescriptor(1, false, MachineType.C1_XLARGE, 0);

		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.machineTurnedOff(machineDescriptor);
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(monitor, schedulingHeuristic);
		
		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor);
		
		lb.handleEvent(new JEEvent(JEEventType.MACHINE_TURNED_OFF, lb, 0l, machineDescriptor));
		
		EasyMock.verify(monitor, schedulingHeuristic);
	}
	
	@Test
	public void testHandleEventRequestQueued(){
		Request request = EasyMock.createStrictMock(Request.class);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.requestQueued(0, request, 0);
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(monitor, schedulingHeuristic, request);
		
		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor);
		
		lb.handleEvent(new JEEvent(JEEventType.REQUESTQUEUED, lb, 0l, request));
		
		EasyMock.verify(monitor, schedulingHeuristic, request);
	}
	
	@Test
	public void testEstimateServersWithoutServers(){
		
		Capture<MachineStatistics> captured = new Capture<MachineStatistics>();
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.sendStatistics(EasyMock.anyLong(), EasyMock.capture(captured), EasyMock.anyInt());
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(monitor, schedulingHeuristic);
		
		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor);
		lb.estimateServers(0);
		
		assertEquals(0, captured.getValue().averageUtilisation, 0.00001);
		assertEquals(0, captured.getValue().numberOfRequestsArrivalInLastInterval, 0.00001);
		assertEquals(0, captured.getValue().numberOfRequestsCompletionsInLastInterval, 0.00001);
		assertEquals(0, captured.getValue().totalNumberOfServers, 0.00001);
		
		EasyMock.verify(monitor, schedulingHeuristic);
	}
	
	@Test
	public void testEstimateServersWithServers() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		
		Capture<MachineStatistics> captured = new Capture<MachineStatistics>();
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.sendStatistics(EasyMock.anyLong(), EasyMock.capture(captured), EasyMock.anyInt());
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		Machine machine1 = EasyMock.createMock(Machine.class);
		Machine machine2 = EasyMock.createMock(Machine.class);
		Machine machine3 = EasyMock.createMock(Machine.class);
		
		EasyMock.replay(monitor, schedulingHeuristic, machine1, machine2, machine3);
		
		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor);
		
		Field field = LoadBalancer.class.getDeclaredField("servers");
		field.setAccessible(true);
		List<Machine> servers = (List<Machine>) field.get(lb);
		servers.add(machine1);
		servers.add(machine2);
		servers.add(machine3);
		
		lb.estimateServers(100);
		
		assertEquals(0, captured.getValue().averageUtilisation, 0.00001);
		assertEquals(0, captured.getValue().numberOfRequestsArrivalInLastInterval, 0.00001);
		assertEquals(0, captured.getValue().numberOfRequestsCompletionsInLastInterval, 0.00001);
		assertEquals(3, captured.getValue().totalNumberOfServers, 0.00001);
		
		EasyMock.verify(monitor, schedulingHeuristic);
	}
	
	@Test
	public void testEqualsHashCodeConsistencyWithSameTierAndSameHandlerID() {
		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), new RoundRobinHeuristic(), Integer.MAX_VALUE, 0);

		assertTrue(lb.equals(lb));
		assertEquals(lb.hashCode(), lb.hashCode());
	}
	
	@Test
	public void testEqualsHashCodeConsistencyWithSameTierButDifferentHandlerID() {
		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), new RoundRobinHeuristic(), Integer.MAX_VALUE, 0);
		LoadBalancer lbClone = new LoadBalancer(Checkpointer.loadScheduler(), new RoundRobinHeuristic(), Integer.MAX_VALUE, 0);

		assertFalse(lb.equals(lbClone));
		assertFalse(lbClone.equals(lb));
		assertNotSame(lb.hashCode(), lbClone.hashCode());
	}
	
	@Test
	public void testEqualsHashCodeConsistencyWithDifferentTier() {
		LoadBalancer lb1 = new LoadBalancer(Checkpointer.loadScheduler(), new RoundRobinHeuristic(), Integer.MAX_VALUE, 0);
		LoadBalancer lb2 = new LoadBalancer(Checkpointer.loadScheduler(), new RoundRobinHeuristic(), Integer.MAX_VALUE, 1);
		
		assertTrue(lb1.equals(lb1));
		assertFalse(lb1.equals(lb2));
		assertFalse(lb2.equals(lb1));
		assertNotSame(lb1.hashCode(), lb2.hashCode());
	}
	
	@Test(expected=AssertionError.class)
	public void testEqualsWithNullObject() {
		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), new RoundRobinHeuristic(), Integer.MAX_VALUE, 0);
		LoadBalancer lbNull = null;
		
		lb.equals(lbNull);
	}
	
	@Test(expected=AssertionError.class)
	public void testEqualsWithAnotherClassObject() {
		LoadBalancer lb = new LoadBalancer(Checkpointer.loadScheduler(), new RoundRobinHeuristic(), Integer.MAX_VALUE, 0);		
		
		assertTrue(lb.equals(lb));
		lb.equals(new String(""));
	}
}
