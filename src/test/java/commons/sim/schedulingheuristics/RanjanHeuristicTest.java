package commons.sim.schedulingheuristics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import commons.cloud.Request;
import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;
import commons.sim.components.TimeSharedMachine;
import commons.sim.jeevent.JEEventScheduler;


public class RanjanHeuristicTest {

	private static final long ONE_MINUTE_IN_MILLIS = 1000 * 60;
	
	private RanjanHeuristic heuristic;
	
	@Before
	public void setUp(){
		heuristic = new RanjanHeuristic();
	}
	
	@Test
	public void testConstruction(){
		assertEquals(0, this.heuristic.getRequestsArrivalCounter());
		assertEquals(0, this.heuristic.getFinishedRequestsCounter());
	}
	
	/**
	 * This test verifies that without machines available to be selected an error is thrown.
	 */
	@Test
	public void testGetServerWithoutMachines(){
		String userID = "u1";
		long time = ONE_MINUTE_IN_MILLIS * 10;
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getTimeInMillis()).andReturn(time).once();
		EasyMock.expect(request.getUserID()).andReturn(userID).once();
		EasyMock.replay(request);
		
		try{
			heuristic.getNextServer(request, new ArrayList<Machine>());
			fail("Error while selecting inexistent servers!");
		}catch(ArithmeticException e){
		}
		EasyMock.verify(request);
	}
	
	@Test
	public void testGetServerForFirstRequest(){
		String userID = "u1";
		long time = ONE_MINUTE_IN_MILLIS * 1;
		
		Request request = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request.getTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request.getUserID()).andReturn(userID).times(2);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, 0), null);
		servers.add(machine1);
		
		Machine nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		assertEquals(1, heuristic.getRequestsArrivalCounter());
		
		EasyMock.verify(request);
	}
	
	/**
	 * This method verifies if the round-robin allocation is being done for a set of
	 * requests arriving from different users.
	 */
	@Test
	public void testGetServerForFirstRequestsWithMultipleServers(){
		String userID = "u1";
		String secondUserID = "u2";
		String thirdUserID = "u3";
		String fourthUserID = "u4";
		String fifthUserID = "u5";
		String sixthUserID = "u6";

		long time = ONE_MINUTE_IN_MILLIS * 1;
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, 0), null);
		Machine machine3 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(3, false, 0), null);
		Machine machine4 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(4, false, 0), null);
		Machine machine5 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(5, false, 0), null);
		servers.add(machine1);
		servers.add(machine2);
		servers.add(machine3);
		servers.add(machine4);
		servers.add(machine5);
		
		//First request
		Request request = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request.getTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request.getUserID()).andReturn(userID).times(2);
		EasyMock.replay(request);
		Machine nextServer = heuristic.getNextServer(request, servers);
		assertEquals(1, heuristic.getRequestsArrivalCounter());
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request);
		
		//Second request
		Request request2 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request2.getTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request2.getUserID()).andReturn(secondUserID).times(2);
		EasyMock.replay(request2);
		nextServer = heuristic.getNextServer(request2, servers);
		assertEquals(2, heuristic.getRequestsArrivalCounter());
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
		EasyMock.verify(request2);
		
		//Third request
		Request request3 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request3.getTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request3.getUserID()).andReturn(thirdUserID).times(2);
		EasyMock.replay(request3);
		nextServer = heuristic.getNextServer(request3, servers);
		assertEquals(3, heuristic.getRequestsArrivalCounter());
		assertNotNull(nextServer);
		assertEquals(machine3, nextServer);
		EasyMock.verify(request3);
		
		//Fourth request
		Request request4 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request4.getTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request4.getUserID()).andReturn(fourthUserID).times(2);
		EasyMock.replay(request4);
		nextServer = heuristic.getNextServer(request4, servers);
		assertEquals(4, heuristic.getRequestsArrivalCounter());
		assertNotNull(nextServer);
		assertEquals(machine4, nextServer);
		EasyMock.verify(request4);
		
		//Fifth request
		Request request5 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request5.getTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request5.getUserID()).andReturn(fifthUserID).times(2);
		EasyMock.replay(request5);
		nextServer = heuristic.getNextServer(request5, servers);
		assertEquals(5, heuristic.getRequestsArrivalCounter());
		assertNotNull(nextServer);
		assertEquals(machine5, nextServer);
		EasyMock.verify(request5);
		
		//Restarting again
		Request request6 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request6.getTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request6.getUserID()).andReturn(sixthUserID).times(2);
		EasyMock.replay(request6);
		nextServer = heuristic.getNextServer(request6, servers);
		assertEquals(6, heuristic.getRequestsArrivalCounter());
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request6);
	}
	
	/**
	 * This method verifies that requests arriving from the same user during a SESSION interval
	 * are redirected to the same server
	 */
	@Test
	public void testGetServerForFirstRequestsWithMultipleServers2(){
		String userID = "u1";
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, 0), null);
		Machine machine3 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(3, false, 0), null);
		Machine machine4 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(4, false, 0), null);
		Machine machine5 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(5, false, 0), null);
		servers.add(machine1);
		servers.add(machine2);
		servers.add(machine3);
		servers.add(machine4);
		servers.add(machine5);
		
		//First request
		Request request = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 1).times(2);
		EasyMock.expect(request.getUserID()).andReturn(userID).times(2);
		EasyMock.replay(request);
		Machine nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request);
		
		//Second request
		Request request2 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request2.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 3);
		EasyMock.expect(request2.getUserID()).andReturn(userID);
		EasyMock.replay(request2);
		nextServer = heuristic.getNextServer(request2, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request2);
		
		//Third request
		Request request3 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request3.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 8);
		EasyMock.expect(request3.getUserID()).andReturn(userID);
		EasyMock.replay(request3);
		nextServer = heuristic.getNextServer(request3, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request3);
		
		//Fourth request
		Request request4 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request4.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 17);
		EasyMock.expect(request4.getUserID()).andReturn(userID);
		EasyMock.replay(request4);
		nextServer = heuristic.getNextServer(request4, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request4);
		
		//Fifth request
		Request request5 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request5.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 30);
		EasyMock.expect(request5.getUserID()).andReturn(userID);
		EasyMock.replay(request5);
		nextServer = heuristic.getNextServer(request5, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request5);
		
		//Sixth request
		Request request6 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request6.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 45);
		EasyMock.expect(request6.getUserID()).andReturn(userID);
		EasyMock.replay(request6);
		nextServer = heuristic.getNextServer(request6, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request6);
		
		//Request arriving after session limit
		Request request7 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request7.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 61).times(2);
		EasyMock.expect(request7.getUserID()).andReturn(userID).times(2);
		EasyMock.replay(request7);
		nextServer = heuristic.getNextServer(request7, servers);
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
		EasyMock.verify(request7);
	}
	
	/**
	 * This method verifies that requests arriving from the different users during a SESSION intervalo
	 * are redirected to the same server
	 */
	@Test
	public void testGetServerForFirstRequestsWithMultipleServers3(){
		String userID = "u1";
		String secondUserID = "u2";
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, 0), null);
		Machine machine3 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(3, false, 0), null);
		Machine machine4 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(4, false, 0), null);
		Machine machine5 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(5, false, 0), null);
		servers.add(machine1);
		servers.add(machine2);
		servers.add(machine3);
		servers.add(machine4);
		servers.add(machine5);
		
		//First request
		Request request = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 1).times(2);
		EasyMock.expect(request.getUserID()).andReturn(userID).times(2);
		EasyMock.replay(request);
		Machine nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request);
		
		//Second request
		Request request2 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request2.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 2).times(2);
		EasyMock.expect(request2.getUserID()).andReturn(secondUserID).times(2);
		EasyMock.replay(request2);
		nextServer = heuristic.getNextServer(request2, servers);
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
		EasyMock.verify(request2);
		
		//Third request
		Request request3 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request3.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 5);
		EasyMock.expect(request3.getUserID()).andReturn(userID);
		EasyMock.replay(request3);
		nextServer = heuristic.getNextServer(request3, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request3);
		
		//Fourth request
		Request request4 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request4.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 10);
		EasyMock.expect(request4.getUserID()).andReturn(secondUserID);
		EasyMock.replay(request4);
		nextServer = heuristic.getNextServer(request4, servers);
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
		EasyMock.verify(request4);
		
		//Requests after session limit
		Request request5 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request5.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 21).times(2);
		EasyMock.expect(request5.getUserID()).andReturn(userID).times(2);
		EasyMock.replay(request5);
		nextServer = heuristic.getNextServer(request5, servers);
		assertNotNull(nextServer);
		assertEquals(machine3, nextServer);
		EasyMock.verify(request5);
		
		Request request6 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request6.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 26).times(2);
		EasyMock.expect(request6.getUserID()).andReturn(secondUserID).times(2);
		EasyMock.replay(request6);
		nextServer = heuristic.getNextServer(request6, servers);
		assertNotNull(nextServer);
		assertEquals(machine4, nextServer);
		EasyMock.verify(request6);
		
		Request request7 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request7.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 37).times(2);
		EasyMock.expect(request7.getUserID()).andReturn(userID).times(2);
		EasyMock.replay(request7);
		nextServer = heuristic.getNextServer(request7, servers);
		assertNotNull(nextServer);
		assertEquals(machine5, nextServer);
		EasyMock.verify(request7);
		
		Request request8 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request8.getTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 42).times(2);
		EasyMock.expect(request8.getUserID()).andReturn(secondUserID).times(2);
		EasyMock.replay(request8);
		nextServer = heuristic.getNextServer(request8, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request8);
	}

	@Test
	public void testResetCounters(){
		String userID = "u1";
		long time = ONE_MINUTE_IN_MILLIS * 1;
		
		Request request = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request.getTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request.getUserID()).andReturn(userID).times(2);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, 0), null);
		servers.add(machine1);
		
		heuristic.getNextServer(request, servers);
		assertEquals(1, heuristic.getRequestsArrivalCounter());
		
		EasyMock.verify(request);
		
		//Resetting counters
		heuristic.resetCounters();
		assertEquals(0, heuristic.getRequestsArrivalCounter());
		assertEquals(0, heuristic.getFinishedRequestsCounter());
	}
}

