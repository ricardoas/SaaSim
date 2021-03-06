package saasim.sim.components;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import saasim.cloud.MachineType;
import saasim.cloud.Request;
import saasim.config.Configuration;
import saasim.provisioning.DPS;
import saasim.provisioning.Monitor;
import saasim.sim.core.Event;
import saasim.sim.core.EventScheduler;
import saasim.sim.core.EventType;
import saasim.sim.schedulingheuristics.Statistics;
import saasim.sim.schedulingheuristics.RoundRobinHeuristic;
import saasim.sim.schedulingheuristics.SchedulingHeuristic;
import saasim.util.ValidConfigurationTest;


public class LoadBalancerTest extends ValidConfigurationTest {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullConfiguration();
	}
	
	@Test
	public void testAddServerWithSetupDelay(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleLoadBalancerWithAdmissionControl.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeSharedMachine.class))).andReturn(2);
		EasyMock.expect(scheduler.now()).andReturn(0l).times(3);
		Capture<Event> captured = new Capture<Event>();
		scheduler.queueEvent(EasyMock.capture(captured));
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.expect(schedulingHeuristic.getMachines()).andReturn(new ArrayList<Machine>());
		EasyMock.replay(scheduler, schedulingHeuristic);
		
		SimpleLoadBalancerWithAdmissionControl lb = new SimpleLoadBalancerWithAdmissionControl(scheduler, null, schedulingHeuristic, Integer.MAX_VALUE, 0);

		lb.addMachine(descriptor, true);
		assertEquals(0, lb.getServers().size());
		
		Event event = captured.getValue();
		assertEquals(EventType.ADD_SERVER, event.getType());
		assertEquals(300000l, event.getScheduledTime());
		
		EasyMock.verify(scheduler, schedulingHeuristic);
	}
	
	@Test
	public void testAddServerWithoutSetupDelay(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleLoadBalancerWithAdmissionControl.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeSharedMachine.class))).andReturn(2);
		EasyMock.expect(scheduler.now()).andReturn(0l).times(3);
		Capture<Event> captured = new Capture<Event>();
		scheduler.queueEvent(EasyMock.capture(captured));
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.expect(schedulingHeuristic.getMachines()).andReturn(new ArrayList<Machine>());
		EasyMock.replay(scheduler, schedulingHeuristic);
		
		SimpleLoadBalancerWithAdmissionControl lb = new SimpleLoadBalancerWithAdmissionControl(scheduler, null, schedulingHeuristic, Integer.MAX_VALUE, 0);

		lb.addMachine(descriptor, false);
		assertEquals(0, lb.getServers().size());
		
		Event event = captured.getValue();
		assertEquals(EventType.ADD_SERVER, event.getType());
		assertEquals(0, event.getScheduledTime());
		
		EasyMock.verify(scheduler, schedulingHeuristic);
	}
	
	/**
	 * Scheduling a new request with one machine artificially chosen by the heuristic
	 */
	@Test
	public void testHandleEventNewRequestWithOneMachine(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		Request request = EasyMock.createStrictMock(Request.class);
		Event newRequestEvent = EasyMock.createStrictMock(Event.class);
		Machine machine = EasyMock.createStrictMock(TimeSharedMachine.class);
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		EasyMock.expect(newRequestEvent.getType()).andReturn(EventType.NEWREQUEST).once();
		EasyMock.expect(newRequestEvent.getValue()).andReturn(new Request [] {request}).once();
		
		schedulingHeuristic.addMachine(EasyMock.isA(Machine.class));
		EasyMock.expect(schedulingHeuristic.next(EasyMock.isA(Request.class))).andReturn(machine);
		
		List<Machine> machines = new ArrayList<Machine>();
		machines.add(machine);
		EasyMock.expect(schedulingHeuristic.getMachines()).andReturn(machines);
		machine.sendRequest(request);
		
		EasyMock.replay(newRequestEvent, schedulingHeuristic, request, machine);
		
		SimpleLoadBalancerWithAdmissionControl lb = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, schedulingHeuristic, Integer.MAX_VALUE, 1);
		lb.addMachine(descriptor, false);
		Event machineIsUpEvent = new Event(EventType.ADD_SERVER, lb, 0l, descriptor);
//		lb.handleEvent(machineIsUpEvent);
//		lb.handleEvent(newRequestEvent);
		lb.serverIsUp(descriptor);
		lb.handleNewRequest(request);
		
		assertEquals(1, lb.getServers().size());
		
		EasyMock.verify(newRequestEvent, schedulingHeuristic, request, machine);
	}
	
	/**
	 * Scheduling a new request while the heuristic does not chooses any machines.
	 */
	@Test
	public void testHandleEventNewRequestWithNoAvailableMachine(){
		Request request = EasyMock.createStrictMock(Request.class);
		Event event = EasyMock.createStrictMock(Event.class);
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.requestQueued(0, request, 1);
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		EasyMock.expect(event.getType()).andReturn(EventType.NEWREQUEST).once();
		EasyMock.expect(event.getValue()).andReturn(new Request [] {request}).once();
		
		EasyMock.expect(schedulingHeuristic.next(EasyMock.isA(Request.class))).andReturn(null);
		
		EasyMock.replay(event, schedulingHeuristic, request, dps);
		
		//Load balancer being constructed without machines!
		SimpleLoadBalancerWithAdmissionControl lb = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, schedulingHeuristic, Integer.MAX_VALUE, 1);
		lb.setMonitor(dps, null);
//		lb.handleEvent(event);
		lb.handleNewRequest(request);
		
		EasyMock.verify(event, schedulingHeuristic, request, dps);
	}
	
	
	@Test
	public void testHandleEventEvaluateUtilisation() throws ConfigurationException{
		long evaluationTime = 1000;
		double utilisation1 = 0.9;
		double utilisation2 = 0.5;
		long totalArrivals = 100l;
		long totalCompletions = 100l;
		Statistics machineStatistics = new Statistics((utilisation1+utilisation2)/2, totalArrivals, totalCompletions, 2);
		
		//Mocking machines actions
		MachineDescriptor descriptor = new MachineDescriptor(0, false, MachineType.M1_SMALL, 0);
		MachineDescriptor descriptor2 = new MachineDescriptor(1, true, MachineType.C1_MEDIUM, 0);
		
		//Mocking scheduling heuristic actions
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		schedulingHeuristic.addMachine(EasyMock.isA(Machine.class));
		EasyMock.expectLastCall().times(2);
		EasyMock.expect(schedulingHeuristic.getStatistics(1000)).andReturn(machineStatistics);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.sendStatistics(1000, machineStatistics, 0);
		EasyMock.replay(schedulingHeuristic, monitor);
		
		SimpleLoadBalancerWithAdmissionControl lb = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor, null);
		
		lb.addMachine(descriptor, true);
		lb.addMachine(descriptor2, true);
		
		Event machineIsUpEvent = new Event(EventType.ADD_SERVER, lb, 0l, descriptor);
		Event machineIsUpEvent2 = new Event(EventType.ADD_SERVER, lb, 0l, descriptor2);
		
//		lb.handleEvent(machineIsUpEvent);
//		lb.handleEvent(machineIsUpEvent2);
		lb.serverIsUp(descriptor);
		lb.serverIsUp(descriptor2);
		
		//Calculating utilisation
		lb.collectStatistics(evaluationTime, 300000, 0, 0);
		EasyMock.verify(schedulingHeuristic, monitor);
	}
	
	@Ignore @Test
	public void testHandleEventEvaluateUtilisationWithoutMachines(){
		long evaluationTime = 1000;
		long totalArrivals = 100l;
		long totalCompletions = 0;
		
		//Mocking scheduling heuristic actions
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(schedulingHeuristic);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.sendStatistics(0, new Statistics(0, totalArrivals, totalCompletions, 0), 0);
		EasyMock.replay(monitor);
		
		LoadBalancer lb = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, schedulingHeuristic, Integer.MAX_VALUE, 0);
		
		//Calculating utilisation
//		lb.handleEvent(new JEEvent(JEEventType.COLLECT_STATISTICS, lb, 0l, evaluationTime));
//		lb.collectStatistics(now, timeInterval, numberOfRequests);
	}
	
	@Test
	public void testHandleEventRequestQueued(){
		Request request = EasyMock.createStrictMock(Request.class);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.requestQueued(0, request, 0);
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(monitor, schedulingHeuristic, request);
		
		SimpleLoadBalancerWithAdmissionControl lb = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor, null);
		
//		lb.handleEvent(new JEEvent(JEEventType.REQUESTQUEUED, lb, 0l, request));
		lb.requestWasQueued(request);
		
		EasyMock.verify(monitor, schedulingHeuristic, request);
	}
	
	@Test
	public void testEstimateServersWithoutServers(){
		Capture<Statistics> captured = new Capture<Statistics>();
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.sendStatistics(EasyMock.anyLong(), EasyMock.capture(captured), EasyMock.anyInt());
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.expect(schedulingHeuristic.getNumberOfMachines()).andReturn(0);
		
		EasyMock.replay(monitor, schedulingHeuristic);
		
		SimpleLoadBalancerWithAdmissionControl lb = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor, null);
		lb.estimateServers(0);
		
		assertEquals(0, captured.getValue().averageUtilisation, 0.00001);
		assertEquals(0, captured.getValue().requestArrivals, 0.00001);
		assertEquals(0, captured.getValue().requestCompletions, 0.00001);
		assertEquals(0, captured.getValue().totalNumberOfActiveServers, 0.00001);
		
		EasyMock.verify(monitor, schedulingHeuristic);
	}
	
	@Test
	public void testEstimateServersWithServers() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		Capture<Statistics> captured = new Capture<Statistics>();
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.sendStatistics(EasyMock.anyLong(), EasyMock.capture(captured), EasyMock.anyInt());
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.expect(schedulingHeuristic.getNumberOfMachines()).andReturn(3);
		
		MachineDescriptor descriptor1 = new MachineDescriptor(1, false, MachineType.M1_SMALL, 1);
		MachineDescriptor descriptor2 = new MachineDescriptor(2, false, MachineType.M1_SMALL, 1);
		MachineDescriptor descriptor3 = new MachineDescriptor(3, false, MachineType.M1_SMALL, 1);
		
		EasyMock.replay(monitor, schedulingHeuristic);
		
		SimpleLoadBalancerWithAdmissionControl lb = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor, null);
		lb.addMachine(descriptor1, false);
		lb.addMachine(descriptor2, false);
		lb.addMachine(descriptor3, false);
		
		lb.estimateServers(100);
		
		assertEquals(0, captured.getValue().averageUtilisation, 0.00001);
		assertEquals(0, captured.getValue().requestArrivals, 0.00001);
		assertEquals(0, captured.getValue().requestCompletions, 0.00001);
		assertEquals(3, captured.getValue().totalNumberOfActiveServers, 0.00001);
		
		EasyMock.verify(monitor, schedulingHeuristic);
	}
	
	@Test
	public void testEqualsHashCodeConsistencyWithSameTierAndSameHandlerID() {
		LoadBalancer lb = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, new RoundRobinHeuristic(), Integer.MAX_VALUE, 0);

		assertTrue(lb.equals(lb));
		assertEquals(lb.hashCode(), lb.hashCode());
	}
	
	@Test
	public void testEqualsHashCodeConsistencyWithSameTierButDifferentHandlerID() {
		LoadBalancer lb = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, new RoundRobinHeuristic(), Integer.MAX_VALUE, 0);
		LoadBalancer lbClone = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, new RoundRobinHeuristic(), Integer.MAX_VALUE, 0);

		assertFalse(lb.equals(lbClone));
		assertFalse(lbClone.equals(lb));
		assertNotSame(lb.hashCode(), lbClone.hashCode());
	}
	
	@Test
	public void testEqualsHashCodeConsistencyWithDifferentTier() {
		LoadBalancer lb1 = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, new RoundRobinHeuristic(), Integer.MAX_VALUE, 0);
		LoadBalancer lb2 = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, new RoundRobinHeuristic(), Integer.MAX_VALUE, 1);
		
		assertTrue(lb1.equals(lb1));
		assertFalse(lb1.equals(lb2));
		assertFalse(lb2.equals(lb1));
		assertNotSame(lb1.hashCode(), lb2.hashCode());
	}
	
	@Test(expected=AssertionError.class)
	public void testEqualsWithNullObject() {
		LoadBalancer lb = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, new RoundRobinHeuristic(), Integer.MAX_VALUE, 0);
		LoadBalancer lbNull = null;
		
		lb.equals(lbNull);
	}
	
	@Test(expected=AssertionError.class)
	public void testEqualsWithAnotherClassObject() {
		LoadBalancer lb = new SimpleLoadBalancerWithAdmissionControl(Configuration.getInstance().getScheduler(), null, new RoundRobinHeuristic(), Integer.MAX_VALUE, 0);		
		
		assertTrue(lb.equals(lb));
		lb.equals(new String(""));
	}
}