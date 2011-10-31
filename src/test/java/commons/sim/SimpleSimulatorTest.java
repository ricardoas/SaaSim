package commons.sim;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import provisioning.Monitor;
import util.ValidConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.io.Checkpointer;
import commons.io.TickSize;
import commons.io.WorkloadParser;
import commons.sim.components.LoadBalancer;
import commons.sim.components.MachineDescriptor;
import commons.sim.components.TimeSharedMachine;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.schedulingheuristics.RoundRobinHeuristic;
import commons.util.SimulationInfo;

public class SimpleSimulatorTest extends ValidConfigurationTest {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullConfiguration();
	}

	@Test(expected=AssertionError.class)
	public void testAddServerWithInexistentTier() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(3);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator = new SimpleSimulator(scheduler, loadBalancer);
		
		simulator.addServer(4, new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0), false);
		
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test(expected=RuntimeException.class)
	public void testAddServerWithNullMachine() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(3);
		EasyMock.expect(scheduler.now()).andReturn(0L);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator = new SimpleSimulator(scheduler, loadBalancer);
		
		simulator.addServer(0, null, false);
		
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test
	public void testAddServer() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(3);
		EasyMock.expect(scheduler.now()).andReturn(0L).times(2);
		Capture<JEEvent> event = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(event));
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		
		simulator.addServer(0, new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0), false);
		assertEquals(JEEventType.ADD_SERVER, event.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test(expected=AssertionError.class)
	public void testRemoveServerWithInexistentTier() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(3);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator = new SimpleSimulator(scheduler, loadBalancer);
		
		simulator.removeServer(4, new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0), false);
		
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test
	public void testRemoveServerNotUseForce() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(scheduler.now()).andReturn(0L);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		EasyMock.expect(timeSharedMachine.getDescriptor()).andReturn(new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0)).times(2);
		timeSharedMachine.shutdownOnFinish();
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		
		JEEvent event = new JEEvent(JEEventType.ADD_SERVER, handler, 1L, timeSharedMachine);
		loadBalancer.handleEvent(event);
		
		assertTrue(loadBalancer.getServers().size() == 1);
		simulator.removeServer(0, new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0), false);
		assertTrue(loadBalancer.getServers().size() == 0);
		
		EasyMock.verify(monitor, scheduler, handler, timeSharedMachine);
	}
	
	@Test
	public void testRemoveServerUseForce() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(scheduler.now()).andReturn(0L).times(3);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		EasyMock.expect(timeSharedMachine.getDescriptor()).andReturn(new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0)).times(2);
		Queue<Request> queue = new LinkedList<Request>();
		EasyMock.expect(timeSharedMachine.getProcessorQueue()).andReturn(queue);
		timeSharedMachine.shutdownOnFinish();
		EasyMock.expectLastCall().times(1);
		
		Capture<JEEvent> eventTurnedOff = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(eventTurnedOff));
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		
		JEEvent eventAddServer = new JEEvent(JEEventType.ADD_SERVER, handler, 1L, timeSharedMachine);
		loadBalancer.handleEvent(eventAddServer);
		
		assertTrue(loadBalancer.getServers().size() == 1);
		simulator.removeServer(0, new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0), true);
		assertTrue(loadBalancer.getServers().size() == 0);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, eventTurnedOff.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, timeSharedMachine);
	}
	
	
	@Test(expected=AssertionError.class)
	public void testRemoveServerWithoutDescriptorAndInexistentTier() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(3);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		
		simulator.removeServer(4, false);
		
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test
	public void testRemoveServerWithoutDescriptorAndNotUseForce() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		TimeSharedMachine timeSharedMachine2 = EasyMock.createStrictMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(scheduler.now()).andReturn(0L).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		
		EasyMock.expect(timeSharedMachine.getDescriptor()).andReturn(new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0)).times(2);
		EasyMock.expect(timeSharedMachine2.getDescriptor()).andReturn(new MachineDescriptor(1, false, MachineType.C1_MEDIUM, 0)).times(3);
		timeSharedMachine2.shutdownOnFinish();
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler, timeSharedMachine, timeSharedMachine2);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		
		loadBalancer.handleEvent(new JEEvent(JEEventType.ADD_SERVER, handler, 1L, timeSharedMachine));
		loadBalancer.handleEvent(new JEEvent(JEEventType.ADD_SERVER, handler, 1L, timeSharedMachine2));
		
		assertTrue(loadBalancer.getServers().size() == 2);
		simulator.removeServer(0, false);
		assertTrue(loadBalancer.getServers().size() == 1);
		
		EasyMock.verify(monitor, scheduler, handler, timeSharedMachine);
	}
	
	@Test
	public void testRemoveServerWithOneMachineAndNotUseForce() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(scheduler.now()).andReturn(0L);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		EasyMock.expect(timeSharedMachine.getDescriptor()).andReturn(new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0));
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		
		JEEvent event = new JEEvent(JEEventType.ADD_SERVER, handler, 1L, timeSharedMachine);
		loadBalancer.handleEvent(event);
		
		assertTrue(loadBalancer.getServers().size() == 1);
		simulator.removeServer(0, false);
		assertTrue(loadBalancer.getServers().size() == 1);
		
		EasyMock.verify(monitor, scheduler, handler, timeSharedMachine);
	}
	
	@Test
	public void testConstructor(){
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		EasyMock.replay(loadBalancer);
		
		assertNotNull(new SimpleSimulator(Checkpointer.loadScheduler(), loadBalancer));
		
		EasyMock.verify(loadBalancer);
	}
	
	@Test
	public void testRemoveServerWithoutDescriptorAndUseForce() {
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		TimeSharedMachine timeSharedMachine2 = EasyMock.createStrictMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(scheduler.now()).andReturn(0L).times(4);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		
		EasyMock.expect(timeSharedMachine.getDescriptor()).andReturn(new MachineDescriptor(0, true, MachineType.C1_MEDIUM, 0)).times(3);
		EasyMock.expect(timeSharedMachine2.getDescriptor()).andReturn(new MachineDescriptor(1, true, MachineType.C1_MEDIUM, 0)).times(4);
		Queue<Request> queue = new LinkedList<Request>();
		EasyMock.expect(timeSharedMachine2.getProcessorQueue()).andReturn(queue);
		timeSharedMachine2.shutdownOnFinish();
		EasyMock.expectLastCall().times(1);
		
		Capture<JEEvent> eventTurnedOff = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(eventTurnedOff));
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler, timeSharedMachine, timeSharedMachine2);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		
		loadBalancer.handleEvent(new JEEvent(JEEventType.ADD_SERVER, handler, 1L, timeSharedMachine));
		loadBalancer.handleEvent(new JEEvent(JEEventType.ADD_SERVER, handler, 1L, timeSharedMachine2));
		
		assertTrue(loadBalancer.getServers().size() == 2);
		simulator.removeServer(0, true);
		assertTrue(loadBalancer.getServers().size() == 1);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, eventTurnedOff.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, timeSharedMachine);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=AssertionError.class)
	public void testSetWorkloadParserWithNullWorkload() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createStrictMock(WorkloadParser.class);
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		
		EasyMock.replay(monitor, scheduler, handler, workloadParser);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		simulator.setWorkloadParser(null);
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser);
	}
	
	@Test
	public void testSetWorkloadParser() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createStrictMock(WorkloadParser.class);
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0L);
		
		Capture<JEEvent> eventReadWorkload = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(eventReadWorkload));
		EasyMock.expectLastCall().times(1);
		
		EasyMock.expect(workloadParser.hasNext()).andReturn(true);
		EasyMock.expect(workloadParser.next()).andReturn(new LinkedList<Request>());
		EasyMock.expect(workloadParser.hasNext()).andReturn(true);
		
		EasyMock.replay(monitor, scheduler, handler, workloadParser, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		
		simulator.setWorkloadParser(workloadParser);
		JEEvent event = new JEEvent(JEEventType.READWORKLOAD, handler, 1L, timeSharedMachine);
		simulator.handleEvent(event);
		assertEquals(JEEventType.READWORKLOAD, eventReadWorkload.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser, timeSharedMachine);
	}
	
	@Test
	public void testHandleEventChargeUsers() {
		
		while(!Checkpointer.loadSimulationInfo().isChargeDay()){
			Checkpointer.loadSimulationInfo().addDay();
		}
		
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		monitor.chargeUsers(31 * TickSize.DAY.getTickInMillis() - 1);
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		simulator.setMonitor(monitor);
		
		JEEvent event = new JEEvent(JEEventType.CHARGE_USERS, handler, 31 * TickSize.DAY.getTickInMillis() - 1);
		simulator.handleEvent(event);
		
		EasyMock.verify(monitor, scheduler, handler);
	}

	@Test
	public void testHandleEventReadWorkload() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createStrictMock(WorkloadParser.class);
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		Request request = EasyMock.createStrictMock(Request.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		Capture<JEEvent> eventReadWorkload = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(eventReadWorkload));
		EasyMock.expect(scheduler.now()).andReturn(0L);
		scheduler.queueEvent(EasyMock.capture(eventReadWorkload));
		
		EasyMock.expect(workloadParser.hasNext()).andReturn(true);
		List<Request> requests = new LinkedList<Request>();
		requests.add(request);
		EasyMock.expect(workloadParser.next()).andReturn(requests);
		EasyMock.expect(workloadParser.hasNext()).andReturn(true);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(0l);
		
		EasyMock.replay(monitor, scheduler, handler, workloadParser, timeSharedMachine, request);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		
		simulator.setWorkloadParser(workloadParser);
		JEEvent event = new JEEvent(JEEventType.READWORKLOAD, handler, 1L, timeSharedMachine);
		simulator.handleEvent(event);
		assertEquals(JEEventType.READWORKLOAD, eventReadWorkload.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser, timeSharedMachine, request);
	}
	
	@Test
	public void testHandleEventReadWorkloadWithoutMoreRequests() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createStrictMock(WorkloadParser.class);
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		Request request = EasyMock.createStrictMock(Request.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		Capture<JEEvent> eventReadWorkload = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(eventReadWorkload));
		EasyMock.expectLastCall();
		
		EasyMock.expect(workloadParser.hasNext()).andReturn(true);
		List<Request> requests = new LinkedList<Request>();
		requests.add(request);
		EasyMock.expect(workloadParser.next()).andReturn(requests);
		EasyMock.expect(workloadParser.hasNext()).andReturn(false);
		workloadParser.close();
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(0l);
		
		EasyMock.replay(monitor, scheduler, handler, workloadParser, timeSharedMachine, request);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		
		simulator.setWorkloadParser(workloadParser);
		JEEvent event = new JEEvent(JEEventType.READWORKLOAD, handler, 1L, timeSharedMachine);
		simulator.handleEvent(event);
		assertEquals(JEEventType.NEWREQUEST, eventReadWorkload.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser, timeSharedMachine, request);
	}
	
	@Test
	public void testHandleEventCollectStatistics() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createStrictMock(WorkloadParser.class);
		JEEventHandler handler = EasyMock.createStrictMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0L).times(1);
		
		Capture<JEEvent> eventCollectStatistics = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(eventCollectStatistics));
		EasyMock.expectLastCall().times(1);
		
		monitor.sendStatistics(1, new MachineStatistics(0, 0, 0, 0), 3);
		EasyMock.expectLastCall().times(1);
		EasyMock.expect(workloadParser.hasNext()).andReturn(true).times(1);
		
		EasyMock.replay(monitor, scheduler, handler, workloadParser, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator = new SimpleSimulator(scheduler, loadBalancer);
		simulator.setMonitor(monitor);
		simulator.setWorkloadParser(workloadParser);
		
		JEEvent event = new JEEvent(JEEventType.COLLECT_STATISTICS, handler, 1L, timeSharedMachine);
		simulator.handleEvent(event);
		assertEquals(JEEventType.COLLECT_STATISTICS, eventCollectStatistics.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser, timeSharedMachine);
	}
	
	@Test
	public void testStartWithoutBeingLastSimulationDay() throws Exception{
		
		Capture<JEEvent> firstEvent = new Capture<JEEvent>();
		Capture<JEEvent> secondEvent = new Capture<JEEvent>();
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleSimulator.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.capture(firstEvent));
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.capture(secondEvent));
		scheduler.start();
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.setMonitor(monitor);
		
		EasyMock.replay(monitor, loadBalancer, scheduler);
		
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		simulator.setMonitor(monitor);
		simulator.start();
		
		assertEquals(JEEventType.READWORKLOAD, firstEvent.getValue().getType());
		assertEquals(JEEventType.COLLECT_STATISTICS, secondEvent.getValue().getType());
		
		EasyMock.verify(monitor, loadBalancer, scheduler);
	}
	
	@Test
	public void testStartInFirstChargeUsersDay() throws Exception{
		
		Capture<JEEvent> firstEvent = new Capture<JEEvent>();
		Capture<JEEvent> secondEvent = new Capture<JEEvent>();
		Capture<JEEvent> thirdEvent = new Capture<JEEvent>();

		SimulationInfo info = Checkpointer.loadSimulationInfo();
		while(!info.isChargeDay()){
			info.addDay();
		}
		
		assert info.isChargeDay();
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleSimulator.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.capture(firstEvent));
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.capture(secondEvent));
		scheduler.queueEvent(EasyMock.capture(thirdEvent));
		scheduler.start();
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.setMonitor(monitor);
		
		EasyMock.replay(monitor, loadBalancer, scheduler);
		
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		simulator.setMonitor(monitor);
		simulator.start();
		
		assertEquals(JEEventType.READWORKLOAD, firstEvent.getValue().getType());
		assertEquals(JEEventType.COLLECT_STATISTICS, secondEvent.getValue().getType());
		assertEquals(JEEventType.CHARGE_USERS, thirdEvent.getValue().getType());

		EasyMock.verify(monitor, loadBalancer, scheduler);
	}
	
	@Test
	public void testStartInOtherChargeUsersDay() throws Exception{
		
		Capture<JEEvent> firstEvent = new Capture<JEEvent>();
		Capture<JEEvent> secondEvent = new Capture<JEEvent>();
		Capture<JEEvent> thirdEvent = new Capture<JEEvent>();

		SimulationInfo info = Checkpointer.loadSimulationInfo();
		while(!info.isChargeDay()){
			info.addDay();
		}
		info.addDay();
		while(!info.isChargeDay()){
			info.addDay();
		}
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleSimulator.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.capture(firstEvent));
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.capture(secondEvent));
		scheduler.queueEvent(EasyMock.capture(thirdEvent));
		scheduler.start();
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.setMonitor(monitor);
		
		EasyMock.replay(monitor, loadBalancer, scheduler);
		
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		simulator.setMonitor(monitor);
		simulator.start();
		
		assertEquals(JEEventType.READWORKLOAD, firstEvent.getValue().getType());
		assertEquals(JEEventType.COLLECT_STATISTICS, secondEvent.getValue().getType());
		assertEquals(JEEventType.CHARGE_USERS, thirdEvent.getValue().getType());

		EasyMock.verify(monitor, loadBalancer, scheduler);
	}
	
	@Test
	public void testStartBeingLastSimulationDay() throws Exception{
		
		Capture<JEEvent> firstEvent = new Capture<JEEvent>();
		Capture<JEEvent> secondEvent = new Capture<JEEvent>();

		SimulationInfo info = Checkpointer.loadSimulationInfo();
		while(!info.isFinished()){
			info.addDay();
		}
		
		assert info.isFinished();
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleSimulator.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.capture(firstEvent));
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.capture(secondEvent));
		scheduler.start();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		EasyMock.replay(monitor, loadBalancer, scheduler);
		
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		simulator.start();
		
		assertEquals(JEEventType.READWORKLOAD, firstEvent.getValue().getType());
		assertEquals(JEEventType.COLLECT_STATISTICS, secondEvent.getValue().getType());
		
		EasyMock.verify(monitor, loadBalancer, scheduler);
	}

	@Test
	public void testStartBeingChargeDay() throws Exception{
		
		Capture<JEEvent> firstEvent = new Capture<JEEvent>();
		Capture<JEEvent> secondEvent = new Capture<JEEvent>();
		Capture<JEEvent> thirdEvent = new Capture<JEEvent>();

		SimulationInfo info = Checkpointer.loadSimulationInfo();
		while(!info.isChargeDay()){
			info.addDay();
		}
		
		assert info.isChargeDay();
		
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleSimulator.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.capture(firstEvent));
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.capture(secondEvent));
		scheduler.queueEvent(EasyMock.capture(thirdEvent));
		scheduler.start();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		EasyMock.replay(monitor, loadBalancer, scheduler);
		
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, loadBalancer);
		simulator.start();
		
		assertEquals(JEEventType.READWORKLOAD, firstEvent.getValue().getType());
		assertEquals(JEEventType.COLLECT_STATISTICS, secondEvent.getValue().getType());
		assertEquals(JEEventType.CHARGE_USERS, thirdEvent.getValue().getType());
		
		assertEquals(31 * TickSize.DAY.getTickInMillis() - 1, thirdEvent.getValue().getScheduledTime());
		
		EasyMock.verify(monitor, loadBalancer, scheduler);
	}
}
