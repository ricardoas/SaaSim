package commons.sim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import commons.cloud.Machine;
import commons.cloud.Request;
import commons.sim.jeevent.JEEventScheduler;


public class ProfitDrivenSchedulerTest {
	
	private double sla = 1000 * 50;//50 sec in millis
	private ProfitDrivenScheduler scheduler;
	private JEEventScheduler eventScheduler;
	
	@Before
	public void setUp(){
		this.eventScheduler = new JEEventScheduler();
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
		Machine machine1 = new Machine(eventScheduler, 1);
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
		Machine machine1 = new Machine(eventScheduler, 1);
		Machine machine2 = new Machine(eventScheduler, 2);
		Machine machine3 = new Machine(eventScheduler, 3);
		servers.add(machine1);
		servers.add(machine2);
		servers.add(machine3);
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		Machine nextServer = scheduler.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		
		//Changing machines order
		servers.clear();
		machine1 = new Machine(eventScheduler, 1);
		machine2 = new Machine(eventScheduler, 2);
		machine3 = new Machine(eventScheduler, 3);
		servers.add(machine3);
		servers.add(machine2);
		servers.add(machine1);
		
		request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		nextServer = scheduler.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine3, nextServer);
	}
	
	/**
	 * This scenario contains one machine that is in the limit of the SLA to attend requests and one
	 * machine that is completely free. As a new request arrives, the solution to attend the SLA for
	 * all requests is to schedule the new one in the second machine.
	 */
	@Test
	public void testGetServerWithBusyMachine(){
		double sla = 1000 * 20;
		this.scheduler = new ProfitDrivenScheduler(sla);
		
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = 1000 * 60 * 1;//in millis
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = 1000 * 10;//in millis
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new Machine(eventScheduler, 1);
		Machine machine2 = new Machine(eventScheduler, 2);
		servers.add(machine1);
		servers.add(machine2);
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		Request request2 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine1.sendRequest(request);
		machine1.sendRequest(request2);
		
		demand = 1000 * 15;//in millis
		Request request3 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		Machine nextServer = scheduler.getNextServer(request3, servers);
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
	}
	
	/**
	 * This scenario contains two machines that are in the limit of the SLA to attend requests.
	 * As a new request arrives, the solution to attend the SLA for all requests is to request
	 * a new machine for the new request.
	 */
	@Test
	public void testGetServerWithBusyMachine2(){
		double sla = 1000 * 20;
		this.scheduler = new ProfitDrivenScheduler(sla);
		
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = 1000 * 60 * 1;//in millis
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = 1000 * 10;//in millis
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new Machine(eventScheduler, 1);
		Machine machine2 = new Machine(eventScheduler, 2);
		servers.add(machine1);
		servers.add(machine2);
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		Request request2 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine1.sendRequest(request);
		machine1.sendRequest(request2);
		
		Request request3 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		Request request4 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine2.sendRequest(request3);
		machine2.sendRequest(request4);
		
		demand = 1000 * 15;//in millis
		Request request5 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		Machine nextServer = scheduler.getNextServer(request5, servers);
		assertNull(nextServer);
	}
	
	/**
	 * This scenario contains two machines in the limit of the SLA to attend requests.
	 * As a new request arrives, the solution to attend the SLA for all requests is to request
	 * a new machine for the new request.
	 */
	@Test
	public void testGetServerWithBusyMachine3(){
		double sla = 1000 * 20;
		this.scheduler = new ProfitDrivenScheduler(sla);
		
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = 1000 * 60 * 1;//in millis
		long size = 1024;
		boolean hasExpired = false;
		String URL = "";
		String httpOperation = "GET";
		long demand = 1000 * 15;//in millis
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new Machine(eventScheduler, 1);
		Machine machine2 = new Machine(eventScheduler, 2);
		servers.add(machine1);
		servers.add(machine2);
		
		Request request = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine1.sendRequest(request);
		
		Request request3 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		machine2.sendRequest(request3);
		
		Request request5 = new Request(clientID, userID, reqID, time, size, hasExpired, httpOperation, URL, demand);
		Machine nextServer = scheduler.getNextServer(request5, servers);
		assertNull(nextServer);
	}
}
