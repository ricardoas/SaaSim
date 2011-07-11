package commons.cloud;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import commons.sim.jeevent.JEEventType;


public class MachineTest {
	
	private Machine machine;

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
		long time = 1000 * 60 * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = 1000 * 60 * 20;
		
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
		long time = 1000 * 60 * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = 1000 * 60 * 20;
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine.sendRequest(request);
		
		//Request arrives after another one and finishes after previous request
		long demand2 = 1000 * 60 * 30;
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
		long time = 1000 * 60 * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = 1000 * 60 * 20;
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine.sendRequest(request);
		
		//Request arrives after another one and finishes after previous request
		long demand2 = 1000 * 60 * 10;
		Request request2 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand2);
		machine.sendRequest(request2);
		
		assertEquals(2, machine.queue.size());
		assertEquals(JEEventType.FINISHREQUEST, machine.nextFinishEvent.getType());
		assertEquals(demand2, machine.nextFinishEvent.getScheduledTime().timeMilliSeconds);
		assertEquals(0, machine.lastProcessingEvaluation.timeMilliSeconds);
		assertEquals(0, machine.finishedRequests.size());
	}
}
