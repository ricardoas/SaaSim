package commons.sim.schedulingheuristics;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.io.Checkpointer;
import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;
import commons.sim.components.TimeSharedMachine;

public class RoundRobinHeuristicTest extends ValidConfigurationTest {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullConfiguration();
	}
	
	
	@Test(expected=ArithmeticException.class)
	public void testWithoutMachines(){
		RoundRobinHeuristic heuristic = new RoundRobinHeuristic();
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		heuristic.getNextServer(request, new ArrayList<Machine>());
		
		EasyMock.verify(request);
	}
	
	@Test
	public void testWithOneMachine(){
		RoundRobinHeuristic heuristic = new RoundRobinHeuristic();
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		servers.add(machine);
		
		//Retrieving for the first time
		Machine nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Asking for another machine
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		EasyMock.verify(request);
	}
	
	@Test
	public void testWithMultipleMachines(){
		RoundRobinHeuristic heuristic = new RoundRobinHeuristic();
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(2, false, MachineType.M1_SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(3, false, MachineType.M1_SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving for the first time
		Machine nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Asking for another machine
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		//After using all machines, it starts all over again
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		EasyMock.verify(request);
	}
	
	@Test
	public void testWithMultipleMachinesAndFinishingFutureServer(){
		RoundRobinHeuristic heuristic = new RoundRobinHeuristic();
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(2, false, MachineType.M1_SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(3, false, MachineType.M1_SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving for the first time
		Machine nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Asking for another machine
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		//Removing server
		servers.remove(machine3);
		heuristic.finishServer(machine3, 2, servers);
		
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
	}
	
	@Test
	public void testWithMultipleMachinesAndFinishingCurrentServerToUse(){
		RoundRobinHeuristic heuristic = new RoundRobinHeuristic();
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(2, false, MachineType.M1_SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(3, false, MachineType.M1_SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving for the first time
		Machine nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Asking for another machine
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		//Removing server
		servers.remove(machine);
		heuristic.finishServer(machine, 0, servers);
		
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
	}
	
	@Test
	public void testWithMultipleMachinesAndFinishingPreviousUsedServer(){
		RoundRobinHeuristic heuristic = new RoundRobinHeuristic();
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(2, false, MachineType.M1_SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(3, false, MachineType.M1_SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving for the first time
		Machine nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Asking for another machine
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		servers.remove(machine);
		heuristic.finishServer(machine, 0, servers);
		
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		nextServer = heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
	}
	
	@Test
	public void testGetArrivalCounter(){
		RoundRobinHeuristic heuristic = new RoundRobinHeuristic();
		assertEquals(0, heuristic.getRequestsArrivalCounter());
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(Checkpointer.loadScheduler(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		servers.add(machine);
		
		//Retrieving for the first time
		Machine nextServer = heuristic.getNextServer(request, servers);
		
		assertEquals(0, heuristic.getRequestsArrivalCounter());
	}
	
	@Test
	public void testReportRequestFinished(){
		RoundRobinHeuristic heuristic = new RoundRobinHeuristic();
		assertEquals(0, heuristic.getFinishedRequestsCounter());
		
		heuristic.reportRequestFinished();
		
		assertEquals(0, heuristic.getFinishedRequestsCounter());
	}
	
	@Test
	public void testResetCounters(){
		RoundRobinHeuristic heuristic = new RoundRobinHeuristic();
		assertEquals(0, heuristic.getFinishedRequestsCounter());
		assertEquals(0, heuristic.getRequestsArrivalCounter());
		
		heuristic.resetCounters();
		
		assertEquals(0, heuristic.getFinishedRequestsCounter());
		assertEquals(0, heuristic.getRequestsArrivalCounter());
	}

}
