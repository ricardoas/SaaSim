package commons.sim.schedulingheuristics;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import util.CleanConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;
import commons.sim.components.TimeSharedMachine;
import commons.sim.jeevent.JEEventScheduler;

public class RoundRobinHeuristicTest extends CleanConfigurationTest {
	
	private RoundRobinHeuristic heuristic;
	
	@Before
	public void setUp(){
		this.heuristic = new RoundRobinHeuristic();
	}
	
	@Test(expected=ArithmeticException.class)
	public void testWithoutMachines(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		this.heuristic.getNextServer(request, new ArrayList<Machine>());
		
		EasyMock.verify(request);
	}
	
	@Test
	public void testWithOneMachine(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		servers.add(machine);
		
		//Retrieving for the first time
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Asking for another machine
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		EasyMock.verify(request);
	}
	
	@Test
	public void testWithMultipleMachines(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, MachineType.SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(3, false, MachineType.SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving for the first time
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Asking for another machine
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		//After using all machines, it starts all over again
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		EasyMock.verify(request);
	}
	
	@Test
	public void testWithMultipleMachinesAndFinishingFutureServer(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, MachineType.SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(3, false, MachineType.SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving for the first time
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Asking for another machine
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		//Removing server
		servers.remove(machine3);
		this.heuristic.finishServer(machine3, 2, servers);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
	}
	
	@Test
	public void testWithMultipleMachinesAndFinishingCurrentServerToUse(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, MachineType.SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(3, false, MachineType.SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving for the first time
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Asking for another machine
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		//Removing server
		servers.remove(machine);
		this.heuristic.finishServer(machine, 0, servers);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
	}
	
	@Test
	public void testWithMultipleMachinesAndFinishingPreviousUsedServer(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, MachineType.SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(3, false, MachineType.SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving for the first time
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Asking for another machine
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		servers.remove(machine);
		this.heuristic.finishServer(machine, 0, servers);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
	}
	
	@Test
	public void testGetArrivalCounter(){
		assertEquals(0, this.heuristic.getRequestsArrivalCounter());
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		servers.add(machine);
		
		//Retrieving for the first time
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		
		assertEquals(0, this.heuristic.getRequestsArrivalCounter());
	}
	
	@Test
	public void testReportRequestFinished(){
		assertEquals(0, this.heuristic.getFinishedRequestsCounter());
		
		this.heuristic.reportRequestFinished();
		
		assertEquals(0, this.heuristic.getFinishedRequestsCounter());
	}
	
	@Test
	public void testResetCounters(){
		assertEquals(0, this.heuristic.getFinishedRequestsCounter());
		assertEquals(0, this.heuristic.getRequestsArrivalCounter());
		
		this.heuristic.resetCounters();
		
		assertEquals(0, this.heuristic.getFinishedRequestsCounter());
		assertEquals(0, this.heuristic.getRequestsArrivalCounter());
	}

}
