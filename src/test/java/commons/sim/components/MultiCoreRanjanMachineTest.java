package commons.sim.components;

import static commons.sim.util.SimulatorProperties.*;
import static org.junit.Assert.*;

import java.util.Queue;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JEEventScheduler.class, Configuration.class})
public class MultiCoreRanjanMachineTest {
	
	private static final long DEFAULT_BACKLOG_SIZE = 2;
	private static final long DEFAULT_MAX_NUM_OF_THREADS = 3;
	
	private MachineDescriptor descriptor;

	@Before
	public void setUp() {
		this.descriptor = new MachineDescriptor(1, false, MachineType.SMALL);
	}

	@Test
	public void testConstructor(){
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replayAll(config);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, null);
		assertEquals(descriptor, machine.getDescriptor());
		assertNull(machine.getLoadBalancer());
		assertNotNull(machine.getProcessorQueue());
		assertTrue(machine.getProcessorQueue().isEmpty());
		
		PowerMock.verifyAll();
	}

	private static Configuration mockConfiguration(double machinePower, long maxNumberOfThreads, long backlogSize) {
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getRelativePower(MachineType.SMALL)).andReturn(machinePower);
		EasyMock.expect(config.getLong(RANJAN_HEURISTIC_NUMBER_OF_TOKENS)).andReturn(maxNumberOfThreads);
		EasyMock.expect(config.getLong(RANJAN_HEURISTIC_BACKLOG_SIZE)).andReturn(backlogSize);
		return config;
	}
	
	/**
	 * This method verifies that a single request is correctly added to a machine, without already
	 * processing requests, since the limit of threads is not reached
	 * @throws Exception 
	 */
	@Test
	public void testSendBigRequestWithEmptyServerAndOneCore(){
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(5000L);
		EasyMock.replay(loadBalancer);
		
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		MultiCoreRanjanMachine machine = PowerMock.createStrictPartialMock(MultiCoreRanjanMachine.class, new String[]{"handleEvent"}, scheduler, descriptor, loadBalancer);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		machine.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		
		EasyMock.replay(request, machine);
		
		machine.sendRequest(request);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertTrue(queue.isEmpty());
		
		queue = machine.getExecutingQueue();
		assertNotNull(queue);
		assertEquals(1, queue.size());
		assertTrue(queue.contains(request));
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.PREEMPTION, event.getType());
		assertEquals(new JETime(100L), event.getScheduledTime());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testSendSomeBigRequestsWithEmptyMachineAndMultiCores(){
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.replay(loadBalancer);

		Request request = EasyMock.createStrictMock(Request.class);
		Request request2 = EasyMock.createStrictMock(Request.class);
		Request request3 = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(5000L);
		request2.assignTo(MachineType.SMALL);
		EasyMock.expect(request2.getTotalToProcess()).andReturn(5000L);
		request3.assignTo(MachineType.SMALL);
		EasyMock.expect(request3.getTotalToProcess()).andReturn(5000L);
		
		Configuration config = mockConfiguration(2, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		MultiCoreRanjanMachine machine = PowerMock.createStrictPartialMock(MultiCoreRanjanMachine.class, new String[]{"handleEvent"}, scheduler, descriptor, loadBalancer);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		Capture<JEEvent> captured2 = new Capture<JEEvent>();
		machine.handleEvent(EasyMock.capture(captured));
		machine.handleEvent(EasyMock.capture(captured2));
		
		EasyMock.replay(request, request2, request3, machine);
		
		machine.sendRequest(request);
		machine.sendRequest(request2);
		machine.sendRequest(request3);
		
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertEquals(1, queue.size());
		assertTrue(queue.contains(request3));
		
		queue = machine.getExecutingQueue();
		assertNotNull(queue);
		assertEquals(2, queue.size());
		assertTrue(queue.contains(request));
		assertTrue(queue.contains(request2));
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.PREEMPTION, event.getType());
		assertEquals(new JETime(100L), event.getScheduledTime());
		assertEquals(request, event.getValue()[1]);
		
		event = captured2.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.PREEMPTION, event.getType());
		assertEquals(new JETime(100L), event.getScheduledTime());
		assertEquals(request2, event.getValue()[1]);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testSendSmallRequestWithEmptyMachineAndOneCore(){

		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		MultiCoreRanjanMachine machine = PowerMock.createStrictPartialMock(MultiCoreRanjanMachine.class, new String[]{"handleEvent"}, scheduler, descriptor, loadBalancer);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		machine.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		
		EasyMock.replay(loadBalancer, request, machine);
		
		machine.sendRequest(request);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertTrue(queue.isEmpty());
		
		queue = machine.getExecutingQueue();
		assertNotNull(queue);
		assertEquals(1, queue.size());
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.PREEMPTION, event.getType());
		assertEquals(new JETime(50L), event.getScheduledTime());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testSendSomeDifferentRequestsThatMachineCanProcessInParallalel(){
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.replay(loadBalancer);

		Request request = new Request("1", "am", "us1", 0, 0, 0, new long[]{50, 50, 50, 50});
		Request request2 = new Request("2", "am", "us1", 0, 0, 0, new long[]{140, 140, 140, 140});
		Request request3 = new Request("3", "am", "us1", 0, 0, 0, new long[]{200, 200, 200, 200});
		
		Configuration config = mockConfiguration(3, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(4d);
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		MultiCoreRanjanMachine machine = PowerMock.createStrictPartialMock(MultiCoreRanjanMachine.class, new String[]{"handleEvent"}, scheduler, descriptor, loadBalancer);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		Capture<JEEvent> captured2 = new Capture<JEEvent>();
		Capture<JEEvent> captured3 = new Capture<JEEvent>();
		machine.handleEvent(EasyMock.capture(captured));
		machine.handleEvent(EasyMock.capture(captured2));
		machine.handleEvent(EasyMock.capture(captured3));
		EasyMock.expectLastCall();
		
		EasyMock.replay(machine);
		
		machine.sendRequest(request2);
		machine.sendRequest(request);
		machine.sendRequest(request3);
		
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertTrue(queue.isEmpty());
		
		queue = machine.getExecutingQueue();
		assertNotNull(queue);
		assertEquals(3, queue.size());
		assertTrue(queue.contains(request));
		assertTrue(queue.contains(request2));
		assertTrue(queue.contains(request3));
		
		scheduler.start();
		
		//Capture for first request quantum finish
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.PREEMPTION, event.getType());
		assertEquals(new JETime(50L), event.getScheduledTime());
		assertEquals(request, event.getValue()[1]);
		
		//Capture for second request quantum finish
		event = captured2.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.PREEMPTION, event.getType());
		assertEquals(new JETime(100L), event.getScheduledTime());
		assertEquals(request2, event.getValue()[1]);
		
		//Capture for third request quantum finish
		event = captured3.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.PREEMPTION, event.getType());
		assertEquals(new JETime(100L), event.getScheduledTime());
		assertEquals(request3, event.getValue()[1]);
		
		PowerMock.verifyAll();
	}

	@Test
	public void testShutdownWithEmptyMachine(){
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(scheduler.registerHandler(loadBalancer));
		Capture<JEEvent> captured = new Capture<JEEvent>();
		loadBalancer.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		
		PowerMock.replayAll(config, loadBalancer);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.shutdownOnFinish();
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, event.getType());
		assertEquals(machine.getDescriptor(), event.getValue()[0]);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testShutdownWithNonEmptyMachine(){
		Configuration config = mockConfiguration(4, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		JEEventScheduler scheduler = new JEEventScheduler();
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(150L);
		request.update(100L);
		EasyMock.expect(request.isFinished()).andReturn(false);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100l);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(10000l);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(request);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(scheduler.registerHandler(loadBalancer));
		Capture<JEEvent> captured = new Capture<JEEvent>();
		loadBalancer.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		PowerMock.replayAll(config, loadBalancer, request);

		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		machine.shutdownOnFinish();
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, event.getType());
		assertEquals(machine.getDescriptor(), event.getValue()[0]);
		
		PowerMock.verifyAll();
	}

	/**
	 * With one core and a number of threads equals to three, three requests stay
	 * in processor processing queue and one is stored in the backlog
	 */
	@Test
	public void testHandlePreemptionWithMoreRequestThanCanRunWithOneCore(){
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request firstRequest = EasyMock.createStrictMock(Request.class);
		firstRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(50L);
		firstRequest.update(50L);
		EasyMock.expect(firstRequest.isFinished()).andReturn(true);
		EasyMock.expect(firstRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(firstRequest.getResponseSizeInBytes()).andReturn(100L);

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		secondRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(200L);
		secondRequest.update(100L);
		EasyMock.expect(secondRequest.isFinished()).andReturn(false);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(100L);
		secondRequest.update(100L);
		EasyMock.expect(secondRequest.isFinished()).andReturn(true);
		EasyMock.expect(secondRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(secondRequest.getResponseSizeInBytes()).andReturn(100L);

		Request thirdRequest = EasyMock.createStrictMock(Request.class);
		thirdRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(thirdRequest.getTotalToProcess()).andReturn(100L);
		thirdRequest.update(100L);
		EasyMock.expect(thirdRequest.isFinished()).andReturn(true);
		EasyMock.expect(thirdRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(thirdRequest.getResponseSizeInBytes()).andReturn(100L);
		
		//Request that is initially assigned to backlog
		Request fourthRequest = EasyMock.createStrictMock(Request.class);
		fourthRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(fourthRequest.getTotalToProcess()).andReturn(100L);
		fourthRequest.update(100L);
		EasyMock.expect(fourthRequest.isFinished()).andReturn(true);
		EasyMock.expect(fourthRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(fourthRequest.getResponseSizeInBytes()).andReturn(100L);

		loadBalancer.reportRequestFinished(firstRequest);
		loadBalancer.reportRequestFinished(thirdRequest);
		loadBalancer.reportRequestFinished(fourthRequest);
		loadBalancer.reportRequestFinished(secondRequest);
		
		PowerMock.replayAll(config, loadBalancer, firstRequest, secondRequest, thirdRequest, fourthRequest);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		machine.sendRequest(thirdRequest);
		machine.sendRequest(fourthRequest);

		Queue<Request> queue = machine.getProcessorQueue();
		assertEquals(2, queue.size());
		assertEquals(secondRequest, queue.poll());
		assertEquals(thirdRequest, queue.poll());
		assertNull(queue.poll());
		
		queue = machine.getExecutingQueue();
		assertEquals(1, queue.size());
		assertEquals(firstRequest, queue.poll());
		assertNull(queue.poll());
		
		scheduler.start();
		
		assertTrue(machine.getProcessorQueue().isEmpty());
		assertTrue(machine.getExecutingQueue().isEmpty());
		
		PowerMock.verify();
	}
	
	/**
	 * With three cores and a number of threads equals to three, three requests are
	 * being processed simultaneously and one is stored in the backlog
	 */
	@Test
	public void testHandlePreemptionWithMoreRequestThanCanRunWithMultiCore(){
		Configuration config = mockConfiguration(3, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request firstRequest = EasyMock.createStrictMock(Request.class);
		firstRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(50L);
		firstRequest.update(50L);
		EasyMock.expect(firstRequest.isFinished()).andReturn(true);
		EasyMock.expect(firstRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(firstRequest.getResponseSizeInBytes()).andReturn(100L);

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		secondRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(200L);
		secondRequest.update(100L);
		EasyMock.expect(secondRequest.isFinished()).andReturn(false);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(100L);
		secondRequest.update(100L);
		EasyMock.expect(secondRequest.isFinished()).andReturn(true);
		EasyMock.expect(secondRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(secondRequest.getResponseSizeInBytes()).andReturn(100L);

		Request thirdRequest = EasyMock.createStrictMock(Request.class);
		thirdRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(thirdRequest.getTotalToProcess()).andReturn(100L);
		thirdRequest.update(100L);
		EasyMock.expect(thirdRequest.isFinished()).andReturn(true);
		EasyMock.expect(thirdRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(thirdRequest.getResponseSizeInBytes()).andReturn(100L);
		
		//Request that is initially assigned to backlog
		Request fourthRequest = EasyMock.createStrictMock(Request.class);
		fourthRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(fourthRequest.getTotalToProcess()).andReturn(100L);
		fourthRequest.update(100L);
		EasyMock.expect(fourthRequest.isFinished()).andReturn(true);
		EasyMock.expect(fourthRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(fourthRequest.getResponseSizeInBytes()).andReturn(100L);

		loadBalancer.reportRequestFinished(firstRequest);
		loadBalancer.reportRequestFinished(thirdRequest);
		loadBalancer.reportRequestFinished(fourthRequest);
		loadBalancer.reportRequestFinished(secondRequest);
		
		PowerMock.replayAll(config, loadBalancer, firstRequest, secondRequest, thirdRequest, fourthRequest);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		machine.sendRequest(thirdRequest);
		machine.sendRequest(fourthRequest);

		Queue<Request> queue = machine.getProcessorQueue();
		assertEquals(0, queue.size());
		
		queue = machine.getExecutingQueue();
		assertEquals(3, queue.size());
		assertEquals(firstRequest, queue.poll());
		assertEquals(secondRequest, queue.poll());
		assertEquals(thirdRequest, queue.poll());
		assertNull(queue.poll());
		
		scheduler.start();
		
		assertTrue(machine.getProcessorQueue().isEmpty());
		assertTrue(machine.getExecutingQueue().isEmpty());
		
		PowerMock.verify();
	}

	/**
	 * With one core and a number of threads equals to six, three requests are
	 * in the processor queue, two requests stay waiting in the backlog and one request is rejected
	 */
	@Test
	public void testSendMoreRequestThanCanRunAndWaitAndOneCore(){
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request firstRequest = EasyMock.createStrictMock(Request.class);
		firstRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(50L);
		firstRequest.update(50L);
		EasyMock.expect(firstRequest.isFinished()).andReturn(true);
		EasyMock.expect(firstRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(firstRequest.getResponseSizeInBytes()).andReturn(100L);
		
		Request secondRequest = EasyMock.createStrictMock(Request.class);
		secondRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(100L);
		secondRequest.update(100L);
		EasyMock.expect(secondRequest.isFinished()).andReturn(true);
		EasyMock.expect(secondRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(secondRequest.getResponseSizeInBytes()).andReturn(100L);
		
		Request thirdRequest = EasyMock.createStrictMock(Request.class);
		thirdRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(thirdRequest.getTotalToProcess()).andReturn(100L);
		thirdRequest.update(100L);
		EasyMock.expect(thirdRequest.isFinished()).andReturn(true);
		EasyMock.expect(thirdRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(thirdRequest.getResponseSizeInBytes()).andReturn(100L);
		
		//Requests initially assigned to backlog
		Request fourthRequest = EasyMock.createStrictMock(Request.class);
		fourthRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(fourthRequest.getTotalToProcess()).andReturn(100L);
		fourthRequest.update(100L);
		EasyMock.expect(fourthRequest.isFinished()).andReturn(true);
		EasyMock.expect(fourthRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(fourthRequest.getResponseSizeInBytes()).andReturn(100L);
		
		Request fifthRequest = EasyMock.createStrictMock(Request.class);
		fifthRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(fifthRequest.getTotalToProcess()).andReturn(100L);
		fifthRequest.update(100L);
		EasyMock.expect(fifthRequest.isFinished()).andReturn(true);
		EasyMock.expect(fifthRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(fifthRequest.getResponseSizeInBytes()).andReturn(100L);
		
		//Request that is rejected
		Request sixthRequest = EasyMock.createStrictMock(Request.class);

		JEEventScheduler scheduler = new JEEventScheduler();
		
		Capture<JEEvent> captured = new Capture<JEEvent>();
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(scheduler.registerHandler(loadBalancer));
		loadBalancer.handleEvent(EasyMock.capture(captured));
		
		loadBalancer.reportRequestFinished(firstRequest);
		loadBalancer.reportRequestFinished(secondRequest);
		loadBalancer.reportRequestFinished(thirdRequest);
		loadBalancer.reportRequestFinished(fourthRequest);
		loadBalancer.reportRequestFinished(fifthRequest);

		PowerMock.replayAll(config, loadBalancer, firstRequest, secondRequest, thirdRequest, fourthRequest, fifthRequest, sixthRequest);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		machine.sendRequest(thirdRequest);
		machine.sendRequest(fourthRequest);
		machine.sendRequest(fifthRequest);
		machine.sendRequest(sixthRequest);

		Queue<Request> queue = machine.getProcessorQueue();
		assertEquals(2, queue.size());
		assertEquals(secondRequest, queue.poll());
		assertEquals(thirdRequest, queue.poll());
		assertNull(queue.poll());
		
		queue = machine.getExecutingQueue();
		assertEquals(1, queue.size());
		assertEquals(firstRequest, queue.poll());
		assertNull(queue.poll());
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.REQUESTQUEUED, event.getType());
		assertEquals(sixthRequest, event.getValue()[0]);
		
		assertTrue(machine.getProcessorQueue().isEmpty());
		
		PowerMock.verifyAll();
	}
	
	/**
	 * With one core and a number of threads equals to six, three requests are
	 * are being processed simultaneously, two requests stay waiting in the backlog and one request is rejected
	 */
	@Test
	public void testSendMoreRequestThanCanRunAndWaitAndMultiCores(){
		Configuration config = mockConfiguration(3, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request firstRequest = EasyMock.createStrictMock(Request.class);
		firstRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(50L);
		firstRequest.update(50L);
		EasyMock.expect(firstRequest.isFinished()).andReturn(true);
		EasyMock.expect(firstRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(firstRequest.getResponseSizeInBytes()).andReturn(100L);
		
		Request secondRequest = EasyMock.createStrictMock(Request.class);
		secondRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(100L);
		secondRequest.update(100L);
		EasyMock.expect(secondRequest.isFinished()).andReturn(true);
		EasyMock.expect(secondRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(secondRequest.getResponseSizeInBytes()).andReturn(100L);
		
		Request thirdRequest = EasyMock.createStrictMock(Request.class);
		thirdRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(thirdRequest.getTotalToProcess()).andReturn(100L);
		thirdRequest.update(100L);
		EasyMock.expect(thirdRequest.isFinished()).andReturn(true);
		EasyMock.expect(thirdRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(thirdRequest.getResponseSizeInBytes()).andReturn(100L);
		
		//Requests initially assigned to backlog
		Request fourthRequest = EasyMock.createStrictMock(Request.class);
		fourthRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(fourthRequest.getTotalToProcess()).andReturn(100L);
		fourthRequest.update(100L);
		EasyMock.expect(fourthRequest.isFinished()).andReturn(true);
		EasyMock.expect(fourthRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(fourthRequest.getResponseSizeInBytes()).andReturn(100L);
		
		Request fifthRequest = EasyMock.createStrictMock(Request.class);
		fifthRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(fifthRequest.getTotalToProcess()).andReturn(100L);
		fifthRequest.update(100L);
		EasyMock.expect(fifthRequest.isFinished()).andReturn(true);
		EasyMock.expect(fifthRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(fifthRequest.getResponseSizeInBytes()).andReturn(100L);
		
		//Request that is rejected
		Request sixthRequest = EasyMock.createStrictMock(Request.class);

		JEEventScheduler scheduler = new JEEventScheduler();
		
		Capture<JEEvent> captured = new Capture<JEEvent>();
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(scheduler.registerHandler(loadBalancer));
		loadBalancer.handleEvent(EasyMock.capture(captured));
		
		loadBalancer.reportRequestFinished(firstRequest);
		loadBalancer.reportRequestFinished(secondRequest);
		loadBalancer.reportRequestFinished(thirdRequest);
		loadBalancer.reportRequestFinished(fourthRequest);
		loadBalancer.reportRequestFinished(fifthRequest);

		PowerMock.replayAll(config, loadBalancer, firstRequest, secondRequest, thirdRequest, fourthRequest, fifthRequest, sixthRequest);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		machine.sendRequest(thirdRequest);
		machine.sendRequest(fourthRequest);
		machine.sendRequest(fifthRequest);
		machine.sendRequest(sixthRequest);

		Queue<Request> queue = machine.getProcessorQueue();
		assertEquals(0, queue.size());
		
		queue = machine.getExecutingQueue();
		assertEquals(3, queue.size());
		assertEquals(firstRequest, queue.poll());
		assertEquals(secondRequest, queue.poll());
		assertEquals(thirdRequest, queue.poll());
		assertNull(queue.poll());
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.REQUESTQUEUED, event.getType());
		assertEquals(sixthRequest, event.getValue()[0]);
		
		assertTrue(machine.getProcessorQueue().isEmpty());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testComputeUtilisationOfEmptyMachine(){
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.replay(loadBalancer);
		
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		assertEquals(Double.NaN, machine.computeUtilisation(scheduler.now().timeMilliSeconds), 0.0001);
		assertEquals(0, machine.computeUtilisation(scheduler.now().timeMilliSeconds + 300000), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	/**
	 * As the request finish event has not happened, the time that is an attribute of computeUtilisation
	 * is used as the computing time
	 */
	@Test
	public void testComputeUtilisationOfMachineWithRunningRequestAndOneCore(){
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(MultiCoreTimeSharedMachine.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(new JETime(100l)).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(150L);
		request.update(100);
		EasyMock.expect(request.isFinished()).andReturn(false);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replayAll(request, loadBalancer, config, scheduler);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		assertEquals(Double.NaN, machine.computeUtilisation(0), 0.0001);
		assertEquals(1, machine.computeUtilisation(50), 0.0001);
		
		//Simulating a preemption event
		machine.handleEvent(new JEEvent(JEEventType.PREEMPTION, machine, new JETime(100), 100l, request));
		
		assertEquals(1, machine.computeUtilisation(100), 0.0001);
		assertEquals(1, machine.computeUtilisation(150), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testComputeUtilisationOfMachineWithRunningRequestAndOneCore2(){
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(MultiCoreTimeSharedMachine.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(new JETime(100l)).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(new JETime(150l));
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(EasyMock.isA(Request.class));
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(150L);
		request.update(100);
		EasyMock.expect(request.isFinished()).andReturn(false);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(0l);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(0l);
		
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replayAll(request, loadBalancer, config, scheduler);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		//Simulating a preemption event
		machine.handleEvent(new JEEvent(JEEventType.PREEMPTION, machine, new JETime(100), 100l, request));
		
		//As a compute utilisation is performed, the next compute utilisation will start from this time!
		assertEquals(1, machine.computeUtilisation(100), 0.0001);
		
		machine.handleEvent(new JEEvent(JEEventType.PREEMPTION, machine, new JETime(150), 50l, request));
		assertEquals(0.25, machine.computeUtilisation(300), 0.0001);
		
		PowerMock.verifyAll();
	}

	/**
	 * As the request finish event has not happened, the time that is an attribute of computeUtilisation
	 * is used as the computing time
	 */
	@Test
	public void testComputeUtilisationOfMachineWithRunningRequestAndDualCore(){
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(MultiCoreTimeSharedMachine.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(new JETime(100l)).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(150L);
		request.update(100);
		EasyMock.expect(request.isFinished()).andReturn(false);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		
		Configuration config = mockConfiguration(2, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replayAll(request, loadBalancer, config, scheduler);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		assertEquals(Double.NaN, machine.computeUtilisation(0), 0.0001);
		assertEquals(0.5, machine.computeUtilisation(50), 0.0001);
		
		//Simulating a preemption event
		machine.handleEvent(new JEEvent(JEEventType.PREEMPTION, machine, new JETime(100), 100l, request));
		
		assertEquals(0.5, machine.computeUtilisation(100), 0.0001);
		assertEquals(0.5, machine.computeUtilisation(150), 0.0001);
		
		PowerMock.verifyAll();
	}

	/**
	 * As the request finish event has not happened, the time that is an attribute of computeUtilisation
	 * is used as the computing time
	 */
	@Test
	public void testComputeUtilisationOfMachineWithRunningRequestAndQuadCore(){
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		
		Configuration config = mockConfiguration(4, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replayAll(request, loadBalancer, config);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		assertEquals(Double.NaN, machine.computeUtilisation(scheduler.now().timeMilliSeconds), 0.0001);
		assertEquals(0.25, machine.computeUtilisation(scheduler.now().timeMilliSeconds + 50), 0.0001);
		
		PowerMock.verifyAll();
	}

	@Test
	public void testComputeUtilisationOfMachineAsRequestFinishesAndOneCore(){
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replayAll(request, loadBalancer, config);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		assertEquals(1, machine.computeUtilisation(50), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(99), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testComputeUtilisationOfMachineAsRequestFinishesAndQuadCore(){
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		Configuration config = mockConfiguration(4, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replayAll(request, loadBalancer, config);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);

		scheduler.start();
		
		assertEquals(0.25, machine.computeUtilisation(50), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(299), 0.0001);
		
		PowerMock.verifyAll();
	}

	@Test
	public void testComputeUtilisationOfMachineAfterRequestFinishesAndOneCore(){
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replayAll(request, loadBalancer, config);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		//Computing utilisation
		assertEquals(0.5, machine.computeUtilisation(100), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(150), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testComputeUtilisationOfMachineAfterRequestFinishesAndQuadCore(){
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		Configuration config = mockConfiguration(4, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replayAll(request, loadBalancer, config);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		//Computing utilisation
		assertEquals(0.125, machine.computeUtilisation(100), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(150), 0.0001);
		
		PowerMock.verifyAll();
	}

	@Test
	public void testComputeUtilisationOfMachineAfterRequestFinishesAndOneCore2(){
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replayAll(request, loadBalancer, config);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		assertEquals(1.0/3, machine.computeUtilisation(150), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(160), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testComputeUtilisationOfMachineAfterRequestFinishesAndDualCore2(){
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		Configuration config = mockConfiguration(2, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		PowerMock.replayAll(request, loadBalancer, config);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		assertEquals(1.0/6, machine.computeUtilisation(150), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(151), 0.0001);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testIsBusyWithRequests(){
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		
		PowerMock.replayAll(config, loadBalancer, request);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		
		//Verifying if machine is busy
		assertFalse(machine.isBusy());
		
		machine.sendRequest(request);
		
		PowerMock.verifyAll();
		
		//Verifying if machine is busy
		assertTrue(machine.isBusy());
	}
	
	@Test
	public void testIsBusyWithoutRequests(){
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		PowerMock.replayAll(config, loadBalancer);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		
		PowerMock.verifyAll();
		
		//Verifying if machine is busy
		assertFalse(machine.isBusy());
	}
	
	@Test
	public void testMachineIsBusyAfterRequestFinishes(){
		Configuration config = mockConfiguration(1, DEFAULT_MAX_NUM_OF_THREADS, DEFAULT_BACKLOG_SIZE);
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		PowerMock.replayAll(config, loadBalancer, request);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		
		machine.sendRequest(request);
		assertTrue(machine.isBusy());//Verifying if machine is busy
		
		Queue<Request> queue = machine.getExecutingQueue();
		assertNotNull(queue);
		assertEquals(1, queue.size());
		
		scheduler.start();
		
		PowerMock.verifyAll();
		
		assertFalse(machine.isBusy());//Verifying if machine is busy
	}
}
