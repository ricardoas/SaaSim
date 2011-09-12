package commons.sim.schedulingheuristics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;
import commons.sim.components.TimeSharedMachine;
import commons.sim.jeevent.JEEventScheduler;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Configuration.class)
public class RoundRobinHeuristicForHeterogenousMachinesTest {
	
	private RoundRobinHeuristicForHeterogenousMachines heuristic;
	
	@Before
	public void setUp(){
		this.heuristic = new RoundRobinHeuristicForHeterogenousMachines();
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testWithoutMachines(){
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		PowerMock.replayAll(config);
		
		this.heuristic.getNextServer(request, new ArrayList<Machine>());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testWithOneMachine(){
		double machineRelativePower = 2;
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.replay(request);
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine = new TimeSharedMachine(new JEEventScheduler(), new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		servers.add(machine);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(4);
		EasyMock.expect(config.getRelativePower(MachineType.SMALL)).andReturn(machineRelativePower).anyTimes();
		PowerMock.replayAll(config);
		
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
		
		PowerMock.verifyAll();
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
		
		//Starting all over again!
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(nextServer, machine);
		
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
}
