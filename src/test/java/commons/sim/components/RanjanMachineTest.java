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
public class RanjanMachineTest {
	
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
		
		Configuration config = mockConfiguration();
		
		PowerMock.replayAll(config);
		
		Machine machine = new RanjanMachine(scheduler, descriptor, null);
		assertEquals(descriptor, machine.getDescriptor());
		assertNull(machine.getLoadBalancer());
		assertNotNull(machine.getProcessorQueue());
		assertTrue(machine.getProcessorQueue().isEmpty());
		
		PowerMock.verifyAll();
	}

	private static Configuration mockConfiguration() {
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLong(RANJAN_HEURISTIC_NUMBER_OF_TOKENS)).andReturn(DEFAULT_MAX_NUM_OF_THREADS);
		EasyMock.expect(config.getLong(RANJAN_HEURISTIC_BACKLOG_SIZE)).andReturn(DEFAULT_BACKLOG_SIZE);
		return config;
	}
	
	/**
	 * This method verifies that a single request is correctly added to a machine, without already
	 * processing requests, since the limit of threads is not reached
	 * @throws Exception 
	 */
	@Test
	public void sendBigRequestWithEmptyServer(){
		
		Configuration config = mockConfiguration();
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(5000L);
		
		PowerMock.replayAll(config);
		
		Machine machine = PowerMock.createStrictPartialMock(RanjanMachine.class, new String[]{"handleEvent"}, scheduler, descriptor, loadBalancer);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		machine.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		
		PowerMock.replay(loadBalancer, request, machine);
		
		machine.sendRequest(request);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertFalse(queue.isEmpty());
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.PREEMPTION, event.getType());
		assertEquals(new JETime(100L), event.getScheduledTime());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testSendSmallRequestWithEmptyMachine(){

		Configuration config = mockConfiguration();
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		
		PowerMock.replayAll(config);
		
		Machine machine = PowerMock.createStrictPartialMock(RanjanMachine.class, new String[]{"handleEvent"}, scheduler, descriptor, loadBalancer);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		machine.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		
		EasyMock.replay(loadBalancer, request, machine);
		
		machine.sendRequest(request);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertFalse(queue.isEmpty());
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.PREEMPTION, event.getType());
		assertEquals(new JETime(50L), event.getScheduledTime());
		
		PowerMock.verifyAll();
	}

	@Test
	public void testSendTwoRequestWithEmptyMachine(){
		Configuration config = mockConfiguration();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request firstRequest = EasyMock.createStrictMock(Request.class);
		firstRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(5000L);
		
		Request secondRequest = EasyMock.createStrictMock(Request.class);
		secondRequest.assignTo(MachineType.SMALL);
		
		PowerMock.replayAll(config, loadBalancer, firstRequest, secondRequest);
		
		Machine machine = new RanjanMachine(new JEEventScheduler(), descriptor, loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertFalse(queue.isEmpty());
		assertEquals(firstRequest, queue.poll());
		assertEquals(secondRequest, queue.poll());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testShutdownWithEmptyMachine(){
		Configuration config = mockConfiguration();
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(scheduler.registerHandler(loadBalancer));
		Capture<JEEvent> captured = new Capture<JEEvent>();
		loadBalancer.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		
		PowerMock.replayAll(config, loadBalancer);
		
		Machine machine = new RanjanMachine(scheduler, descriptor, loadBalancer);
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
		Configuration config = mockConfiguration();
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

		RanjanMachine machine = new RanjanMachine(scheduler, descriptor, loadBalancer);
		
		machine.sendRequest(request);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertFalse(queue.isEmpty());
		machine.shutdownOnFinish();
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, event.getType());
		assertEquals(machine.getDescriptor(), event.getValue()[0]);
		
		PowerMock.verifyAll();
	}

	@Test
	public void testSendMoreRequestThanCanRun(){
		Configuration config = mockConfiguration();
		
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
		
		Machine machine = new RanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		machine.sendRequest(thirdRequest);
		machine.sendRequest(fourthRequest);

		Queue<Request> queue = machine.getProcessorQueue();
		assertEquals(firstRequest, queue.poll());
		assertEquals(secondRequest, queue.poll());
		assertEquals(thirdRequest, queue.poll());
		assertNull(queue.poll());
		
		scheduler.start();
		
		assertTrue(machine.getProcessorQueue().isEmpty());
		
		PowerMock.verify();
	}

	@Test
	public void testSendMoreRequestThanCanRunAndWait(){
		Configuration config = mockConfiguration();
		
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
		
		Machine machine = new RanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		machine.sendRequest(thirdRequest);
		machine.sendRequest(fourthRequest);
		machine.sendRequest(fifthRequest);
		machine.sendRequest(sixthRequest);

		Queue<Request> queue = machine.getProcessorQueue();
		assertEquals(firstRequest, queue.poll());
		assertEquals(secondRequest, queue.poll());
		assertEquals(thirdRequest, queue.poll());
		assertNull(queue.poll());
		
		scheduler.start();
		
		assertEquals(sixthRequest, captured.getValue().getValue()[0]);
		
		assertTrue(machine.getProcessorQueue().isEmpty());
		
		PowerMock.verifyAll();
	}
	
	/**
	 * This method verifies that the utilization computed at each machine depends on the maximum number
	 * of threads that can be executed in a machine, and the current number of threads executing. In
	 * this test, the maximum number of threads and the current number of threads executing are equal.
	 * @throws Exception
	 */
	@Test
	public void testComputeUtilisationWithThreadLimitReached() throws Exception{
		long localMaxNumberOfThreads = 1l;
		long backlogSize = 1l;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLong(RANJAN_HEURISTIC_NUMBER_OF_TOKENS)).andReturn(localMaxNumberOfThreads);
		EasyMock.expect(config.getLong(RANJAN_HEURISTIC_BACKLOG_SIZE)).andReturn(backlogSize);
		
		JEEventScheduler scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(3);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(100)).times(3);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		firstRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(60000L);
		firstRequest.update(100L);
		EasyMock.expect(firstRequest.isFinished()).andReturn(false);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(59900L);

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		secondRequest.assignTo(MachineType.SMALL);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		PowerMock.replayAll(scheduler, firstRequest, secondRequest, config, loadBalancer);
		
		Machine machine = new RanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		//Verifying utilization with one request in the queue, requests in backlog
		assertEquals(1, machine.computeUtilisation(10l), 0.0);
		
		//Simulating that end event has arrived
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.expect(event.getType()).andReturn(JEEventType.PREEMPTION);
		EasyMock.expect(event.getValue()).andReturn(new Long[]{100l});
		EasyMock.replay(event);

		machine.handleEvent(event);
		
		PowerMock.verifyAll();
		
		//Verifying queue of requests that are being processed
		assertEquals(1.0, machine.computeUtilisation(20l), 0.0);
	}
	
	/**
	 * This method verifies that the utilization computed at each machine depends on the maximum number
	 * of threads that can be executed in a machine, and the current number of threads executing. In
	 * this test, the maximum number of threads and the current number of threads executing are different.
	 * @throws Exception
	 */
	@Test
	public void testComputeUtilisationWithThreadLimitNotReached() throws Exception{
		Configuration config = mockConfiguration();
		
		JEEventScheduler scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0L)).times(3);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(60000L);
		PowerMock.replayAll(request, scheduler, config);
		
		Machine machine = new RanjanMachine(scheduler, descriptor, null);
		machine.sendRequest(request);
		
		//Verifying utilization
		assertEquals(1.0, machine.computeUtilisation(10l), 0.0);
		
		//Sending another request in the same time
		Request request2 = EasyMock.createStrictMock(Request.class);
		request2.assignTo(MachineType.SMALL);
		EasyMock.replay(request2);
		
		machine.sendRequest(request2);
		
		//Verifying utilization
		assertEquals(1.0, machine.computeUtilisation(100l), 0.0);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testIsBusyWithRequests(){
		Configuration config = mockConfiguration();
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		
		PowerMock.replayAll(config, loadBalancer, request);
		
		RanjanMachine machine = new RanjanMachine(scheduler, descriptor, loadBalancer);
		
		machine.sendRequest(request);
		
		PowerMock.verifyAll();
		
		//Verifying if machine is busy
		assertTrue(machine.isBusy());
	}
	
	@Test
	public void testIsBusyWithoutRequests(){
		Configuration config = mockConfiguration();
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		PowerMock.replayAll(config, loadBalancer);
		
		RanjanMachine machine = new RanjanMachine(scheduler, descriptor, loadBalancer);
		
		PowerMock.verifyAll();
		
		//Verifying if machine is busy
		assertFalse(machine.isBusy());
	}
	
	@Test
	public void testMachineIsBusyAfterRequestFinishes(){
		Configuration config = mockConfiguration();
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
		
		RanjanMachine machine = new RanjanMachine(scheduler, descriptor, loadBalancer);
		
		machine.sendRequest(request);
		assertTrue(machine.isBusy());//Verifying if machine is busy
		
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertFalse(queue.isEmpty());
		
		scheduler.start();
		
		PowerMock.verifyAll();
		
		assertFalse(machine.isBusy());//Verifying if machine is busy
	}
}
