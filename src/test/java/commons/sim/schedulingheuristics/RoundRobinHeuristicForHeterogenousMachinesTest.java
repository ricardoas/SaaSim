package commons.sim.schedulingheuristics;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import util.ValidConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;
import commons.sim.components.TimeSharedMachine;
import commons.sim.jeevent.JEEventScheduler;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Configuration.class)
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
	public void testWithMultipleMachinesWithSamePower(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, MachineType.SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(3, false, MachineType.SMALL, 0), null);
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
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, MachineType.SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(3, false, MachineType.SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		double machineRelativePower = 1;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getRelativePower(MachineType.SMALL)).andReturn(machineRelativePower).times(4);
		PowerMock.replayAll(config);
		
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
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testWithMultipleMachinesWithSamePowerAndFinishingPreviousServer(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, MachineType.SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(3, false, MachineType.SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		double machineRelativePower = 1;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getRelativePower(MachineType.SMALL)).andReturn(machineRelativePower).times(4);
		PowerMock.replayAll(config);
		
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
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testWithMultipleMachinesWithSamePowerAndFinishingPreviousServer2(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, MachineType.SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(3, false, MachineType.SMALL, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		double machineRelativePower = 1;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(5);
		EasyMock.expect(config.getRelativePower(MachineType.SMALL)).andReturn(machineRelativePower).times(5);
		PowerMock.replayAll(config);
		
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
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testWithMultipleMachinesWithDifferentPower(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, MachineType.MEDIUM, 0), null);
		Machine machine3 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(3, false, MachineType.LARGE, 0), null);
		servers.add(machine);
		servers.add(machine2);
		servers.add(machine3);
		
		double machineRelativePower = 1;
		double machineRelativePower2 = 2;
		double machineRelativePower3 = 4;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(10);
		EasyMock.expect(config.getRelativePower(MachineType.SMALL)).andReturn(machineRelativePower);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(machineRelativePower2).times(2);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(machineRelativePower3).times(4);
		EasyMock.expect(config.getRelativePower(MachineType.SMALL)).andReturn(machineRelativePower);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(machineRelativePower2).times(2);
		PowerMock.replayAll(config);
		
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
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testWithMultipleMachinesWithDifferentTypesButSamePower(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.HIGHCPU, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, MachineType.XLARGE, 0), null);
		servers.add(machine);
		servers.add(machine2);
		
		double machineRelativePower = 3;
		double machineRelativePower2 = 3;
	
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(9);
		EasyMock.expect(config.getRelativePower(MachineType.HIGHCPU)).andReturn(machineRelativePower).times(3);
		EasyMock.expect(config.getRelativePower(MachineType.XLARGE)).andReturn(machineRelativePower2).times(3);
		EasyMock.expect(config.getRelativePower(MachineType.HIGHCPU)).andReturn(machineRelativePower).times(3);
		PowerMock.replayAll(config);
		
		//Retrieving the first machine
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		nextServer = this.heuristic.getNextServer(request, servers);
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
		
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testWithMultipleMachinesAndDifferentDoublePowers(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.MEDIUM, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, MachineType.LARGE, 0), null);
		servers.add(machine);
		servers.add(machine2);
		
		double machineRelativePower = 1.3333;
		double machineRelativePower2 = 3;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(7);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(machineRelativePower).times(2);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(machineRelativePower2).times(3);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(machineRelativePower).times(2);
		PowerMock.replayAll(config);
		
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
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testWithMultipleMachinesAndDifferentDoublePowersAndFinishedServers(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.MEDIUM, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, MachineType.LARGE, 0), null);
		servers.add(machine);
		servers.add(machine2);
		
		double machineRelativePower = 1.3333;
		double machineRelativePower2 = 3;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(7);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(machineRelativePower).times(2);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(machineRelativePower2).times(5);
		PowerMock.replayAll(config);
		
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
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testWithMultipleMachinesAndEquivalentDoublePowers(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.MEDIUM, 0), null);
		Machine machine2 = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(2, false, MachineType.LARGE, 0), null);
		servers.add(machine);
		servers.add(machine2);
		
		double machineRelativePower = 1.3333;
		double machineRelativePower2 = 2;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(7);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(machineRelativePower).times(2);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(machineRelativePower2).times(2);
		EasyMock.expect(config.getRelativePower(MachineType.MEDIUM)).andReturn(machineRelativePower).times(2);
		EasyMock.expect(config.getRelativePower(MachineType.LARGE)).andReturn(machineRelativePower2);
		PowerMock.replayAll(config);
		
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
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testGetArrivalCounter(){
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getRelativePower(MachineType.SMALL)).andReturn(1d);
		PowerMock.replayAll(config);
		
		assertEquals(0, this.heuristic.getRequestsArrivalCounter());
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		servers.add(machine);
		
		//Retrieving for the first time
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		
		assertEquals(0, this.heuristic.getRequestsArrivalCounter());
		
		PowerMock.verifyAll();
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
