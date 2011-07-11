package commons.cloud;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventType;


public class MachineTest {
	
	private Machine machine;
	private long MILLIS = 1000 * 60;

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
		long time = MILLIS * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = MILLIS * 20;
		
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
		long time = MILLIS * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = MILLIS * 20;
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine.sendRequest(request);
		
		//Request arrives after another one and finishes after previous request
		long demand2 = MILLIS * 30;
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
		long time = MILLIS * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = MILLIS * 20;
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine.sendRequest(request);
		
		//Request arrives after another one and finishes equals to previous request
		long demand2 = MILLIS * 10;
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
		long time = MILLIS * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = MILLIS * 20;
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine.sendRequest(request);
		
		//Request arrives after another one and finishes before to previous request
		long demand2 = MILLIS * 2;
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
		long time = MILLIS * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = MILLIS * 20;
		
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
		assertEquals(MILLIS * 4, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(MILLIS * 22, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(MILLIS * 18, machine.queue.get(0).getTotalToProcess());
		
		//Requesting final processing
		machine.handleEvent(new JEEvent(JEEventType.FINISHREQUEST, machine, machine.nextFinishEvent.getScheduledTime(), null));
		assertEquals(0, machine.queue.size());
		assertEquals(2, machine.finishedRequests.size());
		assertNull(machine.nextFinishEvent);
		assertEquals(MILLIS * 22, machine.lastProcessingEvaluation.timeMilliSeconds);
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
		long time = MILLIS * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = MILLIS * 20;
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine.sendRequest(request);
		
		//Request arrives after another one and finishes equals to previous request
		long demand2 = MILLIS * 20;
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
		assertEquals(MILLIS * 20, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(MILLIS * 40, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(MILLIS * 10, machine.queue.get(0).getTotalToProcess());
		assertEquals(MILLIS * 10, machine.queue.get(1).getTotalToProcess());
		
		//Requesting final processing
		machine.handleEvent(new JEEvent(JEEventType.FINISHREQUEST, machine, machine.nextFinishEvent.getScheduledTime(), null));
		assertEquals(0, machine.queue.size());
		assertEquals(2, machine.finishedRequests.size());
		assertNull(machine.nextFinishEvent);
		assertEquals(MILLIS * 40, machine.lastProcessingEvaluation.timeMilliSeconds);
	}
}
