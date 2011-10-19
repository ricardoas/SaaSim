package commons.sim.schedulingheuristics;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Test;

import util.ValidConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;
import commons.sim.components.TimeSharedMachine;
import commons.sim.jeevent.JEEventScheduler;

public class RoundRobinHeuristicForHeterogenousMachinesTest extends ValidConfigurationTest {
	
	private RoundRobinHeuristicForHeterogenousMachines heuristic;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		buildFullConfiguration();
		this.heuristic = new RoundRobinHeuristicForHeterogenousMachines();
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
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
		Machine machine = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
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
	public void testWithMultipleMachinesWithSamePower(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(2, false, MachineType.M1_SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(3, false, MachineType.M1_SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving the first machine
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Since first machine power is one, now the second machine should be retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		//After using all power of second machine, third machine is retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		//Starting all over again!
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		EasyMock.verify(request);
	}
	
	@Test
	public void testWithMultipleMachinesWithSamePowerAndFinishingLastServer(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(2, false, MachineType.M1_SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(3, false, MachineType.M1_SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving the first machine
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Since first machine power is one, now the second machine should be retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		//After using all power of second machine, third machine is retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		servers.remove(machine3);
		this.heuristic.finishServer(machine3, 2, servers);
		
		//Starting all over again!
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
	}
	
	@Test
	public void testWithMultipleMachinesWithSamePowerAndFinishingPreviousServer(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(2, false, MachineType.M1_SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(3, false, MachineType.M1_SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving the first machine
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Since first machine power is one, now the second machine should be retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		//After using all power of second machine, third machine is retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		//Removing server
		servers.remove(machine);
		this.heuristic.finishServer(machine, 0, servers);
		
		//Starting all over again!
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
	}
	
	@Test
	public void testWithMultipleMachinesWithSamePowerAndFinishingPreviousServer2(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(2, false, MachineType.M1_SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(3, false, MachineType.M1_SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving the first machine
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Since first machine power is one, now the second machine should be retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		//After using all power of second machine, third machine is retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		//Starting all over again!
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine, nextServer);

		//Removing server
		servers.remove(machine);
		this.heuristic.finishServer(machine, 0, servers);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine2, nextServer);
	}
	
	@Test
	public void testWithMultipleMachinesWithDifferentPower(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(2, false, MachineType.C1_MEDIUM, 0), null);
		Machine machine3 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(3, false, MachineType.M1_XLARGE, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		//Retrieving the first machine
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Since first machine power is one, now the second machine should be retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		//After using all power of second machine, third machine is retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine3);
		
		//Starting all over again!
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
	}
	
	@Test
	public void testWithMultipleMachinesWithDifferentTypesButSamePower(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.C1_MEDIUM, 0), null);
		Machine machine2 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(2, false, MachineType.M1_LARGE, 0), null);
		servers.add(machine);
		servers.add(machine2);
		
		//Retrieving the first machine
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Since first machine power is one, now the second machine should be retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		//Starting all over again!
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
	}
	
	@Test
	public void testWithMultipleMachinesAndDifferentDoublePowers(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.C1_MEDIUM, 0), null);
		Machine machine2 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(2, false, MachineType.M1_LARGE, 0), null);
		servers.add(machine);
		servers.add(machine2);
		
		double machineRelativePower = 1.3333;
		double machineRelativePower2 = 3;
		
		//Retrieving the first machine
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Since first machine power is one, now the second machine should be retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		//Starting all over again!
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
	}
	
	@Test
	public void testWithMultipleMachinesAndDifferentDoublePowersAndFinishedServers(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.C1_MEDIUM, 0), null);
		Machine machine2 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(2, false, MachineType.M1_LARGE, 0), null);
		servers.add(machine);
		servers.add(machine2);
		
		double machineRelativePower = 1.3333;
		double machineRelativePower2 = 3;
		
		//Retrieving the first machine
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Since first machine power is one, now the second machine should be retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		//Removing server
		servers.remove(machine);
		this.heuristic.finishServer(machine, 0, servers);
		
		//Starting all over again!
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
	}
	
	@Test
	public void testWithMultipleMachinesAndEquivalentDoublePowers(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.C1_MEDIUM, 0), null);
		Machine machine2 = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(2, false, MachineType.M1_LARGE, 0), null);
		servers.add(machine);
		servers.add(machine2);
		
		double machineRelativePower = 1.3333;
		double machineRelativePower2 = 2;
		
		//Retrieving the first machine
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		//Since first machine power is one, now the second machine should be retrieved
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine2);
		
		//Starting all over again!
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
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
		Machine machine = new TimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), null);
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
