package commons.sim;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import commons.cloud.Machine;
import commons.cloud.Request;


public class RanjanSchedulerTest {
	
	private static final int MILLIS = 1000 * 60;
	private static final String URL = null;
	private RanjanScheduler scheduler;

	@Before
	public void setUp(){
		scheduler = new RanjanScheduler();
	}
	
	@Test
	public void testGetServerWithoutMachines(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = 1000 * 60 * 1;
		long size = 1024;
		boolean hasExpired = false;
		String httpOperation = "GET";
		long demand = 1000 * 60 * 20;
		
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		Machine nextServer = scheduler.getNextServer(request, new ArrayList<Machine>());
		assertNull(nextServer);
	}
	
	@Test
	public void testGetServerForFirstRequest(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = 1000 * 60 * 1;
		long size = 1024;
		boolean hasExpired = false;
		String httpOperation = "GET";
		long demand = 1000 * 60 * 20;
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new Machine(1);
		servers.add(machine1);
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		Machine nextServer = scheduler.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
	}
	
	/**
	 * This method verifies if the round-robin allocation is being done for a set of
	 * requests arriving from different users.
	 */
	@Test
	public void testGetServerForFirstRequestsWithMultipleServers(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = 1000 * 60 * 1;
		long size = 1024;
		boolean hasExpired = false;
		String httpOperation = "GET";
		long demand = 1000 * 60 * 20;
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new Machine(1);
		Machine machine2 = new Machine(2);
		Machine machine3 = new Machine(3);
		Machine machine4 = new Machine(4);
		Machine machine5 = new Machine(5);
		servers.add(machine1);
		servers.add(machine2);
		servers.add(machine3);
		servers.add(machine4);
		servers.add(machine5);
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		Machine nextServer = scheduler.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		
		Request request2 = new Request(clientID, "u2", reqID, time, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request2, servers);
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
		
		Request request3 = new Request(clientID, "u3", reqID, time, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request3, servers);
		assertNotNull(nextServer);
		assertEquals(machine3, nextServer);
		
		Request request4 = new Request(clientID, "u4", reqID, time, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request4, servers);
		assertNotNull(nextServer);
		assertEquals(machine4, nextServer);
		
		Request request5 = new Request(clientID, "u5", reqID, time, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request5, servers);
		assertNotNull(nextServer);
		assertEquals(machine5, nextServer);
		
		//Restarting again
		Request request6 = new Request(clientID, "u6", reqID, time, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request6, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
	}
	
	/**
	 * This method verifies that requests arriving from the same user during a SESSION intervalo
	 * are redirected to the same server
	 */
	@Test
	public void testGetServerForFirstRequestsWithMultipleServers2(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long size = 1024;
		boolean hasExpired = false;
		String httpOperation = "GET";
		long demand = 1000 * 60 * 20;
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new Machine(1);
		Machine machine2 = new Machine(2);
		Machine machine3 = new Machine(3);
		Machine machine4 = new Machine(4);
		Machine machine5 = new Machine(5);
		servers.add(machine1);
		servers.add(machine2);
		servers.add(machine3);
		servers.add(machine4);
		servers.add(machine5);
		
		Request request = new Request(clientID, userID, reqID, MILLIS * 1, size, hasExpired, httpOperation, URL, demand);
		Machine nextServer = scheduler.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		
		Request request2 = new Request(clientID, userID, reqID, MILLIS * 3, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request2, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		
		Request request3 = new Request(clientID, userID, reqID, MILLIS * 8, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request3, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		
		Request request4 = new Request(clientID, userID, reqID, MILLIS * 17, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request4, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		
		Request request5 = new Request(clientID, userID, reqID, MILLIS * 30, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request5, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		
		Request request6 = new Request(clientID, userID, reqID, MILLIS * 45, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request6, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		
		//Request arriving after session limit
		Request request7 = new Request(clientID, userID, reqID, MILLIS * 61, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request7, servers);
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
	}
	
	/**
	 * This method verifies that requests arriving from the different users during a SESSION intervalo
	 * are redirected to the same server
	 */
	@Test
	public void testGetServerForFirstRequestsWithMultipleServers3(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long size = 1024;
		boolean hasExpired = false;
		String httpOperation = "GET";
		long demand = 1000 * 60 * 20;
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new Machine(1);
		Machine machine2 = new Machine(2);
		Machine machine3 = new Machine(3);
		Machine machine4 = new Machine(4);
		Machine machine5 = new Machine(5);
		servers.add(machine1);
		servers.add(machine2);
		servers.add(machine3);
		servers.add(machine4);
		servers.add(machine5);
		
		Request request = new Request(clientID, userID, reqID, MILLIS * 1, size, hasExpired, httpOperation, URL, demand);
		Machine nextServer = scheduler.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		
		Request request2 = new Request(clientID, "u2", reqID, MILLIS * 2, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request2, servers);
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
		
		Request request3 = new Request(clientID, userID, reqID, MILLIS * 5, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request3, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		
		Request request4 = new Request(clientID, "u2", reqID, MILLIS * 10, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request4, servers);
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
		
		//Requests after session limit
		Request request5 = new Request(clientID, userID, reqID, MILLIS * 21, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request5, servers);
		assertNotNull(nextServer);
		assertEquals(machine3, nextServer);
		
		Request request6 = new Request(clientID, "u2", reqID, MILLIS * 26, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request6, servers);
		assertNotNull(nextServer);
		assertEquals(machine4, nextServer);
		
		Request request7 = new Request(clientID, userID, reqID, MILLIS * 37, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request7, servers);
		assertNotNull(nextServer);
		assertEquals(machine5, nextServer);
		
		Request request8 = new Request(clientID, "u2", reqID, MILLIS * 42, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request8, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
	}
}
