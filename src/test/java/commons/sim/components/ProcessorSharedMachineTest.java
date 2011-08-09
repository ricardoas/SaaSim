package commons.sim.components;

import static org.junit.Assert.*;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.cloud.Request;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JEEventScheduler.class)
public class ProcessorSharedMachineTest {
	
	private ProcessorSharedMachine machine;
	private long ONE_MINUTE_IN_MILLIS = 1000 * 60;
	private JEEventScheduler scheduler;
	
	/**
	 * This method verifies machine construct and its variables initialization.
	 * @throws Exception
	 */
	@Test
	public void construction() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.replay(scheduler);
		
		long machineID = 1l;
		machine = new ProcessorSharedMachine(scheduler, new MachineDescriptor(machineID, false, 0), null);
		
		EasyMock.verify(scheduler);
		
		assertEquals(machineID,  machine.getMachineID());
		assertNull(machine.getLoadBalancer());
		assertEquals(0, machine.getNumberOfRequestsArrivalsInPreviousInterval());
		assertEquals(0, machine.getNumberOfRequestsCompletionsInPreviousInterval());
		assertEquals(0, machine.getQueue().size());
		assertEquals(0, machine.getTotalProcessed(), 0.0);
		
		assertFalse(machine.isReserved);
		assertFalse(machine.shutdownOnFinish);
		assertNull(machine.nextFinishEvent);
	}
	
	/**
	 * This method verifies that a single request is correctly added to a machine processing queue
	 * @throws Exception 
	 */
	@Test
	public void sendRequestWithEmptyServer() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0L)).times(3);
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		EasyMock.replay(request, scheduler);
		
		long machineID = 1l;
		machine = new ProcessorSharedMachine(scheduler, new MachineDescriptor(machineID, true, 0), null);
		machine.sendRequest(request);
		
		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		assertEquals(request, queue.get(0));
		EasyMock.verify(request, scheduler);
	}
	
	/**
	 * This method verifies that two identical requests arriving at same time in a certain machine are 
	 * stored in a processing queue to share the CPU.
	 * @throws Exception
	 */
	@Test
	public void sendTwoIdenticalRequestsAtSameTime() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(7);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(ONE_MINUTE_IN_MILLIS * 10).times(2);

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();

		EasyMock.replay(scheduler, firstRequest, secondRequest);
		
		long machineID = 1l;
		machine = new ProcessorSharedMachine(scheduler, new MachineDescriptor(machineID, true, 0), null);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		Request firstRequestAtQueue = queue.get(0);
		assertEquals(firstRequest, firstRequestAtQueue);
		assertEquals(600000L, firstRequestAtQueue.getTotalToProcess());
		
		Request secondRequestAtQueue = queue.get(1);
		assertEquals(secondRequest, secondRequestAtQueue);
		assertEquals(600000L, secondRequestAtQueue.getTotalToProcess());
		
		EasyMock.verify(scheduler, firstRequest, secondRequest);
	}
	
	/**
	 * This method verifies that two different requests arriving at same time in a certain machine are 
	 * stored in a processing queue to share the CPU.
	 * @throws Exception
	 */
	@Test
	public void sendDifferentRequestsAtSameTime() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(7);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(0L).once();

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 5).once();

		EasyMock.replay(scheduler, firstRequest, secondRequest);
		
		long machineID = 1l;
		machine = new ProcessorSharedMachine(scheduler, new MachineDescriptor(machineID, true, 0), null);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);

		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		Request firstRequestAtQueue = queue.get(0);
		assertEquals(firstRequest, firstRequestAtQueue);
		
		Request secondRequestAtQueue = queue.get(1);
		assertEquals(secondRequest, secondRequestAtQueue);
		
		EasyMock.verify(scheduler, firstRequest, secondRequest);
	}
	
	/**
	 * This method verifies that two identical requests arriving at different times in a certain machine
	 * are stored in a processing queue to share the CPU. As a second request arrives in a different time
	 * previous requests should have their execution times updated.
	 * @throws Exception
	 */
	@Test
	public void sendTwoIdenticalRequestsAtDifferentOverlappingTimes() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(3);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(100000L)).times(4);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		firstRequest.update(100000L);
		EasyMock.expectLastCall();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(500000L).once();

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();

		EasyMock.replay(scheduler, firstRequest, secondRequest);
		
		long machineID = 1l;
		machine = new ProcessorSharedMachine(scheduler, new MachineDescriptor(machineID, true, 0), null);

		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		Request firstRequestAtQueue = queue.get(0);
		assertEquals(firstRequest, firstRequestAtQueue);
		
		Request secondRequestAtQueue = queue.get(1);
		assertEquals(secondRequest, secondRequestAtQueue);
		
		EasyMock.verify(scheduler, firstRequest, secondRequest);
	}
	
	/**
	 * This method verifies that three requests arriving at different times in a certain machine
	 * are stored in a processing queue to share the CPU. During a certain interval the first request
	 * consumes the CPU alone, then it shares the CPU with the second request and, finally, the CPU
	 * is shared by three requests.
	 * @throws Exception
	 */
	@Test
	public void sendMoreRequestsAtDifferentOverlappingTimes() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(3);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(50000L)).times(4);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(150000L)).times(4);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		firstRequest.update(50000L);
		EasyMock.expectLastCall();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(550000L).once();
		firstRequest.update(50000L);
		EasyMock.expectLastCall();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(500000L).once();

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		secondRequest.update(50000L);
		EasyMock.expectLastCall();

		Request thirdRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(thirdRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 2).once();
		
		EasyMock.replay(scheduler, firstRequest, secondRequest, thirdRequest);
		
		long machineID = 1l;
		machine = new ProcessorSharedMachine(scheduler, new MachineDescriptor(machineID, true, 0), null);

		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		machine.sendRequest(thirdRequest);
		
		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		Request firstRequestAtQueue = queue.get(0);
		assertEquals(firstRequest, firstRequestAtQueue);
		
		Request secondRequestAtQueue = queue.get(1);
		assertEquals(secondRequest, secondRequestAtQueue);
		
		Request thirdRequesAtQueue = queue.get(2);
		assertEquals(thirdRequest, thirdRequesAtQueue);
		
		EasyMock.verify(scheduler, firstRequest, secondRequest, thirdRequest);
	}
	
	/**
	 * This method verifies that a finish event makes single request in the queue to be fully
	 * processed and then the queue becomes empty
	 * @throws Exception
	 */
	@Test
	public void finishSingleRequest() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0L)).times(3);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(ONE_MINUTE_IN_MILLIS * 10));
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		request.update(ONE_MINUTE_IN_MILLIS * 10);
		EasyMock.expectLastCall();

		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(request, scheduler, loadBalancer);
		
		long machineID = 1l;
		machine = new ProcessorSharedMachine(scheduler, new MachineDescriptor(machineID, true, 0), null);
		machine.setLoadBalancer(loadBalancer);
		machine.sendRequest(request);
		
		//Simulating that request end event has arrived
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.expect(event.getType()).andReturn(JEEventType.REQUEST_FINISHED);
		EasyMock.expect(event.getValue()).andReturn(new Request[] {request});
		EasyMock.replay(event);
		
		machine.handleEvent(event);
		
		EasyMock.verify(request, scheduler, event, loadBalancer);
		
		//Verifying that no more requests are pending
		List<Request> queue = machine.getQueue();
		assertTrue(queue.isEmpty());
	}
	
	/**
	 * This method verifies that a finish event makes the request that is satisfied by the intervall duration
	 * to be fully processed, but other requests in queue are partially processed and remain in the queue
	 * to be processed in the future.
	 * @throws Exception
	 */
	@Test
	public void finishEventWithMultipleRequests() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(7);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(ONE_MINUTE_IN_MILLIS * 4)).times(3);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		firstRequest.update(ONE_MINUTE_IN_MILLIS * 2);
		EasyMock.expectLastCall();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(ONE_MINUTE_IN_MILLIS * 8).times(3);

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 2).once();
		secondRequest.update(ONE_MINUTE_IN_MILLIS * 2);
		EasyMock.expectLastCall();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(secondRequest);
		
		EasyMock.replay(scheduler, firstRequest, secondRequest, loadBalancer);
		
		long machineID = 1l;
		machine = new ProcessorSharedMachine(scheduler, new MachineDescriptor(machineID, true, 0), null);
		machine.setLoadBalancer(loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);

		//Simulating that request end event has arrived
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.expect(event.getType()).andReturn(JEEventType.REQUEST_FINISHED);
		EasyMock.expect(event.getValue()).andReturn(new Request[] {secondRequest});
		EasyMock.replay(event);
		
		machine.handleEvent(event);
		
		EasyMock.verify(scheduler, firstRequest, secondRequest, event, loadBalancer);
		
		//Verifying that second request was finished, but first request is still pending
		List<Request> queue = machine.getQueue();
		assertFalse(queue.isEmpty());
		assertEquals(1, queue.size());
		assertEquals(firstRequest, queue.get(0));
	}
	
	/**
	 * This method verifies that handling a finish request event that refers to an inexistent request
	 * tries to process requests in the queue and to create a new finish event. So, no error occurs.
	 * @throws Exception
	 */
	@Test
	public void finishEventWithoutRequests() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0));

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		Request secondRequest = EasyMock.createStrictMock(Request.class);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(firstRequest);
		
		EasyMock.replay(scheduler, firstRequest, secondRequest, loadBalancer);
		
		long machineID = 1l;
		machine = new ProcessorSharedMachine(scheduler, new MachineDescriptor(machineID, true, 0), null);
		machine.setLoadBalancer(loadBalancer);
		assertEquals(0, machine.getQueue().size());

		//Simulating that request end event has arrived
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.expect(event.getType()).andReturn(JEEventType.REQUEST_FINISHED);
		EasyMock.expect(event.getValue()).andReturn(new Request[] {firstRequest});
		EasyMock.replay(event);
		
		machine.handleEvent(event);
		
		EasyMock.verify(scheduler, firstRequest, secondRequest, event, loadBalancer);
		
		assertEquals(0, machine.getQueue().size());
	}
	
	/**
	 * This method verifies that as soon as a machine is created and has not received any requests,
	 * the machine is not busy.
	 * @throws Exception
	 */
	@Test
	public void machineIsNotBusy() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.replay(scheduler);
		
		//Without requests arriving machine is not busy
		long machineID = 1l;
		machine = new ProcessorSharedMachine(scheduler, new MachineDescriptor(machineID, true, 0), null);
		assertFalse(machine.isBusy());
		
		EasyMock.verify(scheduler);
	}
	
	/**
	 * This method verifies that as soon as a machine receives a request to be processed, it
	 * becomes busy.
	 * @throws Exception
	 */
	@Test
	public void machineIsBusy() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0L)).times(3);
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		EasyMock.replay(request, scheduler);
		
		//After requests arriving, machine becomes busy
		long machineID = 1l;
		machine = new ProcessorSharedMachine(scheduler, new MachineDescriptor(machineID, true, 0), null);
		
		assertFalse(machine.isBusy());
		machine.sendRequest(request);
		assertTrue(machine.isBusy());
		
		EasyMock.verify(request, scheduler);
	}
	
	/**
	 * This method verifies that after finishing all pending requests, a machine becomes not busy again.
	 * @throws Exception
	 */
	@Test
	public void machineBecomesNotBusyAfterFinishingRequest() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0L)).times(3);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(ONE_MINUTE_IN_MILLIS * 10));
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		request.update(ONE_MINUTE_IN_MILLIS * 10);
		EasyMock.expectLastCall();

		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(request, scheduler, loadBalancer);
		
		long machineID = 1l;
		machine = new ProcessorSharedMachine(scheduler, new MachineDescriptor(machineID, true, 0), null);
		machine.setLoadBalancer(loadBalancer);
		assertFalse(machine.isBusy());
		machine.sendRequest(request);
		assertTrue(machine.isBusy());
		
		//Simulating that request end event has arrived
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.expect(event.getType()).andReturn(JEEventType.REQUEST_FINISHED);
		EasyMock.expect(event.getValue()).andReturn(new Request[] {request});
		EasyMock.replay(event);
		
		machine.handleEvent(event);
		
		EasyMock.verify(request, scheduler, event, loadBalancer);
		
		//After finishing request, no more requests are pending. So, machine is not busy anymore!
		assertFalse(machine.isBusy());
	}
	
	/**
	 * This method verifies that finishing some requests does not make the machine to become not busy.
	 * If any request remains in the queue machine continues to be busy.
	 * @throws Exception
	 */
	@Test
	public void machineIsBusyAfterRequestsFinishing() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(7);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(ONE_MINUTE_IN_MILLIS * 4)).times(3);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
		firstRequest.update(ONE_MINUTE_IN_MILLIS * 2);
		EasyMock.expectLastCall();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(ONE_MINUTE_IN_MILLIS * 8).times(3);

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 2).once();
		secondRequest.update(ONE_MINUTE_IN_MILLIS * 2);
		EasyMock.expectLastCall();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(secondRequest);
		
		EasyMock.replay(scheduler, firstRequest, secondRequest, loadBalancer);
		
		long machineID = 1l;
		machine = new ProcessorSharedMachine(scheduler, new MachineDescriptor(machineID, true, 0), null);
		machine.setLoadBalancer(loadBalancer);
		
		assertFalse(machine.isBusy());
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		assertTrue(machine.isBusy());

		//Simulating that request end event has arrived
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.expect(event.getType()).andReturn(JEEventType.REQUEST_FINISHED);
		EasyMock.expect(event.getValue()).andReturn(new Request[] {secondRequest});
		EasyMock.replay(event);
		
		machine.handleEvent(event);
		
		EasyMock.verify(scheduler, firstRequest, secondRequest, event, loadBalancer);
		
		//Although second request has finished, first request still needs to be processed and so machine is busy
		assertTrue(machine.isBusy());
	}
	
//	@Test
//	//FIXME: Esse teste está sem muito sentido! :P
//	public void sendTwoDifferentRequestsAtDifferentOverlappingTimesFinishingBefore() throws Exception{
//		scheduler = PowerMock.createStrictPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
//		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(3);
//		EasyMock.expect(scheduler.now()).andReturn(new JETime(100000L)).times(4);
//
//		Request firstRequest = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(firstRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 10).once();
//		firstRequest.update(100000L);
//		EasyMock.expectLastCall();
//		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(500000L).once();
//
//		Request secondRequest = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(secondRequest.getDemand()).andReturn(ONE_MINUTE_IN_MILLIS * 5).once();
//
//		EasyMock.replay(scheduler, firstRequest, secondRequest);
//
//		machine = new Machine(scheduler, 1);
//
//		machine.sendRequest(firstRequest);
//		machine.sendRequest(secondRequest);
//
//		List<Request> queue = machine.getQueue();
//
//		assertFalse(queue.isEmpty());
//		Request firstRequestAtQueue = queue.get(0);
//		assertEquals(firstRequest, firstRequestAtQueue);
//
//		Request secondRequestAtQueue = queue.get(1);
//		assertEquals(secondRequest, secondRequestAtQueue);
//		
//		EasyMock.verify(scheduler, firstRequest, secondRequest);
//	}

//	@Ignore @Test
//	public void sendTwoRequests(){
//		String clientID = "c1";
//		String userID = "u1";
//		String reqID = "1";
//		long time = ONE_MINUTE_IN_MILLIS * 1;
//		long size = 1024;
//		boolean hasExpired = false;
//		String URL = "";
//		String httpOperation = "GET";
//		long demand = ONE_MINUTE_IN_MILLIS * 20;
//		
//		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
//		machine.sendRequest(request);
//		
//		//Request arrives after another one and finishes after previous request
//		long demand2 = ONE_MINUTE_IN_MILLIS * 30;
//		Request request2 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand2);
//		machine.sendRequest(request2);
//		
////		assertEquals(2, machine.queue.size());
//		assertEquals(JEEventType.REQUEST_FINISHED, machine.nextFinishEvent.getType());
//		assertEquals(demand, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
//		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
//		assertEquals(0, machine.finishedRequests.size());
//	}
//	
//	@Ignore @Test
//	public void sendTwoRequestsWithRequestFinishingEqually(){
//		String clientID = "c1";
//		String userID = "u1";
//		String reqID = "1";
//		long time = ONE_MINUTE_IN_MILLIS * 1;
//		long size = 1024;
//		boolean hasExpired = false;
//		String URL = "";
//		String httpOperation = "GET";
//		long demand = ONE_MINUTE_IN_MILLIS * 20;
//		
//		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
//		machine.sendRequest(request);
//		
//		//Request arrives after another one and finishes equals to previous request
//		long demand2 = ONE_MINUTE_IN_MILLIS * 10;
//		Request request2 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand2);
//		machine.sendRequest(request2);
//		
////		assertEquals(2, machine.queue.size());
//		assertEquals(JEEventType.REQUEST_FINISHED, machine.nextFinishEvent.getType());
//		assertEquals(demand, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
//		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
//		assertEquals(0, machine.finishedRequests.size());
//	}
//	
//	@Ignore @Test
//	public void sendTwoRequestsWithRequestFinishingEarlier(){
//		String clientID = "c1";
//		String userID = "u1";
//		String reqID = "1";
//		long time = ONE_MINUTE_IN_MILLIS * 1;
//		long size = 1024;
//		boolean hasExpired = false;
//		String URL = "";
//		String httpOperation = "GET";
//		long demand = ONE_MINUTE_IN_MILLIS * 20;
//		
//		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
//		machine.sendRequest(request);
//		
//		//Request arrives after another one and finishes before to previous request
//		long demand2 = ONE_MINUTE_IN_MILLIS * 2;
//		Request request2 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand2);
//		machine.sendRequest(request2);
//		
////		assertEquals(2, machine.queue.size());
//		assertEquals(JEEventType.REQUEST_FINISHED, machine.nextFinishEvent.getType());
//		assertEquals(demand2 * 2, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
//		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
//		assertEquals(0, machine.finishedRequests.size());
//	}
//	
//	/**
//	 * This method verifies the processing of two requests, considering that the second request scheduled
//	 * finishes before the first one scheduled
//	 */
//	@Ignore @Test
//	public void sendRequestsWithProcessing(){
//		String clientID = "c1";
//		String userID = "u1";
//		String reqID = "1";
//		long time = ONE_MINUTE_IN_MILLIS * 1;
//		long size = 1024;
//		boolean hasExpired = false;
//		String URL = "";
//		String httpOperation = "GET";
//		long demand = ONE_MINUTE_IN_MILLIS * 20;
//		
//		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
//		machine.sendRequest(request);
//		
//		//Request arrives after another one and finishes before previous request
//		long demand2 = 1000 * 60 * 2;
//		Request request2 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand2);
//		machine.sendRequest(request2);
//		
//		//Requesting requests processing
//		machine.handleEvent(new JEEvent(JEEventType.REQUEST_FINISHED, machine, machine.nextFinishEvent.getScheduledTime()));
////		assertEquals(1, machine.queue.size());
//		assertEquals(JEEventType.REQUEST_FINISHED, machine.nextFinishEvent.getType());
//		assertEquals(1, machine.finishedRequests.size());
//		assertEquals(ONE_MINUTE_IN_MILLIS * 4, machine.lastProcessingEvaluation.timeMilliSeconds);
//		assertEquals(ONE_MINUTE_IN_MILLIS * 22, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
////		assertEquals(ONE_MINUTE_IN_MILLIS * 18, machine.queue.get(0).getTotalToProcess());
//		
//		//Requesting final processing
//		machine.handleEvent(new JEEvent(JEEventType.REQUEST_FINISHED, machine, machine.nextFinishEvent.getScheduledTime()));
////		assertEquals(0, machine.queue.size());
//		assertEquals(2, machine.finishedRequests.size());
//		assertNull(machine.nextFinishEvent);
//		assertEquals(ONE_MINUTE_IN_MILLIS * 22, machine.lastProcessingEvaluation.timeMilliSeconds);
//	}
//	
//	/**
//	 * This method verifies the processing of two requests, considering that both requests scheduled
//	 * have the same demand
//	 */
//	@Ignore @Test
//	public void sendTwoRequestsWithProcessing(){
//		String clientID = "c1";
//		String userID = "u1";
//		String reqID = "1";
//		long time = ONE_MINUTE_IN_MILLIS * 1;
//		long size = 1024;
//		boolean hasExpired = false;
//		String URL = "";
//		String httpOperation = "GET";
//		long demand = ONE_MINUTE_IN_MILLIS * 20;
//		
//		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
//		machine.sendRequest(request);
//		
//		//Request arrives after another one and finishes equals to previous request
//		long demand2 = ONE_MINUTE_IN_MILLIS * 20;
//		Request request2 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand2);
//		machine.sendRequest(request2);
//		
////		assertEquals(2, machine.queue.size());
//		assertEquals(JEEventType.REQUEST_FINISHED, machine.nextFinishEvent.getType());
//		assertEquals(demand, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
//		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
//		assertEquals(0, machine.finishedRequests.size());
//		
//		//Requesting requests processing
//		machine.handleEvent(new JEEvent(JEEventType.REQUEST_FINISHED, machine, machine.nextFinishEvent.getScheduledTime()));
////		assertEquals(2, machine.queue.size());
//		assertEquals(JEEventType.REQUEST_FINISHED, machine.nextFinishEvent.getType());
//		assertEquals(0, machine.finishedRequests.size());
//		assertEquals(ONE_MINUTE_IN_MILLIS * 20, machine.lastProcessingEvaluation.timeMilliSeconds);
//		assertEquals(ONE_MINUTE_IN_MILLIS * 40, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
////		assertEquals(ONE_MINUTE_IN_MILLIS * 10, machine.queue.get(0).getTotalToProcess());
////		assertEquals(ONE_MINUTE_IN_MILLIS * 10, machine.queue.get(1).getTotalToProcess());
//		
//		//Requesting final processing
//		machine.handleEvent(new JEEvent(JEEventType.REQUEST_FINISHED, machine, machine.nextFinishEvent.getScheduledTime()));
////		assertEquals(0, machine.queue.size());
//		assertEquals(2, machine.finishedRequests.size());
//		assertNull(machine.nextFinishEvent);
//		assertEquals(ONE_MINUTE_IN_MILLIS * 40, machine.lastProcessingEvaluation.timeMilliSeconds);
//	}
//	
//	@Ignore @Test
//	public void computeUtilizationWithoutDemand(){
//		assertEquals(0, machine.computeUtilization(0), 0.0);
//		assertEquals(0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 10), 0.0);
//	}
//	
//	@Ignore @Test
//	public void computeUtilizationWithUnfinishedDemand(){
//		String clientID = "c1";
//		String userID = "u1";
//		String reqID = "1";
//		long time = ONE_MINUTE_IN_MILLIS * 1;
//		long size = 1024;
//		boolean hasExpired = false;
//		String URL = "";
//		String httpOperation = "GET";
//		long demand = ONE_MINUTE_IN_MILLIS * 20;
//		
//		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
////		machine.queue.add(request);
////		machine.queue.add(request);
//		
//		//No processing is requested. Evaluating utilization
//		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 10), 0.0);
//		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 20), 0.0);
//		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 30), 0.0);
//		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 40), 0.0);
//		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 90), 0.0);
//	}
//	
//	@Ignore @Test
//	public void computeUtilizationWithFinishedDemand(){
//		String clientID = "c1";
//		String userID = "u1";
//		String reqID = "1";
//		long time = ONE_MINUTE_IN_MILLIS * 1;
//		long size = 1024;
//		boolean hasExpired = false;
//		String URL = "";
//		String httpOperation = "GET";
//		long demand = ONE_MINUTE_IN_MILLIS * 4;
//		
//		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
//		machine.sendRequest(request);
//		machine.sendRequest(request);
//		
//		//Evaluating utilization
//		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 1), 0.0);
//		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 2), 0.0);
//		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 5), 0.0);
//		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 7), 0.0);
//		
//		//Processing
//		machine.handleEvent(new JEEvent(JEEventType.REQUEST_FINISHED, machine, new JETime(demand * 2)));
//		
//		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 8), 0.0);//exactly at demand end time
//		assertEquals(0.6, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 10), 0.0);
//		assertEquals(0.2, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 12), 0.0);
//		assertEquals(0.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 14), 0.0);
//	}
}
