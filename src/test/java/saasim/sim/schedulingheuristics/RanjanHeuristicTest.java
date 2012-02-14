package saasim.sim.schedulingheuristics;

import static org.junit.Assert.*;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import saasim.cloud.MachineType;
import saasim.cloud.Request;
import saasim.config.Configuration;
import saasim.sim.components.Machine;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.components.TimeSharedMachine;
import saasim.sim.provisioningheuristics.MachineStatistics;
import saasim.util.ValidConfigurationTest;



public class RanjanHeuristicTest extends ValidConfigurationTest {

	private static final long ONE_MINUTE_IN_MILLIS = 1000 * 60;
	
	private RanjanHeuristic heuristic;
	
	@Before
	public void setUp() throws ConfigurationException{
		buildFullRanjanConfiguration();
		heuristic = new RanjanHeuristic();
	}
	
	@Test
	public void testConstruction(){
		MachineStatistics statistics = heuristic.getStatistics(0);
		assertEquals(0, statistics.requestArrivals);
		assertEquals(0, statistics.requestCompletions);
	}
	
	/**
	 * This test verifies that without machines available to be selected an error is thrown.
	 */
	@Test
	public void testGetServerWithoutMachines(){
		int userID = 1;
		long time = ONE_MINUTE_IN_MILLIS * 10;
		
		Request request = EasyMock.createMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(time).times(3);
		EasyMock.expect(request.getUserID()).andReturn(userID).times(2);
		EasyMock.replay(request);
		
		assertNull(heuristic.next(request));
		EasyMock.verify(request);
	}
	
	@Test
	public void testGetServerForFirstRequest(){
		int userID = 1;
		long time = ONE_MINUTE_IN_MILLIS * 1;
		
		Request request = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(time);
		EasyMock.expect(request.getUserID()).andReturn(userID);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(time);
		EasyMock.replay(request);
		
		Machine machine1 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		heuristic.addMachine(machine1);
		
		Machine nextServer = heuristic.next(request);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		assertEquals(1, heuristic.getStatistics(time).requestArrivals);
		
		EasyMock.verify(request);
	}
	
	/**
	 * This method verifies if the round-robin allocation is being done for a set of
	 * requests arriving from different users.
	 */
	@Test
	public void testGetServerForFirstRequestsWithMultipleServers(){
		int userID = 1;
		int secondUserID = 2;
		int thirdUserID = 3;
		int fourthUserID = 4;
		int fifthUserID = 5;
		int sixthUserID =  6;

		long time = ONE_MINUTE_IN_MILLIS * 1;
		
		Machine machine1 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(2, false, MachineType.M1_SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(3, false, MachineType.M1_SMALL, 0), null);
		Machine machine4 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(4, false, MachineType.M1_SMALL, 0), null);
		Machine machine5 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(5, false, MachineType.M1_SMALL, 0), null);
		heuristic.addMachine(machine1);		
		heuristic.addMachine(machine2);
		heuristic.addMachine(machine3);
		heuristic.addMachine(machine4);
		heuristic.addMachine(machine5);
		
		//First request
		Request request = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(time);
		EasyMock.expect(request.getUserID()).andReturn(userID);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(time);
		EasyMock.replay(request);
		Machine nextServer = heuristic.next(request);
		assertEquals(1, heuristic.getStatistics(time).requestArrivals);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request);
		
		//Second request
		Request request2 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request2.getArrivalTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request2.getUserID()).andReturn(secondUserID);
		EasyMock.replay(request2);
		nextServer = heuristic.next(request2);
		assertEquals(1, heuristic.getStatistics(time).requestArrivals);
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
		EasyMock.verify(request2);
		
		//Third request
		Request request3 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request3.getArrivalTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request3.getUserID()).andReturn(thirdUserID);
		EasyMock.replay(request3);
		nextServer = heuristic.next(request3);
		assertEquals(1, heuristic.getStatistics(time).requestArrivals);
		assertNotNull(nextServer);
		assertEquals(machine3, nextServer);
		EasyMock.verify(request3);
		
		//Fourth request
		Request request4 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request4.getArrivalTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request4.getUserID()).andReturn(fourthUserID);
		EasyMock.replay(request4);
		nextServer = heuristic.next(request4);
		assertEquals(1, heuristic.getStatistics(time).requestArrivals);
		assertNotNull(nextServer);
		assertEquals(machine4, nextServer);
		EasyMock.verify(request4);
		
		//Fifth request
		Request request5 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request5.getArrivalTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request5.getUserID()).andReturn(fifthUserID);
		EasyMock.replay(request5);
		nextServer = heuristic.next(request5);
		assertEquals(1, heuristic.getStatistics(time).requestArrivals);
		assertNotNull(nextServer);
		assertEquals(machine5, nextServer);
		EasyMock.verify(request5);
		
		//Restarting again
		Request request6 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request6.getArrivalTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request6.getUserID()).andReturn(sixthUserID);
		EasyMock.replay(request6);
		nextServer = heuristic.next(request6);
		assertEquals(1, heuristic.getStatistics(time).requestArrivals);
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
		int userID = 1;
		
		Machine machine1 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(2, false, MachineType.M1_SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(3, false, MachineType.M1_SMALL, 0), null);
		Machine machine4 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(4, false, MachineType.M1_SMALL, 0), null);
		Machine machine5 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(5, false, MachineType.M1_SMALL, 0), null);
		heuristic.addMachine(machine1);		
		heuristic.addMachine(machine2);
		heuristic.addMachine(machine3);
		heuristic.addMachine(machine4);
		heuristic.addMachine(machine5);
		
		//First request
		Request request = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 1).times(2);
		EasyMock.expect(request.getUserID()).andReturn(userID);
		EasyMock.replay(request);
		Machine nextServer = heuristic.next(request);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request);
		
		//Second request
		Request request2 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request2.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 3);
		EasyMock.expect(request2.getUserID()).andReturn(userID);
		EasyMock.replay(request2);
		nextServer = heuristic.next(request2);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request2);
		
		//Third request
		Request request3 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request3.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 8);
		EasyMock.expect(request3.getUserID()).andReturn(userID);
		EasyMock.replay(request3);
		nextServer = heuristic.next(request3);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request3);
		
		//Fourth request
		Request request4 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request4.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 17);
		EasyMock.expect(request4.getUserID()).andReturn(userID);
		EasyMock.replay(request4);
		nextServer = heuristic.next(request4);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request4);
		
		//Fifth request
		Request request5 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request5.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 30);
		EasyMock.expect(request5.getUserID()).andReturn(userID);
		EasyMock.replay(request5);
		nextServer = heuristic.next(request5);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request5);
		
		//Sixth request
		Request request6 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request6.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 45);
		EasyMock.expect(request6.getUserID()).andReturn(userID);
		EasyMock.replay(request6);
		nextServer = heuristic.next(request6);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request6);
		
		//Request arriving after session limit
		Request request7 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request7.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 61).times(2);
		EasyMock.expect(request7.getUserID()).andReturn(userID);
		EasyMock.replay(request7);
		nextServer = heuristic.next(request7);
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
		int userID = 999;
		int secondUserID = 888;
		
		Machine machine1 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(2, false, MachineType.M1_SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(3, false, MachineType.M1_SMALL, 0), null);
		Machine machine4 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(4, false, MachineType.M1_SMALL, 0), null);
		Machine machine5 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(5, false, MachineType.M1_SMALL, 0), null);
		heuristic.addMachine(machine1);		
		heuristic.addMachine(machine2);
		heuristic.addMachine(machine3);
		heuristic.addMachine(machine4);
		heuristic.addMachine(machine5);
		
		//First request
		Request request = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 1).times(2);
		EasyMock.expect(request.getUserID()).andReturn(userID);
		EasyMock.replay(request);
		Machine nextServer = heuristic.next(request);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request);
		
		//Second request
		Request request2 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request2.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 2).times(2);
		EasyMock.expect(request2.getUserID()).andReturn(secondUserID);
		EasyMock.replay(request2);
		nextServer = heuristic.next(request2);
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
		EasyMock.verify(request2);
		
		//Third request
		Request request3 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request3.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 5);
		EasyMock.expect(request3.getUserID()).andReturn(userID);
		EasyMock.replay(request3);
		nextServer = heuristic.next(request3);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request3);
		
		//Fourth request
		Request request4 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request4.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 10);
		EasyMock.expect(request4.getUserID()).andReturn(secondUserID);
		EasyMock.replay(request4);
		nextServer = heuristic.next(request4);
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
		EasyMock.verify(request4);
		
		//Requests after session limit
		Request request5 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request5.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 21).times(2);
		EasyMock.expect(request5.getUserID()).andReturn(userID);
		EasyMock.replay(request5);
		nextServer = heuristic.next(request5);
		assertNotNull(nextServer);
		assertEquals(machine3, nextServer);
		EasyMock.verify(request5);
		
		Request request6 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request6.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 26).times(2);
		EasyMock.expect(request6.getUserID()).andReturn(secondUserID);
		EasyMock.replay(request6);
		nextServer = heuristic.next(request6);
		assertNotNull(nextServer);
		assertEquals(machine4, nextServer);
		EasyMock.verify(request6);
		
		Request request7 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request7.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 37).times(2);
		EasyMock.expect(request7.getUserID()).andReturn(userID);
		EasyMock.replay(request7);
		nextServer = heuristic.next(request7);
		assertNotNull(nextServer);
		assertEquals(machine5, nextServer);
		EasyMock.verify(request7);
		
		Request request8 = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request8.getArrivalTimeInMillis()).andReturn(ONE_MINUTE_IN_MILLIS * 42).times(2);
		EasyMock.expect(request8.getUserID()).andReturn(secondUserID);
		EasyMock.replay(request8);
		nextServer = heuristic.next(request8);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		EasyMock.verify(request8);
	}

	@Test
	public void testResetCounters(){
		int userID = 100000;
		long time = ONE_MINUTE_IN_MILLIS * 1;
		
		Request request = EasyMock.createNiceMock(Request.class);
		EasyMock.expect(request.getArrivalTimeInMillis()).andReturn(time).times(2);
		EasyMock.expect(request.getUserID()).andReturn(userID);
		EasyMock.replay(request);
		
		Machine machine1 = new TimeSharedMachine(Configuration.getInstance().getScheduler(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		heuristic.addMachine(machine1);
		
		heuristic.next(request);
		assertEquals(1, heuristic.getStatistics(time).requestArrivals);
		
		EasyMock.verify(request);
		
		//Resetting counters
		MachineStatistics statistics = heuristic.getStatistics(time);
		assertEquals(0, statistics.requestArrivals);
		assertEquals(0, statistics.requestCompletions);
	}
}

