package commons.sim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import commons.cloud.Machine;
import commons.cloud.Request;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventType;


public class LoadBalancerTest {
	
	private LoadBalancer lb;

	@Before
	public void setUp(){
		lb = new LoadBalancer(new RanjanScheduler());
	}
	
	/**
	 * Scheduling a new request without machines does not throws any error
	 */
	@Test
	public void handleEventNewRequestWithoutMachines(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = 1000 * 60 * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = 1000 * 60 * 20;

		Request[] requests = new Request[1];
		requests[0] = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.expect(event.getType()).andReturn(JEEventType.NEWREQUEST).once();
		EasyMock.expect(event.getValue()).andReturn(requests).once();
		EasyMock.replay(event);
		
		lb.handleEvent(event);
		
		EasyMock.verify(event);
		
		assertNotNull(lb.servers);
		assertEquals(0, lb.servers.size());
	}
	
	/**
	 * Scheduling a new request without machines does not throws any error
	 */
	@Test
	public void handleEventNewRequestWithMachine(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = 1000 * 60 * 1;
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = 1000 * 60 * 20;

		Request[] requests = new Request[1];
		requests[0] = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		
		Machine machine = EasyMock.createStrictMock(Machine.class);
		lb.servers.add(machine);//Creating a machine to serve the request
		
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.expect(event.getType()).andReturn(JEEventType.NEWREQUEST).once();
		EasyMock.expect(event.getValue()).andReturn(requests).once();
		machine.sendRequest(requests[0]);
		EasyMock.expectLastCall().once();
		
		EasyMock.replay(event);
		EasyMock.replay(machine);
		
		lb.handleEvent(event);
		
		EasyMock.verify(event);
		EasyMock.verify(machine);
		
		assertNotNull(lb.servers);
		assertEquals(1, lb.servers.size());
	}
	
}
