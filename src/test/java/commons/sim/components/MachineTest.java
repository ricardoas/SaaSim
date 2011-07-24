package commons.sim.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
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
public class MachineTest {
	
	private Machine machine;
	private long ONE_MINUTE_IN_MILLIS = 1000 * 60;
	private JEEventScheduler scheduler;

	/**
	 * This method verifies that a single request is correctly added to a machine
	 * @throws Exception 
	 */
	@Test
	public void sendRequestWithEmptyServer() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0L)).times(3);
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getDemand()).andReturn(600000L).once();
		EasyMock.replay(request, scheduler);
		
		machine = new Machine(scheduler, 1);
		machine.sendRequest(request);
		
		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		assertEquals(request, queue.get(0));
		EasyMock.verify(request, scheduler);
	}
	
	@Test
	public void sendTwoIdenticalRequestsAtSameTime() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(7);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(600000L).once();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(600000L).times(2);

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getDemand()).andReturn(600000L).once();
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(600000L).once();

		EasyMock.replay(scheduler, firstRequest, secondRequest);
		
		machine = new Machine(scheduler, 1);
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
	
	@Test
	public void sendDifferentRequestsAtSameTime() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(7);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(600000L).once();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(0L).once();

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getDemand()).andReturn(300000L).once();

		EasyMock.replay(scheduler, firstRequest, secondRequest);
		
		machine = new Machine(scheduler, 1);
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
	
	@Test
	public void sendTwoIdenticalRequestsAtDifferentOverlappingTimes() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(3);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(100000L)).times(4);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(600000L).once();
		firstRequest.update(100000L);
		EasyMock.expectLastCall();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(500000L).once();

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getDemand()).andReturn(600000L).once();

		EasyMock.replay(scheduler, firstRequest, secondRequest);
		
		machine = new Machine(scheduler, 1);

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
	
	@Test
	public void sendTwoDifferentRequestsAtDifferentOverlappingTimesFinishingBefore() throws Exception{
		scheduler = PowerMock.createStrictPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(3);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(100000L)).times(4);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(600000L).once();
		firstRequest.update(100000L);
		EasyMock.expectLastCall();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(500000L).once();

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getDemand()).andReturn(300000L).once();

		EasyMock.replay(scheduler, firstRequest, secondRequest);

		machine = new Machine(scheduler, 1);

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

	@Ignore @Test
	public void sendTwoRequests(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = ONE_MINUTE_IN_MILLIS * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = ONE_MINUTE_IN_MILLIS * 20;
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine.sendRequest(request);
		
		//Request arrives after another one and finishes after previous request
		long demand2 = ONE_MINUTE_IN_MILLIS * 30;
		Request request2 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand2);
		machine.sendRequest(request2);
		
//		assertEquals(2, machine.queue.size());
		assertEquals(JEEventType.REQUEST_FINISHED, machine.nextFinishEvent.getType());
		assertEquals(demand, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(0, machine.finishedRequests.size());
	}
	
	@Ignore @Test
	public void sendTwoRequestsWithRequestFinishingEqually(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = ONE_MINUTE_IN_MILLIS * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = ONE_MINUTE_IN_MILLIS * 20;
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine.sendRequest(request);
		
		//Request arrives after another one and finishes equals to previous request
		long demand2 = ONE_MINUTE_IN_MILLIS * 10;
		Request request2 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand2);
		machine.sendRequest(request2);
		
//		assertEquals(2, machine.queue.size());
		assertEquals(JEEventType.REQUEST_FINISHED, machine.nextFinishEvent.getType());
		assertEquals(demand, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(0, machine.finishedRequests.size());
	}
	
	@Ignore @Test
	public void sendTwoRequestsWithRequestFinishingEarlier(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = ONE_MINUTE_IN_MILLIS * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = ONE_MINUTE_IN_MILLIS * 20;
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine.sendRequest(request);
		
		//Request arrives after another one and finishes before to previous request
		long demand2 = ONE_MINUTE_IN_MILLIS * 2;
		Request request2 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand2);
		machine.sendRequest(request2);
		
//		assertEquals(2, machine.queue.size());
		assertEquals(JEEventType.REQUEST_FINISHED, machine.nextFinishEvent.getType());
		assertEquals(demand2 * 2, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(0, machine.finishedRequests.size());
	}
	
	/**
	 * This method verifies the processing of two requests, considering that the second request scheduled
	 * finishes before the first one scheduled
	 */
	@Ignore @Test
	public void sendRequestsWithProcessing(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = ONE_MINUTE_IN_MILLIS * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = ONE_MINUTE_IN_MILLIS * 20;
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine.sendRequest(request);
		
		//Request arrives after another one and finishes before previous request
		long demand2 = 1000 * 60 * 2;
		Request request2 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand2);
		machine.sendRequest(request2);
		
		//Requesting requests processing
		machine.handleEvent(new JEEvent(JEEventType.REQUEST_FINISHED, machine, machine.nextFinishEvent.getScheduledTime()));
//		assertEquals(1, machine.queue.size());
		assertEquals(JEEventType.REQUEST_FINISHED, machine.nextFinishEvent.getType());
		assertEquals(1, machine.finishedRequests.size());
		assertEquals(ONE_MINUTE_IN_MILLIS * 4, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(ONE_MINUTE_IN_MILLIS * 22, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
//		assertEquals(ONE_MINUTE_IN_MILLIS * 18, machine.queue.get(0).getTotalToProcess());
		
		//Requesting final processing
		machine.handleEvent(new JEEvent(JEEventType.REQUEST_FINISHED, machine, machine.nextFinishEvent.getScheduledTime()));
//		assertEquals(0, machine.queue.size());
		assertEquals(2, machine.finishedRequests.size());
		assertNull(machine.nextFinishEvent);
		assertEquals(ONE_MINUTE_IN_MILLIS * 22, machine.lastProcessingEvaluation.timeMilliSeconds);
	}
	
	/**
	 * This method verifies the processing of two requests, considering that both requests scheduled
	 * have the same demand
	 */
	@Ignore @Test
	public void sendTwoRequestsWithProcessing(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = ONE_MINUTE_IN_MILLIS * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = ONE_MINUTE_IN_MILLIS * 20;
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine.sendRequest(request);
		
		//Request arrives after another one and finishes equals to previous request
		long demand2 = ONE_MINUTE_IN_MILLIS * 20;
		Request request2 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand2);
		machine.sendRequest(request2);
		
//		assertEquals(2, machine.queue.size());
		assertEquals(JEEventType.REQUEST_FINISHED, machine.nextFinishEvent.getType());
		assertEquals(demand, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(0, machine.finishedRequests.size());
		
		//Requesting requests processing
		machine.handleEvent(new JEEvent(JEEventType.REQUEST_FINISHED, machine, machine.nextFinishEvent.getScheduledTime()));
//		assertEquals(2, machine.queue.size());
		assertEquals(JEEventType.REQUEST_FINISHED, machine.nextFinishEvent.getType());
		assertEquals(0, machine.finishedRequests.size());
		assertEquals(ONE_MINUTE_IN_MILLIS * 20, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(ONE_MINUTE_IN_MILLIS * 40, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
//		assertEquals(ONE_MINUTE_IN_MILLIS * 10, machine.queue.get(0).getTotalToProcess());
//		assertEquals(ONE_MINUTE_IN_MILLIS * 10, machine.queue.get(1).getTotalToProcess());
		
		//Requesting final processing
		machine.handleEvent(new JEEvent(JEEventType.REQUEST_FINISHED, machine, machine.nextFinishEvent.getScheduledTime()));
//		assertEquals(0, machine.queue.size());
		assertEquals(2, machine.finishedRequests.size());
		assertNull(machine.nextFinishEvent);
		assertEquals(ONE_MINUTE_IN_MILLIS * 40, machine.lastProcessingEvaluation.timeMilliSeconds);
	}
	
	@Ignore @Test
	public void computeUtilizationWithoutDemand(){
		assertEquals(0, machine.computeUtilization(0), 0.0);
		assertEquals(0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 10), 0.0);
	}
	
	@Ignore @Test
	public void computeUtilizationWithUnfinishedDemand(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = ONE_MINUTE_IN_MILLIS * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = ONE_MINUTE_IN_MILLIS * 20;
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
//		machine.queue.add(request);
//		machine.queue.add(request);
		
		//No processing is requested. Evaluating utilization
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 10), 0.0);
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 20), 0.0);
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 30), 0.0);
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 40), 0.0);
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 90), 0.0);
	}
	
	@Ignore @Test
	public void computeUtilizationWithFinishedDemand(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = ONE_MINUTE_IN_MILLIS * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = ONE_MINUTE_IN_MILLIS * 4;
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine.sendRequest(request);
		machine.sendRequest(request);
		
		//Evaluating utilization
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 1), 0.0);
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 2), 0.0);
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 5), 0.0);
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 7), 0.0);
		
		//Processing
		machine.handleEvent(new JEEvent(JEEventType.REQUEST_FINISHED, machine, new JETime(demand * 2)));
		
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 8), 0.0);//exactly at demand end time
		assertEquals(0.6, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 10), 0.0);
		assertEquals(0.2, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 12), 0.0);
		assertEquals(0.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 14), 0.0);
	}
}
