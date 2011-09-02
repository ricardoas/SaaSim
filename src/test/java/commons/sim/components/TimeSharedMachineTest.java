package commons.sim.components;

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
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TimeSharedMachine.class)
public class TimeSharedMachineTest {

	private MachineDescriptor descriptor;

	@Before
	public void setUp() {
		this.descriptor = new MachineDescriptor(1, false, MachineType.SMALL);
	}

	@Test
	public void testConstructor() {
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		EasyMock.replay(loadBalancer);
		
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), descriptor, loadBalancer);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertTrue(queue.isEmpty());
		assertEquals(descriptor, machine.getDescriptor());
		
		EasyMock.verify(loadBalancer);
	}
	
	@Test
	public void testSendBigRequestWithEmptyMachine(){
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(5000L);
		
		TimeSharedMachine machine = PowerMock.createStrictPartialMock(TimeSharedMachine.class, new String[]{"handleEvent"}, scheduler, descriptor, loadBalancer);
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
		assertEquals(new JETime(100L), event.getScheduledTime());
		
		EasyMock.verify(loadBalancer, request, machine);
	}

	@Test
	public void testSendSmallRequestWithEmptyMachine(){
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		
		TimeSharedMachine machine = PowerMock.createStrictPartialMock(TimeSharedMachine.class, new String[]{"handleEvent"}, scheduler, descriptor, loadBalancer);
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
		
		EasyMock.verify(loadBalancer, request, machine);
	}

	@Test
	public void testSendTwoRequestWithEmptyMachine(){
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request firstRequest = EasyMock.createStrictMock(Request.class);
		firstRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(5000L);
		Request secondRequest = EasyMock.createStrictMock(Request.class);
		secondRequest.assignTo(MachineType.SMALL);
		EasyMock.replay(loadBalancer, firstRequest, secondRequest);
		
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), descriptor, loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertFalse(queue.isEmpty());
		assertEquals(firstRequest, queue.poll());
		assertEquals(secondRequest, queue.poll());
		
		EasyMock.verify(loadBalancer, firstRequest, secondRequest);
	}
	
	@Test
	public void testShutdownWithEmptyMachine(){
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(scheduler.registerHandler(loadBalancer));
		Capture<JEEvent> captured = new Capture<JEEvent>();
		loadBalancer.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		
		EasyMock.replay(loadBalancer);
		
		Machine machine = new TimeSharedMachine(scheduler, descriptor, loadBalancer);
		machine.shutdownOnFinish();
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, event.getType());
		assertEquals(machine.getDescriptor(), event.getValue()[0]);
		
		EasyMock.verify(loadBalancer);
	}

	@Test
	public void testShutdownWithNonEmptyMachine(){
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(5000L);
		
		TimeSharedMachine machine = PowerMock.createStrictPartialMock(TimeSharedMachine.class, new String[]{"handleEvent"}, scheduler, descriptor, loadBalancer);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		machine.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		
		EasyMock.replay(loadBalancer, request, machine);
		
		machine.sendRequest(request);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertFalse(queue.isEmpty());
		machine.shutdownOnFinish();
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.PREEMPTION, event.getType());
		assertEquals(new JETime(100L), event.getScheduledTime());
		
		EasyMock.verify(loadBalancer, request, machine);
	}
	
	/**
	 * In this scenario events the scheduler deals with some preemption events until the request
	 * is fully processed. After the request is fully processed, the machine is turned off.
	 */
	@Test
	public void testHandlePreemptionOfLastRequestOnQueueWithShutdown(){
		JEEventScheduler scheduler = new JEEventScheduler();

		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(250L);
		request.update(100L);
		EasyMock.expect(request.isFinished()).andReturn(false);
		EasyMock.expect(request.getTotalToProcess()).andReturn(150L);
		request.update(100L);
		EasyMock.expect(request.isFinished()).andReturn(false);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		request.update(50L);
		EasyMock.expect(request.isFinished()).andReturn(true);
		EasyMock.expect(request.getRequestSizeInBytes()).andReturn(100000L);
		EasyMock.expect(request.getResponseSizeInBytes()).andReturn(100000L);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(request);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(scheduler.registerHandler(loadBalancer));
		Capture<JEEvent> captured = new Capture<JEEvent>();
		loadBalancer.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		
		EasyMock.replay(loadBalancer, request);

		Machine machine = new TimeSharedMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(request);
		machine.shutdownOnFinish();
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, event.getType());
		assertEquals(machine.getDescriptor(), event.getValue()[0]);
		
		EasyMock.verify(loadBalancer, request);
	}

	/**
	 * The second request finishes first.
	 */
	@Test
	public void testHandlePreemptionOfTwoRequestOnQueueWithOutShutdown(){
		JEEventScheduler scheduler = new JEEventScheduler();

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		firstRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(500L);
		firstRequest.update(100L);
		EasyMock.expect(firstRequest.isFinished()).andReturn(false);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(400L);
		firstRequest.update(100L);
		EasyMock.expect(firstRequest.isFinished()).andReturn(false);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(300L);
		firstRequest.update(100L);
		EasyMock.expect(firstRequest.isFinished()).andReturn(false);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(200L);
		firstRequest.update(100L);
		EasyMock.expect(firstRequest.isFinished()).andReturn(false);
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(100L);
		firstRequest.update(100L);
		EasyMock.expect(firstRequest.isFinished()).andReturn(true);
		EasyMock.expect(firstRequest.getRequestSizeInBytes()).andReturn(100l);
		EasyMock.expect(firstRequest.getResponseSizeInBytes()).andReturn(10000l);
		
		Request secondRequest = EasyMock.createStrictMock(Request.class);
		secondRequest.assignTo(MachineType.SMALL);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(400L);
		secondRequest.update(100L);
		EasyMock.expect(secondRequest.isFinished()).andReturn(false);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(300L);
		secondRequest.update(100L);
		EasyMock.expect(secondRequest.isFinished()).andReturn(false);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(200L);
		secondRequest.update(100L);
		EasyMock.expect(secondRequest.isFinished()).andReturn(false);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(100L);
		secondRequest.update(100L);
		EasyMock.expect(secondRequest.isFinished()).andReturn(true);
		EasyMock.expect(secondRequest.getRequestSizeInBytes()).andReturn(100l);
		EasyMock.expect(secondRequest.getResponseSizeInBytes()).andReturn(10000l);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(secondRequest);
		EasyMock.expectLastCall();
		loadBalancer.reportRequestFinished(firstRequest);
		EasyMock.expectLastCall();
		
		EasyMock.replay(loadBalancer, firstRequest, secondRequest);
		
		Machine machine = new TimeSharedMachine(scheduler, descriptor, loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		scheduler.start();
		
		EasyMock.verify(loadBalancer, firstRequest, secondRequest);
	}
	
	@Test
	public void testComputeUtilisationOfEmptyMachine(){
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		EasyMock.replay(loadBalancer);
		
		JEEventScheduler scheduler = new JEEventScheduler();
		Machine machine = new TimeSharedMachine(scheduler, descriptor, loadBalancer);
		assertEquals(Double.NaN, machine.computeUtilisation(scheduler.now().timeMilliSeconds), 0.0001);
		assertEquals(0, machine.computeUtilisation(scheduler.now().timeMilliSeconds + 300000), 0.0001);
		
		EasyMock.verify(loadBalancer);
	}

	/**
	 * As the request finish event has not happened, the time that is an attribute of computeUtilisation
	 * is used as the computing time
	 */
	@Test
	public void testComputeUtilisationOfMachineWithRunningRequest(){
		
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		
		TimeSharedMachine machine = new TimeSharedMachine(scheduler, descriptor, loadBalancer);
		
		EasyMock.replay(loadBalancer, request);
		
		machine.sendRequest(request);
		
		assertEquals(Double.NaN, machine.computeUtilisation(scheduler.now().timeMilliSeconds), 0.0001);
		assertEquals(1, machine.computeUtilisation(scheduler.now().timeMilliSeconds + 50), 0.0001);
		
		EasyMock.verify(loadBalancer, request);
	}

	@Test
	public void testComputeUtilisationOfMachineAsRequestFinishes(){
		
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
		
		EasyMock.replay(loadBalancer, request);
		
		TimeSharedMachine machine = new TimeSharedMachine(scheduler, descriptor, loadBalancer);
		
		machine.sendRequest(request);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertFalse(queue.isEmpty());
		
		scheduler.start();
		
		assertEquals(1, machine.computeUtilisation(50), 0.0001);
		
		EasyMock.verify(loadBalancer, request);
	}

	@Test
	public void testComputeUtilisationOfMachineAfterRequestFinishes(){
		
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
		
		EasyMock.replay(loadBalancer, request);
		
		TimeSharedMachine machine = new TimeSharedMachine(scheduler, descriptor, loadBalancer);
		
		machine.sendRequest(request);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertFalse(queue.isEmpty());
		
		scheduler.start();
		
		assertEquals(0.5, machine.computeUtilisation(100), 0.0001);
		
		EasyMock.verify(loadBalancer, request);
	}

	@Test
	public void testComputeUtilisationOfMachineWithRequestFinishes2(){
		
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
		
		EasyMock.replay(loadBalancer, request);
		
		TimeSharedMachine machine = new TimeSharedMachine(scheduler, descriptor, loadBalancer);
		
		machine.sendRequest(request);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertFalse(queue.isEmpty());
		
		scheduler.start();
		
		assertEquals(1.0/3, machine.computeUtilisation(150), 0.0001);
		
		EasyMock.verify(loadBalancer, request);
	}

	@Test
	public void testIsBusyWithRequests(){
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = EasyMock.createStrictMock(Request.class);
		request.assignTo(MachineType.SMALL);
		EasyMock.expect(request.getTotalToProcess()).andReturn(50L);
		
		EasyMock.replay(loadBalancer, request);
		
		TimeSharedMachine machine = new TimeSharedMachine(scheduler, descriptor, loadBalancer);
		
		machine.sendRequest(request);
		
		EasyMock.verify(loadBalancer, request);
		
		//Verifying if machine is busy
		assertTrue(machine.isBusy());
	}
	
	@Test
	public void testIsBusyWithoutRequests(){
		JEEventScheduler scheduler = new JEEventScheduler();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		EasyMock.replay(loadBalancer);
		
		TimeSharedMachine machine = new TimeSharedMachine(scheduler, descriptor, loadBalancer);
		
		EasyMock.verify(loadBalancer);
		
		//Verifying if machine is busy
		assertFalse(machine.isBusy());
	}
	
	@Test
	public void testMachineIsBusyAfterRequestFinishes(){
		
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
		
		EasyMock.replay(loadBalancer, request);
		
		TimeSharedMachine machine = new TimeSharedMachine(scheduler, descriptor, loadBalancer);
		
		machine.sendRequest(request);
		assertTrue(machine.isBusy());//Verifying if machine is busy
		
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertFalse(queue.isEmpty());
		
		scheduler.start();
		
		EasyMock.verify(loadBalancer, request);
		
		assertFalse(machine.isBusy());//Verifying if machine is busy
	}
	
//	@Test
//	public void testEstimateFinishTime(){
//		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
//		
//		EasyMock.replay(loadBalancer);
//
//		Machine machine = new TimeSharedMachine(new JEEventScheduler(), descriptor, loadBalancer);
//		long reqID = 0;
//		Random random = new Random();
//		for (int i = 0; i < 70; i++) {
//			machine.sendRequest(new Request("", reqID+++"", "", 0, 0, "", random.nextInt(500)));
//		}
//		
//		Request request = new Request("", reqID+++"", "", 0, 0, "", random.nextInt(500));
//		machine.estimateFinishTime(request);
//		
//		EasyMock.verify(loadBalancer);
//	}
}

