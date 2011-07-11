package commons.cloud;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;


public class MachineTest {
	
	private Machine machine;
	private long ONE_MINUTE_IN_MILLIS = 1000 * 60;

	@Before
	public void setUp(){
		machine = new Machine(1);
	}
	
	/**
	 * This method verifies that a single request is correctly added to a machine
	 */
	@Test
	public void sendFirstRequest(){
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
		
		assertEquals(1, machine.queue.size());
		assertEquals(JEEventType.FINISHREQUEST, machine.nextFinishEvent.getType());
		assertEquals(demand, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(0, machine.finishedRequests.size());
	}
	
	@Test
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
		
		assertEquals(2, machine.queue.size());
		assertEquals(JEEventType.FINISHREQUEST, machine.nextFinishEvent.getType());
		assertEquals(demand, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(0, machine.finishedRequests.size());
	}
	
	@Test
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
		
		assertEquals(2, machine.queue.size());
		assertEquals(JEEventType.FINISHREQUEST, machine.nextFinishEvent.getType());
		assertEquals(demand, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(0, machine.finishedRequests.size());
	}
	
	@Test
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
		
		assertEquals(2, machine.queue.size());
		assertEquals(JEEventType.FINISHREQUEST, machine.nextFinishEvent.getType());
		assertEquals(demand2 * 2, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(0, machine.finishedRequests.size());
	}
	
	/**
	 * This method verifies the processing of two requests, considering that the second request scheduled
	 * finishes before the first one scheduled
	 */
	@Test
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
		machine.handleEvent(new JEEvent(JEEventType.FINISHREQUEST, machine, machine.nextFinishEvent.getScheduledTime(), null));
		assertEquals(1, machine.queue.size());
		assertEquals(JEEventType.FINISHREQUEST, machine.nextFinishEvent.getType());
		assertEquals(1, machine.finishedRequests.size());
		assertEquals(ONE_MINUTE_IN_MILLIS * 4, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(ONE_MINUTE_IN_MILLIS * 22, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(ONE_MINUTE_IN_MILLIS * 18, machine.queue.get(0).getTotalToProcess());
		
		//Requesting final processing
		machine.handleEvent(new JEEvent(JEEventType.FINISHREQUEST, machine, machine.nextFinishEvent.getScheduledTime(), null));
		assertEquals(0, machine.queue.size());
		assertEquals(2, machine.finishedRequests.size());
		assertNull(machine.nextFinishEvent);
		assertEquals(ONE_MINUTE_IN_MILLIS * 22, machine.lastProcessingEvaluation.timeMilliSeconds);
	}
	
	/**
	 * This method verifies the processing of two requests, considering that both requests scheduled
	 * have the same demand
	 */
	@Test
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
		
		assertEquals(2, machine.queue.size());
		assertEquals(JEEventType.FINISHREQUEST, machine.nextFinishEvent.getType());
		assertEquals(demand, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(0, machine.finishedRequests.size());
		
		//Requesting requests processing
		machine.handleEvent(new JEEvent(JEEventType.FINISHREQUEST, machine, machine.nextFinishEvent.getScheduledTime(), null));
		assertEquals(2, machine.queue.size());
		assertEquals(JEEventType.FINISHREQUEST, machine.nextFinishEvent.getType());
		assertEquals(0, machine.finishedRequests.size());
		assertEquals(ONE_MINUTE_IN_MILLIS * 20, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(ONE_MINUTE_IN_MILLIS * 40, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(ONE_MINUTE_IN_MILLIS * 10, machine.queue.get(0).getTotalToProcess());
		assertEquals(ONE_MINUTE_IN_MILLIS * 10, machine.queue.get(1).getTotalToProcess());
		
		//Requesting final processing
		machine.handleEvent(new JEEvent(JEEventType.FINISHREQUEST, machine, machine.nextFinishEvent.getScheduledTime(), null));
		assertEquals(0, machine.queue.size());
		assertEquals(2, machine.finishedRequests.size());
		assertNull(machine.nextFinishEvent);
		assertEquals(ONE_MINUTE_IN_MILLIS * 40, machine.lastProcessingEvaluation.timeMilliSeconds);
	}
	
	@Test
	public void computeUtilizationWithoutDemand(){
		assertEquals(0, machine.computeUtilization(0), 0.0);
		assertEquals(0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 10), 0.0);
	}
	
	@Test
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
		machine.queue.add(request);
		machine.queue.add(request);
		
		//No processing is requested. Evaluating utilization
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 10), 0.0);
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 20), 0.0);
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 30), 0.0);
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 40), 0.0);
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 90), 0.0);
	}
	
	@Test
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
		machine.handleEvent(new JEEvent(JEEventType.FINISHREQUEST, machine, new JETime(demand * 2), null));
		
		assertEquals(1.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 8), 0.0);//exactly at demand end time
		assertEquals(0.6, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 10), 0.0);
		assertEquals(0.2, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 12), 0.0);
		assertEquals(0.0, machine.computeUtilization(ONE_MINUTE_IN_MILLIS * 14), 0.0);
	}
}
