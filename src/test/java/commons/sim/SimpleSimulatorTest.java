package commons.sim;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import provisioning.Monitor;
import util.ValidConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.config.Configuration;
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
import commons.sim.util.ApplicationFactory;
import commons.sim.util.SimulatorProperties;
import commons.util.SimulationInfo;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class, ApplicationFactory.class, SimpleSimulator.class})
public class SimpleSimulatorTest extends ValidConfigurationTest {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullConfiguration();
	}

	@Test(expected=AssertionError.class)
	public void testAddServerWithInexistentTier() {
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(3);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator = new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		simulator.addServer(4, new MachineDescriptor(0, false, MachineType.MEDIUM, 0), false);
		
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test(expected=RuntimeException.class)
	public void testAddServerWithNullMachine() {
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(3);
		EasyMock.expect(scheduler.now()).andReturn(0L);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator = new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		simulator.addServer(0, null, false);
		
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test
	public void testAddServer() {
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(3);
		EasyMock.expect(scheduler.now()).andReturn(0L).times(2);
		Capture<JEEvent> event = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(event));
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		simulator.addServer(0, new MachineDescriptor(0, false, MachineType.MEDIUM, 0), false);
		assertEquals(JEEventType.ADD_SERVER, event.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test(expected=AssertionError.class)
	public void testRemoveServerWithInexistentTier() {
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(3);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator = new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		simulator.removeServer(4, new MachineDescriptor(0, false, MachineType.MEDIUM, 0), false);
		
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test
	public void testRemoveServerNotUseForce() {
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(scheduler.now()).andReturn(0L);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		EasyMock.expect(timeSharedMachine.getDescriptor()).andReturn(new MachineDescriptor(0, false, MachineType.MEDIUM, 0)).times(2);
		timeSharedMachine.shutdownOnFinish();
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		JEEvent event = new JEEvent(JEEventType.ADD_SERVER, handler, 1L, timeSharedMachine);
		loadBalancer.handleEvent(event);
		
		assertTrue(loadBalancer.getServers().size() == 1);
		simulator.removeServer(0, new MachineDescriptor(0, false, MachineType.MEDIUM, 0), false);
		assertTrue(loadBalancer.getServers().size() == 0);
		
		EasyMock.verify(monitor, scheduler, handler, timeSharedMachine);
	}
	
	@Test
	public void testRemoveServerUseForce() {
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(scheduler.now()).andReturn(0L).times(3);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		EasyMock.expect(timeSharedMachine.getDescriptor()).andReturn(new MachineDescriptor(0, false, MachineType.MEDIUM, 0)).times(2);
		Queue<Request> queue = new LinkedList<Request>();
		EasyMock.expect(timeSharedMachine.getProcessorQueue()).andReturn(queue);
		timeSharedMachine.shutdownOnFinish();
		EasyMock.expectLastCall().times(1);
		
		Capture<JEEvent> eventTurnedOff = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(eventTurnedOff));
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		JEEvent eventAddServer = new JEEvent(JEEventType.ADD_SERVER, handler, 1L, timeSharedMachine);
		loadBalancer.handleEvent(eventAddServer);
		
		assertTrue(loadBalancer.getServers().size() == 1);
		simulator.removeServer(0, new MachineDescriptor(0, false, MachineType.MEDIUM, 0), true);
		assertTrue(loadBalancer.getServers().size() == 0);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, eventTurnedOff.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, timeSharedMachine);
	}
	
	
	@Test(expected=AssertionError.class)
	public void testRemoveServerWithoutDescriptorAndInexistentTier() {
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(3);
		
		EasyMock.replay(monitor, scheduler, handler);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		simulator.removeServer(4, false);
		
		EasyMock.verify(monitor, scheduler, handler);
	}
	
	@Test
	public void testRemoveServerWithoutDescriptorAndNotUseForce() {
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(scheduler.now()).andReturn(0L);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		EasyMock.expect(timeSharedMachine.getDescriptor()).andReturn(new MachineDescriptor(0, false, MachineType.MEDIUM, 0)).times(3);
		timeSharedMachine.shutdownOnFinish();
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		JEEvent event = new JEEvent(JEEventType.ADD_SERVER, handler, 1L, timeSharedMachine);
		loadBalancer.handleEvent(event);
		
		assertTrue(loadBalancer.getServers().size() == 1);
		simulator.removeServer(0, false);
		assertTrue(loadBalancer.getServers().size() == 0);
		
		EasyMock.verify(monitor, scheduler, handler, timeSharedMachine);
	}
	
	@Test
	public void testConstructorWithNullMonitor(){
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(1);
//		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(5000l);
//		EasyMock.expect(config.getParserPageSize()).andReturn(TickSize.MINUTE);
		EasyMock.expect(config.getSimulationInfo()).andReturn(new SimulationInfo(0, 0));
		
//		ApplicationFactory appFactory = EasyMock.createStrictMock(ApplicationFactory.class);
//		PowerMock.mockStatic(ApplicationFactory.class);
//		EasyMock.expect(ApplicationFactory.getInstance()).andReturn(appFactory);
//		EasyMock.expect(appFactory.createNewApplication(EasyMock.isA(JEEventScheduler.class), 
//				EasyMock.anyObject(Monitor.class))).andReturn(new LoadBalancer[0]);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleSimulator.class))).andReturn(1);
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		PowerMock.replayAll(config, scheduler, loadBalancer);
		
		new SimpleSimulator(scheduler, null, loadBalancer);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testRemoveServerWithoutDescriptorAndUseForce() {
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(scheduler.now()).andReturn(0L).times(3);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		EasyMock.expect(timeSharedMachine.getDescriptor()).andReturn(new MachineDescriptor(0, true, MachineType.MEDIUM, 0)).times(4);
		Queue<Request> queue = new LinkedList<Request>();
		EasyMock.expect(timeSharedMachine.getProcessorQueue()).andReturn(queue);
		timeSharedMachine.shutdownOnFinish();
		EasyMock.expectLastCall().times(1);
		
		Capture<JEEvent> eventTurnedOff = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(eventTurnedOff));
		EasyMock.expectLastCall().times(1);
		
		EasyMock.replay(monitor, scheduler, handler, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		JEEvent eventAddServer = new JEEvent(JEEventType.ADD_SERVER, handler, 1L, timeSharedMachine);
		loadBalancer.handleEvent(eventAddServer);
		
		assertTrue(loadBalancer.getServers().size() == 1);
		simulator.removeServer(0, true);
		assertTrue(loadBalancer.getServers().size() == 0);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, eventTurnedOff.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, timeSharedMachine);
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected=AssertionError.class)
	public void testSetWorkloadParserWithNullWorkload() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createMock(WorkloadParser.class);
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		
		EasyMock.replay(monitor, scheduler, handler, workloadParser);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		simulator.setWorkloadParser(null);
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser);
	}
	
	@Test
	public void testSetWorkloadParser() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createMock(WorkloadParser.class);
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createMock(TimeSharedMachine.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0L);
		
		Capture<JEEvent> eventReadWorkload = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(eventReadWorkload));
		EasyMock.expectLastCall().times(1);
		
		EasyMock.expect(workloadParser.hasNext()).andReturn(true).times(2);
		EasyMock.expect(workloadParser.next()).andReturn(new LinkedList<Request>());
		
		EasyMock.replay(monitor, scheduler, handler, workloadParser, timeSharedMachine);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		simulator.setWorkloadParser(workloadParser);
		JEEvent event = new JEEvent(JEEventType.READWORKLOAD, handler, 1L, timeSharedMachine);
		simulator.handleEvent(event);
		assertEquals(JEEventType.READWORKLOAD, eventReadWorkload.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser, timeSharedMachine);
	}
	
	@Test
	public void testHandleEventChargeUsers() {
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getSimulationInfo()).andReturn(new SimulationInfo(30, 0));
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(40l);
		EasyMock.expect(config.getSimulationInfo()).andReturn(new SimulationInfo(30, 0));
		
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		monitor.chargeUsers(TickSize.DAY.getTickInMillis() * 31);
		EasyMock.expectLastCall().times(1);
		
		PowerMock.replayAll(monitor, scheduler, handler, config);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		JEEvent event = new JEEvent(JEEventType.CHARGE_USERS, handler, 1L, 31);
		simulator.handleEvent(event);
		
		PowerMock.verifyAll();
	}

	@Test
	public void testHandleEventReadWorkload() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createMock(WorkloadParser.class);
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createMock(TimeSharedMachine.class);
		Request request = EasyMock.createMock(Request.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0L);
		
		Capture<JEEvent> eventReadWorkload = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(eventReadWorkload));
		EasyMock.expectLastCall().times(2);
		
		EasyMock.expect(workloadParser.hasNext()).andReturn(true).times(2);
		List<Request> requests = new LinkedList<Request>();
		requests.add(request);
		EasyMock.expect(workloadParser.next()).andReturn(requests);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(0l);
		
		EasyMock.replay(monitor, scheduler, handler, workloadParser, timeSharedMachine, request);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		simulator.setWorkloadParser(workloadParser);
		JEEvent event = new JEEvent(JEEventType.READWORKLOAD, handler, 1L, timeSharedMachine);
		simulator.handleEvent(event);
		assertEquals(JEEventType.READWORKLOAD, eventReadWorkload.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser, timeSharedMachine, request);
	}
	
	@Test
	public void testHandleEventReadWorkloadWithoutMoreRequests() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createMock(WorkloadParser.class);
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createMock(TimeSharedMachine.class);
		Request request = EasyMock.createMock(Request.class);
		
		EasyMock.expect(scheduler.registerHandler(EasyMock.anyObject(SimpleSimulator.class))).andReturn(1).times(2);
		EasyMock.expect(handler.getHandlerId()).andReturn(1);
		
		Capture<JEEvent> eventReadWorkload = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(eventReadWorkload));
		EasyMock.expectLastCall();
		
		EasyMock.expect(workloadParser.hasNext()).andReturn(true);
		EasyMock.expect(workloadParser.hasNext()).andReturn(false);
		List<Request> requests = new LinkedList<Request>();
		requests.add(request);
		EasyMock.expect(workloadParser.next()).andReturn(requests);
		workloadParser.close();
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(0l);
		
		EasyMock.replay(monitor, scheduler, handler, workloadParser, timeSharedMachine, request);
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		simulator.setWorkloadParser(workloadParser);
		JEEvent event = new JEEvent(JEEventType.READWORKLOAD, handler, 1L, timeSharedMachine);
		simulator.handleEvent(event);
		assertEquals(JEEventType.NEWREQUEST, eventReadWorkload.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser, timeSharedMachine, request);
	}
	
	@Test
	public void testHandleEventCollectStatistics() {
		WorkloadParser<List<Request>> workloadParser = EasyMock.createMock(WorkloadParser.class);
		JEEventHandler handler = EasyMock.createMock(JEEventHandler.class);
		JEEventScheduler scheduler = EasyMock.createMock(JEEventScheduler.class);
		Monitor monitor = EasyMock.createMock(Monitor.class);
		TimeSharedMachine timeSharedMachine = EasyMock.createMock(TimeSharedMachine.class);
		
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
		
		LoadBalancer loadBalancer = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic(), 2, 3);
		SimpleSimulator simulator = new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		simulator.setWorkloadParser(workloadParser);
		JEEvent event = new JEEvent(JEEventType.COLLECT_STATISTICS, handler, 1L, timeSharedMachine);
		simulator.handleEvent(event);
		assertEquals(JEEventType.COLLECT_STATISTICS, eventCollectStatistics.getValue().getType());
		
		EasyMock.verify(monitor, scheduler, handler, workloadParser, timeSharedMachine);
	}
	
	@Test
	public void testStartWithoutBeingLastSimulationDay() throws Exception{
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getSimulationInfo()).andReturn(new SimulationInfo(0, 0)).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(2l);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleSimulator.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		scheduler.start();
		PowerMock.expectNew(JEEventScheduler.class).andReturn(scheduler);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(2);
		
		ApplicationFactory appFactory = EasyMock.createStrictMock(ApplicationFactory.class);
		PowerMock.mockStatic(ApplicationFactory.class);
		EasyMock.expect(ApplicationFactory.getInstance()).andReturn(appFactory);
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = loadBalancer;
		EasyMock.expect(appFactory.createNewApplication(EasyMock.isA(JEEventScheduler.class), 
				EasyMock.anyObject(Monitor.class))).andReturn(loadBalancers);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		PowerMock.replayAll(config, appFactory, monitor, loadBalancer, scheduler);
		
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		simulator.start();
		
	}
	
	@Test
	public void testStartInFirstChargeUsersDay() throws Exception{
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getSimulationInfo()).andReturn(new SimulationInfo(30, 0)).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(40l);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleSimulator.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		scheduler.start();
		PowerMock.expectNew(JEEventScheduler.class).andReturn(scheduler);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(2);
		
		ApplicationFactory appFactory = EasyMock.createStrictMock(ApplicationFactory.class);
		PowerMock.mockStatic(ApplicationFactory.class);
		EasyMock.expect(ApplicationFactory.getInstance()).andReturn(appFactory);
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = loadBalancer;
		EasyMock.expect(appFactory.createNewApplication(EasyMock.isA(JEEventScheduler.class), 
				EasyMock.anyObject(Monitor.class))).andReturn(loadBalancers);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		PowerMock.replayAll(config, appFactory, monitor, loadBalancer, scheduler);
		
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		simulator.start();
		
	}
	
	@Test
	public void testStartInOtherChargeUsersDay() throws Exception{
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getSimulationInfo()).andReturn(new SimulationInfo(180, 5)).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(181l);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleSimulator.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(0l);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		scheduler.start();
		PowerMock.expectNew(JEEventScheduler.class).andReturn(scheduler);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(2);
		
		ApplicationFactory appFactory = EasyMock.createStrictMock(ApplicationFactory.class);
		PowerMock.mockStatic(ApplicationFactory.class);
		EasyMock.expect(ApplicationFactory.getInstance()).andReturn(appFactory);
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = loadBalancer;
		EasyMock.expect(appFactory.createNewApplication(EasyMock.isA(JEEventScheduler.class), 
				EasyMock.anyObject(Monitor.class))).andReturn(loadBalancers);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		PowerMock.replayAll(config, appFactory, monitor, loadBalancer, scheduler);
		
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		simulator.start();
		
	}
	
	@Test
	public void testStartBeingLastSimulationDay() throws Exception{
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getSimulationInfo()).andReturn(new SimulationInfo(1, 0)).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.PLANNING_PERIOD)).andReturn(1l);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleSimulator.class))).andReturn(1);
		scheduler.start();
		PowerMock.expectNew(JEEventScheduler.class).andReturn(scheduler);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		ApplicationFactory appFactory = EasyMock.createStrictMock(ApplicationFactory.class);
		PowerMock.mockStatic(ApplicationFactory.class);
		EasyMock.expect(ApplicationFactory.getInstance()).andReturn(appFactory);
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = loadBalancer;
		EasyMock.expect(appFactory.createNewApplication(EasyMock.isA(JEEventScheduler.class), 
				EasyMock.anyObject(Monitor.class))).andReturn(loadBalancers);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		PowerMock.replayAll(config, appFactory, monitor, loadBalancer, scheduler);
		
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		simulator.start();
		
	}
	
	@Test
	public void testParseRequest(){
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getSimulationInfo()).andReturn(new SimulationInfo(0, 0));
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(2);
		
//		ApplicationFactory appFactory = EasyMock.createStrictMock(ApplicationFactory.class);
//		PowerMock.mockStatic(ApplicationFactory.class);
//		EasyMock.expect(ApplicationFactory.getInstance()).andReturn(appFactory);
		LoadBalancer[] loadBalancers = new LoadBalancer[1];
		loadBalancers[0] = loadBalancer;
//		EasyMock.expect(appFactory.createNewApplication(EasyMock.isA(JEEventScheduler.class), 
//				EasyMock.anyObject(Monitor.class))).andReturn(loadBalancers);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(SimpleSimulator.class))).andReturn(1);
		
		PowerMock.replayAll(config, monitor, loadBalancer, scheduler);
		
		SimpleSimulator simulator =  new SimpleSimulator(scheduler, monitor, loadBalancer);
		
		Request request = new Request(1, 1222l, 0, 10000l, 101l, 5000l, new long[]{500});
		JEEvent event = simulator.parseEvent(request);
		assertNotNull(event);
		assertEquals(JEEventType.NEWREQUEST, event.getType());
		assertEquals(request, (Request) event.getValue()[0]);
		assertEquals(request.getArrivalTimeInMillis(), event.getScheduledTime());
		
		PowerMock.verifyAll();
	}

}
