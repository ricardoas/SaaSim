package commons.sim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import commons.cloud.Machine;
import commons.cloud.Request;


public class ProfitDrivenSchedulerTest {
	
	private double sla = 1000 * 50;//50 sec in millis
	private ProfitDrivenScheduler scheduler;
	
	@Before
	public void setUp(){
		this.scheduler = new ProfitDrivenScheduler(sla);
	}
	
	@Test
	public void testGetServerWithoutMachines(){
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
		Machine nextServer = scheduler.getNextServer(request, new ArrayList<Machine>());
		assertNull(nextServer);
	}
	
	/**
	 * Tests first request arriving in a certain machine
	 */
	@Test
	public void testGetServerForFirstRequest(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = 1000 * 60 * 1;//in millis
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = 1000 * 20;//in millis
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new Machine(1);
		servers.add(machine1);
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		Machine nextServer = scheduler.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
	}
	
	/**
	 * Verifies that first machine in the collection that matches the evaluation is the machine
	 * selected
	 */
	@Test
	public void testGetServerForFirstRequestAndSeveralMachines(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = 1000 * 60 * 1;//in millis
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = 1000 * 20;//in millis
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new Machine(1);
		Machine machine2 = new Machine(2);
		Machine machine3 = new Machine(3);
		servers.add(machine1);
		servers.add(machine2);
		servers.add(machine3);
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		Machine nextServer = scheduler.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		
		//Changing machines order
		servers.clear();
		machine1 = new Machine(1);
		machine2 = new Machine(2);
		machine3 = new Machine(3);
		servers.add(machine3);
		servers.add(machine2);
		servers.add(machine1);
		
		request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine3, nextServer);
	}

}
