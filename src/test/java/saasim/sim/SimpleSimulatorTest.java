package saasim.sim;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import saasim.cloud.MachineType;
import saasim.cloud.Request;
import saasim.config.Configuration;
import saasim.io.WorkloadParser;
import saasim.provisioning.Monitor;
import saasim.sim.components.LoadBalancer;
import saasim.sim.components.Machine;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.components.TimeSharedMachine;
import saasim.sim.core.Event;
import saasim.sim.core.EventHandler;
import saasim.sim.core.EventScheduler;
import saasim.sim.core.EventType;
import saasim.sim.provisioningheuristics.MachineStatistics;
import saasim.sim.schedulingheuristics.RoundRobinHeuristic;
import saasim.sim.schedulingheuristics.SchedulingHeuristic;
import saasim.util.SimulationInfo;
import saasim.util.TimeUnit;
import saasim.util.ValidConfigurationTest;


public class SimpleSimulatorTest extends ValidConfigurationTest {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullConfiguration();
	}
	
	@Test
	public void testConstructor(){
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.replay(loadBalancer);
		
		assertNotNull(new SimpleMultiTierApplication(Configuration.getInstance().getScheduler(), loadBalancer));
		EasyMock.verify(loadBalancer);
	}
	
	@Test(expected=AssertionError.class)
	public void testAddServerWithInexistentTier() {
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1).times(3);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleMultiTierApplication simulator = new SimpleMultiTierApplication(scheduler, loadBalancer);
		
		simulator.addMachine(4, new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0), false);
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test(expected=RuntimeException.class)
	public void testAddServerWithNullMachine() {
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1).times(3);
		EasyMock.expect(scheduler.now()).andReturn(0L).times(2);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleMultiTierApplication simulator = new SimpleMultiTierApplication(scheduler, loadBalancer);
		
		simulator.addMachine(0, null, false);
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test
	public void testAddServer() {
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1).times(3);
		EasyMock.expect(scheduler.now()).andReturn(0L).times(3);
		Capture<Event> event = new Capture<Event>();
		scheduler.queueEvent(EasyMock.capture(event));
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
		
		simulator.addMachine(0, new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0), false);
		assertEquals(EventType.ADD_SERVER, event.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test(expected=AssertionError.class)
	public void testRemoveServerWithInexistentTier() {
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1).times(3);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleMultiTierApplication simulator = new SimpleMultiTierApplication(scheduler, loadBalancer);
		
		simulator.removeMachine(4, false);
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test
	public void testRemoveServerNotUseForce() {
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		SchedulingHeuristic heuristic = EasyMock.createMock(SchedulingHeuristic.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);

		heuristic.addMachine(EasyMock.isA(Machine.class));
		EasyMock.expect(heuristic.removeMachine()).andReturn(timeSharedMachine);
		
		MachineDescriptor descriptor = new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0);
		EasyMock.expect(timeSharedMachine.getDescriptor()).andReturn(descriptor);
		timeSharedMachine.shutdownOnFinish();
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(heuristic, monitor, scheduler, handler, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, heuristic, 2, 3);
		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
		
//		JEEvent event = new JEEvent(JEEventType.ADD_SERVER, handler, 1L, descriptor);
		loadBalancer.serverIsUp(descriptor);
		
		simulator.removeMachine(0, false);
		EasyMock.verify(monitor, scheduler, handler, timeSharedMachine);
	}
	
	@Ignore @Test
	public void testRemoveServerUseForce() {
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1).times(2);
		EasyMock.expect(scheduler.now()).andReturn(0L).times(3);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		EasyMock.expect(timeSharedMachine.getDescriptor()).andReturn(new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0)).times(2);
		Queue<Request> queue = new LinkedList<Request>();
		EasyMock.expect(timeSharedMachine.getProcessorQueue()).andReturn(queue);
		timeSharedMachine.shutdownOnFinish();
		EasyMock.expectLastCall().times(1);
		
		Capture<Event> eventTurnedOff = new Capture<Event>();
		scheduler.queueEvent(EasyMock.capture(eventTurnedOff));
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
		
		Event eventAddServer = new Event(EventType.ADD_SERVER, handler, 1L, timeSharedMachine);
//		loadBalancer.handleEvent(eventAddServer);
		loadBalancer.serverIsUp(timeSharedMachine.getDescriptor());
		
		assertTrue(loadBalancer.getServers().size() == 1);
		simulator.removeMachine(0, true);
		assertTrue(loadBalancer.getServers().size() == 0);
		assertEquals(EventType.MACHINE_TURNED_OFF, eventTurnedOff.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, timeSharedMachine);
	}
	
	@Test
	public void testRemoveServerWithOneMachineAndNotUseForce() {
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		SchedulingHeuristic heuristic = EasyMock.createMock(SchedulingHeuristic.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		MachineDescriptor descriptor = new MachineDescriptor(0, false, MachineType.C1_MEDIUM, 0);
		EasyMock.expect(timeSharedMachine.getDescriptor()).andReturn(descriptor);
		timeSharedMachine.shutdownOnFinish();
		EasyMock.expectLastCall().times(1);

		EasyMock.expect(heuristic.removeMachine()).andReturn(timeSharedMachine);
		EasyMock.replay(monitor, scheduler, handler, timeSharedMachine, heuristic);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, heuristic, 2, 3);
		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
		
		Event event = new Event(EventType.ADD_SERVER, handler, 1L, descriptor);
//		loadBalancer.handleEvent(event);
		loadBalancer.serverIsUp(descriptor);
		
		simulator.removeMachine(0, false);
		EasyMock.verify(monitor, scheduler, handler, timeSharedMachine, heuristic);
	}
	
	@Ignore @Test
	public void testRemoveServerWithoutDescriptorAndUseForce() {
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		TimeSharedMachine timeSharedMachine2 = EasyMock.createStrictMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1).times(2);
		EasyMock.expect(scheduler.now()).andReturn(0L).times(4);
		EasyMock.expect(handler.getHandlerId()).andReturn(1).times(2);
		
		EasyMock.expect(timeSharedMachine.getDescriptor()).andReturn(new MachineDescriptor(0, true, MachineType.C1_MEDIUM, 0)).times(3);
		EasyMock.expect(timeSharedMachine2.getDescriptor()).andReturn(new MachineDescriptor(1, true, MachineType.C1_MEDIUM, 0)).times(4);
		Queue<Request> queue = new LinkedList<Request>();
		EasyMock.expect(timeSharedMachine2.getProcessorQueue()).andReturn(queue);
		timeSharedMachine2.shutdownOnFinish();
		EasyMock.expectLastCall().times(1);
		
		Capture<Event> eventTurnedOff = new Capture<Event>();
		scheduler.queueEvent(EasyMock.capture(eventTurnedOff));
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler, timeSharedMachine, timeSharedMachine2);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
		
//		loadBalancer.handleEvent(new JEEvent(JEEventType.ADD_SERVER, handler, 1L, timeSharedMachine));
//		loadBalancer.handleEvent(new JEEvent(JEEventType.ADD_SERVER, handler, 1L, timeSharedMachine2));
		
		loadBalancer.serverIsUp(timeSharedMachine.getDescriptor());
		loadBalancer.serverIsUp(timeSharedMachine2.getDescriptor());
		
		assertTrue(loadBalancer.getServers().size() == 2);
		simulator.removeMachine(0, true);
		assertTrue(loadBalancer.getServers().size() == 1);
		assertEquals(EventType.MACHINE_TURNED_OFF, eventTurnedOff.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, timeSharedMachine);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=AssertionError.class)
	public void testSetWorkloadParserWithNullWorkload() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createStrictMock(WorkloadParser.class);
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1).times(2);
		
		EasyMock.replay(monitor, scheduler, handler, workloadParser);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
		simulator.setWorkloadParser(null);
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testSetWorkloadParser() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createStrictMock(WorkloadParser.class);
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0L);
		
		Capture<Event> eventReadWorkload = new Capture<Event>();
		scheduler.queueEvent(EasyMock.capture(eventReadWorkload));
		EasyMock.expectLastCall().times(1);
		
		EasyMock.expect(workloadParser.hasNext()).andReturn(true);
		EasyMock.expect(workloadParser.next()).andReturn(new LinkedList<Request>());
		EasyMock.expect(workloadParser.hasNext()).andReturn(true);
		
		EasyMock.replay(monitor, scheduler, handler, workloadParser, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
		
		simulator.setWorkloadParser(workloadParser);
		Event event = new Event(EventType.READWORKLOAD, handler, 1L, timeSharedMachine);
//		simulator.handleEvent(event);
		simulator.readWorkload();
		
		assertEquals(EventType.READWORKLOAD, eventReadWorkload.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser, timeSharedMachine);
	}
	
	@Test
	public void testHandleEventChargeUsers() {
		while(!Configuration.getInstance().getSimulationInfo().isChargeDay()){
			Configuration.getInstance().getSimulationInfo().addDay();
		}
		
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		monitor.chargeUsers(31 * TimeUnit.DAY.getMillis() - 1);
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
		simulator.setMonitor(monitor);
		
		Event event = new Event(EventType.CHARGE_USERS, handler, 31 * TimeUnit.DAY.getMillis() - 1);
//		simulator.handleEvent(event);
		simulator.chargeUsers();
		
		EasyMock.verify(monitor, scheduler, handler);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testHandleEventReadWorkload() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createStrictMock(WorkloadParser.class);
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		Request request = EasyMock.createStrictMock(Request.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		Capture<Event> eventReadWorkload = new Capture<Event>();
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
		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
		
		simulator.setWorkloadParser(workloadParser);
		Event event = new Event(EventType.READWORKLOAD, handler, 1L, timeSharedMachine);
//		simulator.handleEvent(event);
		simulator.readWorkload();
		
		assertEquals(EventType.READWORKLOAD, eventReadWorkload.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser, timeSharedMachine, request);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleEventReadWorkloadWithoutMoreRequests() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createStrictMock(WorkloadParser.class);
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		Request request = EasyMock.createStrictMock(Request.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		Capture<Event> eventReadWorkload = new Capture<Event>();
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
		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
		
		simulator.setWorkloadParser(workloadParser);
		Event event = new Event(EventType.READWORKLOAD, handler, 1L, timeSharedMachine);
//		simulator.handleEvent(event);
		simulator.readWorkload();
		
		assertEquals(EventType.NEWREQUEST, eventReadWorkload.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser, timeSharedMachine, request);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleEventCollectStatistics() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createStrictMock(WorkloadParser.class);
		EventHandler handler = EasyMock.createStrictMock(EventHandler.class);
		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createStrictMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleMultiTierApplication.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0L).times(1);
		
		Capture<Event> eventCollectStatistics = new Capture<Event>();
		scheduler.queueEvent(EasyMock.capture(eventCollectStatistics));
		EasyMock.expectLastCall().times(1);
		
		monitor.sendStatistics(1, new MachineStatistics(0, 0, 0, 0), 3);
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler, workloadParser, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, new RoundRobinHeuristic(), 2, 3);
		SimpleMultiTierApplication simulator = new SimpleMultiTierApplication(scheduler, loadBalancer);
		simulator.setMonitor(monitor);
		simulator.setWorkloadParser(workloadParser);
		
		Event event = new Event(EventType.COLLECT_STATISTICS, handler, 1L, timeSharedMachine);
//		simulator.handleEvent(event);
		simulator.collectStatistics();
		
		assertEquals(EventType.COLLECT_STATISTICS, eventCollectStatistics.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser, timeSharedMachine);
	}
	
//	@Test
//	public void testStartWithoutBeingLastSimulationDay() throws Exception{
//		Capture<Event> firstEvent = new Capture<Event>();
//		Capture<Event> secondEvent = new Capture<Event>();
//		
//		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
//		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleMultiTierApplication.class))).andReturn(1);
//		EasyMock.expect(scheduler.now()).andReturn(0l);
//		scheduler.queueEvent(EasyMock.capture(firstEvent));
//		EasyMock.expect(scheduler.now()).andReturn(0l);
//		scheduler.queueEvent(EasyMock.capture(secondEvent));
//		scheduler.start();
//		
//		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
//		EasyMock.expect(monitor.isOptimal()).andReturn(false);
//		
//		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
//		loadBalancer.setMonitor(monitor, null);
//		
//		EasyMock.replay(monitor, loadBalancer, scheduler);
//		
//		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
//		simulator.setMonitor(monitor);
//		simulator.start();
//		
//		assertEquals(EventType.READWORKLOAD, firstEvent.getValue().getType());
//		assertEquals(EventType.COLLECT_STATISTICS, secondEvent.getValue().getType());
//		
//		EasyMock.verify(monitor, loadBalancer, scheduler);
//	}
//	
//	@Test
//	public void testStartInFirstChargeUsersDay() throws Exception{
//		Capture<Event> firstEvent = new Capture<Event>();
//		Capture<Event> secondEvent = new Capture<Event>();
//		Capture<Event> thirdEvent = new Capture<Event>();
//
//		SimulationInfo info = Configuration.getInstance().getSimulationInfo();
//		while(!info.isChargeDay()){
//			info.addDay();
//		}
//		
//		assert info.isChargeDay();
//		
//		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
//		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleMultiTierApplication.class))).andReturn(1);
//		EasyMock.expect(scheduler.now()).andReturn(0l);
//		scheduler.queueEvent(EasyMock.capture(firstEvent));
//		EasyMock.expectLastCall().times(2);
//		scheduler.start();
//		EasyMock.expect(scheduler.now()).andReturn(0l);
//		scheduler.queueEvent(EasyMock.capture(secondEvent));
//		scheduler.queueEvent(EasyMock.capture(thirdEvent));
//		
//		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
//		EasyMock.expect(monitor.isOptimal()).andReturn(false);
//		
//		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
//		loadBalancer.setMonitor(monitor, null);
//		
//		EasyMock.replay(monitor, loadBalancer, scheduler);
//		
//		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
//		simulator.setMonitor(monitor);
//		simulator.start();
//		
//		assertEquals(EventType.READWORKLOAD, firstEvent.getValue().getType());
//		assertEquals(EventType.COLLECT_STATISTICS, secondEvent.getValue().getType());
//		assertEquals(EventType.CHARGE_USERS, thirdEvent.getValue().getType());
//
//		EasyMock.verify(monitor, loadBalancer, scheduler);
//	}
//	
//	@Test
//	public void testStartInOtherChargeUsersDay() throws Exception{
//		Capture<Event> firstEvent = new Capture<Event>();
//		Capture<Event> secondEvent = new Capture<Event>();
//		Capture<Event> thirdEvent = new Capture<Event>();
//
//		SimulationInfo info = Configuration.getInstance().getSimulationInfo();
//		while(!info.isChargeDay()){
//			info.addDay();
//		}
//		info.addDay();
//		while(!info.isChargeDay()){
//			info.addDay();
//		}
//		
//		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
//		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleMultiTierApplication.class))).andReturn(1);
//		EasyMock.expect(scheduler.now()).andReturn(0l);
//		scheduler.queueEvent(EasyMock.capture(firstEvent));
//		EasyMock.expect(scheduler.now()).andReturn(0l);
//		scheduler.queueEvent(EasyMock.capture(secondEvent));
//		scheduler.queueEvent(EasyMock.capture(thirdEvent));
//		scheduler.start();
//		
//		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
//		EasyMock.expect(monitor.isOptimal()).andReturn(false);
//		
//		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
//		loadBalancer.setMonitor(monitor, null);
//		
//		EasyMock.replay(monitor, loadBalancer, scheduler);
//		
//		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
//		simulator.setMonitor(monitor);
//		simulator.start();
//		
//		assertEquals(EventType.READWORKLOAD, firstEvent.getValue().getType());
//		assertEquals(EventType.COLLECT_STATISTICS, secondEvent.getValue().getType());
//		assertEquals(EventType.CHARGE_USERS, thirdEvent.getValue().getType());
//
//		EasyMock.verify(monitor, loadBalancer, scheduler);
//	}
//	
//	@Test
//	public void testStartBeingLastSimulationDay() throws Exception{
//		Capture<Event> firstEvent = new Capture<Event>();
//		Capture<Event> secondEvent = new Capture<Event>();
//
//		SimulationInfo info = Configuration.getInstance().getSimulationInfo();
//		while(!info.isFinishDay()){
//			info.addDay();
//		}
//		
//		assert info.isFinishDay();
//		
//		EventScheduler scheduler = EasyMock.createStrictMock(EventScheduler.class);
//		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleMultiTierApplication.class))).andReturn(1);
//		EasyMock.expect(scheduler.now()).andReturn(0l);
//		scheduler.queueEvent(EasyMock.capture(firstEvent));
//		EasyMock.expect(scheduler.now()).andReturn(0l);
//		scheduler.queueEvent(EasyMock.capture(secondEvent));
//		scheduler.start();
//		
//		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
//		loadBalancer.setMonitor(EasyMock.isA(Monitor.class), null);
//		
//		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
//		EasyMock.expect(monitor.isOptimal()).andReturn(false);
//		
//		EasyMock.replay(monitor, loadBalancer, scheduler);
//		
//		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
//		simulator.setMonitor(monitor);
//		simulator.start();
//		
//		assertEquals(EventType.READWORKLOAD, firstEvent.getValue().getType());
//		assertEquals(EventType.COLLECT_STATISTICS, secondEvent.getValue().getType());
//		
//		EasyMock.verify(monitor, loadBalancer, scheduler);
//	}
//
//	@Test
//	public void testStartBeingChargeDay() throws Exception{
//		Capture<Event> firstEvent = new Capture<Event>();
//		Capture<Event> secondEvent = new Capture<Event>();
//		Capture<Event> thirdEvent = new Capture<Event>();
//
//		SimulationInfo info = Configuration.getInstance().getSimulationInfo();
//		while(!info.isChargeDay()){
//			info.addDay();
//		}
//		
//		assert info.isChargeDay();
//		
//		EventScheduler scheduler = EasyMock.createMock(EventScheduler.class);
//		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleMultiTierApplication.class))).andReturn(1);
//		EasyMock.expect(scheduler.now()).andReturn(0l);
//		scheduler.queueEvent(EasyMock.capture(firstEvent));
//		EasyMock.expect(scheduler.now()).andReturn(0l);
//		scheduler.queueEvent(EasyMock.capture(secondEvent));
//		scheduler.queueEvent(EasyMock.capture(thirdEvent));
//		scheduler.start();
//		
//		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
//		loadBalancer.setMonitor(EasyMock.isA(Monitor.class), null);
//		
//		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
//		EasyMock.expect(monitor.isOptimal()).andReturn(false);
//		EasyMock.replay(monitor, loadBalancer, scheduler);
//		
//		SimpleMultiTierApplication simulator =  new SimpleMultiTierApplication(scheduler, loadBalancer);
//		simulator.setMonitor(monitor);
//		simulator.start();
//		
//		assertEquals(EventType.READWORKLOAD, firstEvent.getValue().getType());
//		assertEquals(EventType.COLLECT_STATISTICS, secondEvent.getValue().getType());
//		assertEquals(EventType.CHARGE_USERS, thirdEvent.getValue().getType());
//		
//		assertEquals(31 * TimeUnit.DAY.getMillis() - 1, thirdEvent.getValue().getScheduledTime());
//		
//		EasyMock.verify(monitor, loadBalancer, scheduler);
//	}
}