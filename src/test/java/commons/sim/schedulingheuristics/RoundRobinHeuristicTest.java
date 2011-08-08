package commons.sim.schedulingheuristics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import commons.cloud.Request;
import commons.sim.components.ProcessorSharedMachine;
import commons.sim.jeevent.JEEventScheduler;

public class RoundRobinHeuristicTest {
	
	private RoundRobinHeuristic heuristic;
	
	@Before
	public void setUp(){
		this.heuristic = new RoundRobinHeuristic();
	}
	
	@Test
	public void testWithoutMachines(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		try{
			this.heuristic.getNextServer(request, new ArrayList<ProcessorSharedMachine>());
			fail("Error allocating request to empty set of machines!");
		}catch(ArithmeticException e){
		}
		
		EasyMock.verify(request);
	}
	
	@Test
	public void testWithOneMachine(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<ProcessorSharedMachine> servers = new ArrayList<ProcessorSharedMachine>();
		ProcessorSharedMachine machine = new ProcessorSharedMachine(new JEEventScheduler(), 1);
		servers.add(machine);
		
		//Retrieving for the first time
		ProcessorSharedMachine nextServer = this.heuristic.getNextServer(request, servers);
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
		
		ArrayList<ProcessorSharedMachine> servers = new ArrayList<ProcessorSharedMachine>();
		ProcessorSharedMachine machine = new ProcessorSharedMachine(new JEEventScheduler(), 1);
		ProcessorSharedMachine machine2 = new ProcessorSharedMachine(new JEEventScheduler(), 2);
		ProcessorSharedMachine machine3 = new ProcessorSharedMachine(new JEEventScheduler(), 3);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving for the first time
		ProcessorSharedMachine nextServer = this.heuristic.getNextServer(request, servers);
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
		
		EasyMock.verify(request);
	}

}
