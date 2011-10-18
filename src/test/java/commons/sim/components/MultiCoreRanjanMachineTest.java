package commons.sim.components;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Queue;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;

/**
 * Backlog size = 2
 * Number of Threads = 3
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class MultiCoreRanjanMachineTest extends ValidConfigurationTest {
	
	private MachineDescriptor descriptor;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullRanjanConfiguration();
		this.descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
	}

	@Test
	public void testConstructor() {
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(JEEventScheduler.getInstance(), descriptor, null);
		assertEquals(descriptor, machine.getDescriptor());
		assertNull(machine.getLoadBalancer());
		assertNotNull(machine.getProcessorQueue());
		assertTrue(machine.getProcessorQueue().isEmpty());
	}

	@Test
	public void testSmallRequestExecutionWithSingleCoreMachine(){
	
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Capture<Request> captured = new Capture<Request>();
		loadBalancer.reportRequestFinished(EasyMock.capture(captured));
		EasyMock.replay(loadBalancer);
		
		Request request = new Request(0, 0, 0, 0, 10, 100, new long[]{50});
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(JEEventScheduler.getInstance(), descriptor, loadBalancer);
		
		machine.sendRequest(request);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertTrue(queue.isEmpty());
		
		JEEventScheduler.getInstance().start();
		
		assertEquals(request, captured.getValue());
		assertEquals(50, JEEventScheduler.getInstance().now());
	}

	/**
	 * This method verifies that a single request is correctly added to a machine, without already
	 * processing requests, since the limit of threads is not reached
	 * @throws Exception 
	 */
	@Test
	public void testBigRequestExecutionWithSingleCoreMachine(){
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Capture<Request> captured = new Capture<Request>();
		loadBalancer.reportRequestFinished(EasyMock.capture(captured));
		EasyMock.replay(loadBalancer);
		
		Request request = new Request(0, 0, 0, 0, 10, 100, new long[]{2500});
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(JEEventScheduler.getInstance(), descriptor, loadBalancer);
		
		machine.sendRequest(request);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertTrue(queue.isEmpty());
		
		JEEventScheduler.getInstance().start();
		
		assertEquals(request, captured.getValue());
		assertEquals(2500, JEEventScheduler.getInstance().now());
	}
	
	@Test
	public void testSendRequestToBacklogSingleCoreMachine() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		Request request = new Request(0, 0, 0, 0, 10, 100, new long[]{5000});
		Request request2 = new Request(0, 0, 0, 0, 10, 100, new long[]{5000});
		Request request3 = new Request(0, 0, 0, 0, 10, 100, new long[]{5000});
		Request request4 = new Request(0, 0, 0, 0, 10, 100, new long[]{5000});
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(request);
		loadBalancer.reportRequestFinished(request2);
		loadBalancer.reportRequestFinished(request3);
		loadBalancer.reportRequestFinished(request4);
		EasyMock.replay(loadBalancer);
		
		Queue<Request> queue = EasyMock.createStrictMock(Queue.class);
		Capture<Request> captured = new Capture<Request>();
		EasyMock.expect(queue.size()).andReturn(0);
		EasyMock.expect(queue.add(EasyMock.capture(captured))).andReturn(true);
		EasyMock.expect(queue.isEmpty()).andReturn(false);
		EasyMock.expect(queue.poll()).andReturn(request4);
		EasyMock.expect(queue.isEmpty()).andReturn(true).times(3);
		EasyMock.replay(queue);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), loadBalancer);
		
		Field declaredField = MultiCoreRanjanMachine.class.getDeclaredField("backlog");
		declaredField.setAccessible(true);
		declaredField.set(machine, queue);
		
		machine.sendRequest(request);
		machine.sendRequest(request2);
		machine.sendRequest(request3);
		machine.sendRequest(request4);
		
		Queue<Request> processorQueue = machine.getProcessorQueue();
		assertEquals(2, processorQueue.size());
		assertEquals(request2, processorQueue.poll());
		assertEquals(request3, processorQueue.poll());
		
		assertEquals(request4, captured.getValue());
		
		JEEventScheduler.getInstance().start();
		
		assertEquals(20000, JEEventScheduler.getInstance().now());
		
		EasyMock.verify(queue);
	}
	
	@Test
	public void testSendRequestToBacklogMultiCoreMachine() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		Request request = new Request(0, 0, 0, 0, 10, 100, new long[]{5000, 5000, 5000, 5000});
		Request request2 = new Request(0, 0, 0, 0, 10, 100, new long[]{5000, 5000, 5000, 5000});
		Request request3 = new Request(0, 0, 0, 0, 10, 100, new long[]{5000, 5000, 5000, 5000});
		Request request4 = new Request(0, 0, 0, 0, 10, 100, new long[]{5000, 5000, 5000, 5000});
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(request);
		loadBalancer.reportRequestFinished(request2);
		loadBalancer.reportRequestFinished(request3);
		loadBalancer.reportRequestFinished(request4);
		EasyMock.replay(loadBalancer);
		
		Queue<Request> queue = EasyMock.createStrictMock(Queue.class);
		Capture<Request> captured = new Capture<Request>();
		EasyMock.expect(queue.size()).andReturn(0);
		EasyMock.expect(queue.add(EasyMock.capture(captured))).andReturn(true);
		EasyMock.expect(queue.isEmpty()).andReturn(false);
		EasyMock.expect(queue.poll()).andReturn(request4);
		EasyMock.expect(queue.isEmpty()).andReturn(true).times(3);
		EasyMock.replay(queue);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.M1_XLARGE, 0), loadBalancer);
		
		Field declaredField = MultiCoreRanjanMachine.class.getDeclaredField("backlog");
		declaredField.setAccessible(true);
		declaredField.set(machine, queue);
		
		machine.sendRequest(request);
		machine.sendRequest(request2);
		machine.sendRequest(request3);
		machine.sendRequest(request4);
		
		Queue<Request> processorQueue = machine.getProcessorQueue();
		assertTrue(processorQueue.isEmpty());
		
		assertEquals(request4, captured.getValue());
		
		JEEventScheduler.getInstance().start();
		
		assertEquals(10000, JEEventScheduler.getInstance().now());

		EasyMock.verify(queue);
	}
	
	@Test
	public void testShutdownWithEmptyMachine(){
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(scheduler.registerHandler(loadBalancer));
		Capture<JEEvent> captured = new Capture<JEEvent>();
		loadBalancer.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.shutdownOnFinish();
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, event.getType());
		assertEquals(machine.getDescriptor(), event.getValue()[0]);
		
		assertEquals(0, JEEventScheduler.getInstance().now());
		
		EasyMock.verify(loadBalancer);
	}
	
	@Test
	public void testShutdownWithNonEmptyMachine(){
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		Request request = new Request(0, 0, 0, 0, 10, 100, new long[]{5000});
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(request);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(scheduler.registerHandler(loadBalancer));
		Capture<JEEvent> captured = new Capture<JEEvent>();
		loadBalancer.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		EasyMock.replay(loadBalancer);

		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		machine.shutdownOnFinish();
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, event.getType());
		assertEquals(machine.getDescriptor(), event.getValue()[0]);
		
		assertEquals(5000, JEEventScheduler.getInstance().now());
		
		EasyMock.verify(loadBalancer);
	}

	/**
	 * With one core and a number of threads equals to three, three requests stay
	 * in processor processing queue and one is stored in the backlog
	 */
	@Test
	public void testHandlePreemptionWithMoreRequestThanCanRunWithOneCore(){
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request firstRequest = EasyMock.createStrictMock(Request.class);
		firstRequest.assignTo(MachineType.M1_SMALL);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(50L);
		firstRequest.update(50L);
		EasyMock.expect(firstRequest.isFinished()).andReturn(true);
		EasyMock.expect(firstRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(firstRequest.getResponseSizeInBytes()).andReturn(100L);

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		secondRequest.assignTo(MachineType.M1_SMALL);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(200L);
		secondRequest.update(100L);
		EasyMock.expect(secondRequest.isFinished()).andReturn(false);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(100L);
		secondRequest.update(100L);
		EasyMock.expect(secondRequest.isFinished()).andReturn(true);
		EasyMock.expect(secondRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(secondRequest.getResponseSizeInBytes()).andReturn(100L);

		Request thirdRequest = EasyMock.createStrictMock(Request.class);
		thirdRequest.assignTo(MachineType.M1_SMALL);
		EasyMock.expect(thirdRequest.getTotalToProcess()).andReturn(100L);
		thirdRequest.update(100L);
		EasyMock.expect(thirdRequest.isFinished()).andReturn(true);
		EasyMock.expect(thirdRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(thirdRequest.getResponseSizeInBytes()).andReturn(100L);
		
		//Request that is initially assigned to backlog
		Request fourthRequest = EasyMock.createStrictMock(Request.class);
		fourthRequest.assignTo(MachineType.M1_SMALL);
		EasyMock.expect(fourthRequest.getTotalToProcess()).andReturn(100L);
		fourthRequest.update(100L);
		EasyMock.expect(fourthRequest.isFinished()).andReturn(true);
		EasyMock.expect(fourthRequest.getRequestSizeInBytes()).andReturn(100L);
		EasyMock.expect(fourthRequest.getResponseSizeInBytes()).andReturn(100L);

		loadBalancer.reportRequestFinished(firstRequest);
		loadBalancer.reportRequestFinished(thirdRequest);
		loadBalancer.reportRequestFinished(fourthRequest);
		loadBalancer.reportRequestFinished(secondRequest);
		
		EasyMock.replay(loadBalancer, firstRequest, secondRequest, thirdRequest, fourthRequest);
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		machine.sendRequest(thirdRequest);
		machine.sendRequest(fourthRequest);

		Queue<Request> queue = machine.getProcessorQueue();
		assertEquals(2, queue.size());
		assertEquals(secondRequest, queue.poll());
		assertEquals(thirdRequest, queue.poll());
		assertNull(queue.poll());
		
		scheduler.start();
		
		assertTrue(machine.getProcessorQueue().isEmpty());

		EasyMock.verify(loadBalancer, firstRequest, secondRequest, thirdRequest, fourthRequest);
	}
	
	/**
	 * With one core and a number of threads equals to six, three requests are
	 * in the processor queue, two requests stay waiting in the backlog and one request is rejected
	 */
	@Test
	public void testReportLostRequestWithSingleCoreMachine(){

		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
	
		Request firstRequest = new Request(0, 0, 0, 0, 100, 100, new long[]{50});
		Request secondRequest = new Request(1, 0, 0, 0, 100, 100, new long[]{100});
		Request thirdRequest = new Request(2, 0, 0, 0, 100, 100, new long[]{100});
		Request fourthRequest = new Request(3, 0, 0, 0, 100, 100, new long[]{100});
		Request fifthRequest = new Request(4, 0, 0, 0, 100, 100, new long[]{100});
		Request sixthRequest = new Request(5, 0, 0, 0, 100, 100, new long[]{50});

		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(JEEventScheduler.getInstance().registerHandler(loadBalancer));
		
		Capture<JEEvent> captured = new Capture<JEEvent>();
		loadBalancer.handleEvent(EasyMock.capture(captured));
		
		loadBalancer.reportRequestFinished(firstRequest);
		loadBalancer.reportRequestFinished(secondRequest);
		loadBalancer.reportRequestFinished(thirdRequest);
		loadBalancer.reportRequestFinished(fourthRequest);
		loadBalancer.reportRequestFinished(fifthRequest);

		EasyMock.replay(loadBalancer);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), loadBalancer);
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
		
		JEEventScheduler.getInstance().start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.REQUESTQUEUED, event.getType());
		assertEquals(sixthRequest, event.getValue()[0]);
		
		assertTrue(machine.getProcessorQueue().isEmpty());
		
		assertEquals(450, JEEventScheduler.getInstance().now());
		
		EasyMock.verify(loadBalancer);
	}
	
	@Test
	public void testReportLostRequestWithMultiCoreMachine(){
	
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
	
		Request firstRequest = new Request(0, 0, 0, 0, 100, 100, new long[]{50, 50, 50, 50});
		Request secondRequest = new Request(1, 0, 0, 0, 100, 100, new long[]{100, 100, 100, 100});
		Request thirdRequest = new Request(2, 0, 0, 0, 100, 100, new long[]{100, 100, 100, 100});
		Request fourthRequest = new Request(3, 0, 0, 0, 100, 100, new long[]{100, 100, 100, 100});
		Request fifthRequest = new Request(4, 0, 0, 0, 100, 100, new long[]{100, 100, 100, 100});
		Request sixthRequest = new Request(5, 0, 0, 0, 100, 100, new long[]{50, 50, 50, 50});
	
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(JEEventScheduler.getInstance().registerHandler(loadBalancer));
		
		Capture<JEEvent> captured = new Capture<JEEvent>();
		loadBalancer.handleEvent(EasyMock.capture(captured));
		
		loadBalancer.reportRequestFinished(firstRequest);
		loadBalancer.reportRequestFinished(secondRequest);
		loadBalancer.reportRequestFinished(thirdRequest);
		loadBalancer.reportRequestFinished(fourthRequest);
		loadBalancer.reportRequestFinished(fifthRequest);
	
		EasyMock.replay(loadBalancer);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.M1_XLARGE, 0), loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		machine.sendRequest(thirdRequest);
		machine.sendRequest(fourthRequest);
		machine.sendRequest(fifthRequest);
		machine.sendRequest(sixthRequest);
		
		Queue<Request> queue = machine.getProcessorQueue();
		assertTrue(queue.isEmpty());
		
		JEEventScheduler.getInstance().start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.REQUESTQUEUED, event.getType());
		assertEquals(sixthRequest, event.getValue()[0]);
		
		assertTrue(machine.getProcessorQueue().isEmpty());
		
		assertEquals(200, JEEventScheduler.getInstance().now());
		
		EasyMock.verify(loadBalancer);
	}

	@Test
	public void testComputeUtilisationOfEmptyMachine(){
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.replay(loadBalancer);
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		assertEquals(Double.NaN, machine.computeUtilisation(scheduler.now()), 0.0001);
		assertEquals(0, machine.computeUtilisation(scheduler.now() + 300000), 0.0001);
		
		EasyMock.verify(loadBalancer);
	}
	
	/**
	 * As the request finish event has not happened, the time that is an attribute of computeUtilisation
	 * is used as the computing time
	 */
	@Test
	public void testComputeUtilisationOfMachineWithRunningRequestAndOneCore(){
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(MultiCoreTimeSharedMachine.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(100l).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.M1_SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(150L);
		request.update(100);
		EasyMock.expect(request.isFinished()).andReturn(false);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		
		EasyMock.replay(request, loadBalancer, scheduler);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		assertEquals(Double.NaN, machine.computeUtilisation(0), 0.0001);
		assertEquals(1, machine.computeUtilisation(50), 0.0001);
		
		//Simulating a preemption event
		machine.handleEvent(new JEEvent(JEEventType.PREEMPTION, machine, 100l, 100l, request));
		
		assertEquals(1, machine.computeUtilisation(100), 0.0001);
		assertEquals(1, machine.computeUtilisation(150), 0.0001);
		
		EasyMock.verify(request, loadBalancer, scheduler);
	}
	
	@Test
	public void testComputeUtilisationOfMachineWithRunningRequestAndOneCore2(){
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(MultiCoreTimeSharedMachine.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(100l).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(150l);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(EasyMock.isA(Request.class));
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.M1_SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(150L);
		request.update(100);
		EasyMock.expect(request.isFinished()).andReturn(false);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(0l);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(0l);
		
		EasyMock.replay(request, loadBalancer, scheduler);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		//Simulating a preemption event
		machine.handleEvent(new JEEvent(JEEventType.PREEMPTION, machine, 100l, 100l, request));
		
		//As a compute utilisation is performed, the next compute utilisation will start from this time!
		assertEquals(1, machine.computeUtilisation(100), 0.0001);
		
		machine.handleEvent(new JEEvent(JEEventType.PREEMPTION, machine, 150l, 50l, request));
		assertEquals(0.25, machine.computeUtilisation(300), 0.0001);
		
		EasyMock.verify(request, loadBalancer, scheduler);
	}

	/**
	 * As the request finish event has not happened, the time that is an attribute of computeUtilisation
	 * is used as the computing time
	 */
	@Test
	public void testComputeUtilisationOfMachineWithRunningRequestAndDualCore(){
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(MultiCoreTimeSharedMachine.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(100l).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.M1_LARGE);
		EasyMock.expect(request.getTotalToProcess()).andReturn(150L);
		request.update(100);
		EasyMock.expect(request.isFinished()).andReturn(false);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		
		EasyMock.replay(request, loadBalancer, scheduler);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, new MachineDescriptor(1, false, MachineType.M1_LARGE, 0), loadBalancer);
		machine.sendRequest(request);
		
		assertEquals(Double.NaN, machine.computeUtilisation(0), 0.0001);
		assertEquals(0.5, machine.computeUtilisation(50), 0.0001);
		
		//Simulating a preemption event
		machine.handleEvent(new JEEvent(JEEventType.PREEMPTION, machine, 100l, 100l, request));
		
		assertEquals(0.5, machine.computeUtilisation(100), 0.0001);
		assertEquals(0.5, machine.computeUtilisation(150), 0.0001);
		
		EasyMock.verify(request, loadBalancer, scheduler);
	}

	/**
	 * As the request finish event has not happened, the time that is an attribute of computeUtilisation
	 * is used as the computing time
	 */
	@Test
	public void testComputeUtilisationOfMachineWithRunningRequestAndQuadCore(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.M1_XLARGE);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		
		EasyMock.replay(request, loadBalancer);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, new MachineDescriptor(1, false, MachineType.M1_XLARGE, 0), loadBalancer);
		machine.sendRequest(request);
		
		assertEquals(Double.NaN, machine.computeUtilisation(scheduler.now()), 0.0001);
		assertEquals(0.25, machine.computeUtilisation(scheduler.now() + 50), 0.0001);
		
		EasyMock.verify(request, loadBalancer);
	}

	@Test
	public void testComputeUtilisationOfMachineAsRequestFinishesAndOneCore(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.M1_SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(request, loadBalancer);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		assertEquals(1, machine.computeUtilisation(50), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(99), 0.0001);
		
		EasyMock.verify(request, loadBalancer);
	}
	
	@Test
	public void testComputeUtilisationOfMachineAsRequestFinishesAndQuadCore(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.M1_XLARGE);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(request, loadBalancer);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, new MachineDescriptor(1, false, MachineType.M1_XLARGE, 0), loadBalancer);
		machine.sendRequest(request);

		scheduler.start();
		
		assertEquals(0.25, machine.computeUtilisation(50), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(299), 0.0001);
		
		EasyMock.verify(request, loadBalancer);
	}

	@Test
	public void testComputeUtilisationOfMachineAfterRequestFinishesAndOneCore(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.M1_SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(request, loadBalancer);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		//Computing utilisation
		assertEquals(0.5, machine.computeUtilisation(100), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(150), 0.0001);
		
		EasyMock.verify(request, loadBalancer);
	}
	
	@Test
	public void testComputeUtilisationOfMachineAfterRequestFinishesAndQuadCore(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.M1_XLARGE);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(request, loadBalancer);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, new MachineDescriptor(1, false, MachineType.M1_XLARGE, 0), loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		//Computing utilisation
		assertEquals(0.125, machine.computeUtilisation(100), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(150), 0.0001);
		
		EasyMock.verify(request, loadBalancer);
	}

	@Test
	public void testComputeUtilisationOfMachineAfterRequestFinishesAndOneCore2(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.M1_SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(request, loadBalancer);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		assertEquals(1.0/3, machine.computeUtilisation(150), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(160), 0.0001);
		
		EasyMock.verify(request, loadBalancer);
	}
	
	@Test
	public void testComputeUtilisationOfMachineAfterRequestFinishesAndDualCore2(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.M1_LARGE);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(request, loadBalancer);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(scheduler, new MachineDescriptor(1, false, MachineType.M1_LARGE, 0), loadBalancer);
		
		machine.sendRequest(request);
		
		scheduler.start();
		
		assertEquals(1.0/6, machine.computeUtilisation(150), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(151), 0.0001);
		
		EasyMock.verify(request, loadBalancer);
	}
	
	@Test
	public void testMachineIsBusyAfterRequestFinishes(){
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.M1_SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(loadBalancer, request);
		
		MultiCoreRanjanMachine machine = new MultiCoreRanjanMachine(JEEventScheduler.getInstance(), descriptor, loadBalancer);
		
		machine.sendRequest(request);
		
		assertTrue(machine.isBusy());//Verifying if machine is busy
		
		JEEventScheduler.getInstance().start();
		
		assertFalse(machine.isBusy());//Verifying if machine is busy
		
		EasyMock.verify(loadBalancer, request);
	}
}
