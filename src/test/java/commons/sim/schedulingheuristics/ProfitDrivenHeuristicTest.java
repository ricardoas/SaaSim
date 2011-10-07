package commons.sim.schedulingheuristics;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
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
import commons.sim.util.SaaSAppProperties;

public class ProfitDrivenHeuristicTest extends ValidConfigurationTest {
	
	private final String CONFIG_FILE = "src/test/resources/config.properties";
	private double sla = 1000 * 50;//50 sec in millis
	private ProfitDrivenHeuristic heuristic;
	
	@Override
	public void setUp() throws ConfigurationException{
		super.setUp();
		Configuration.getInstance().setProperty(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME, this.sla);
		this.heuristic = new ProfitDrivenHeuristic();
	}
	
	@Override
	public String getConfigurationFile() {
		return CONFIG_FILE;
	}
	
	@Test
	public void testConstruction(){
		assertEquals(0, this.heuristic.getFinishedRequestsCounter());
		assertEquals(0, this.heuristic.getRequestsArrivalCounter());
	}
	
	@Test
	public void testGetServerWithoutMachines(){
		int clientID = 1;
		int userID = 1;
		long reqID = 1;
		long time = 1000 * 60 * 1;
		long size = 1024;
		long response = 1024;
		long [] demand = new long[]{1000 * 60 * 20};
		
		Request request = new Request(reqID, clientID, userID, time, size, response, demand);
		Machine nextServer = this.heuristic.getNextServer(request, new ArrayList<Machine>());
		assertNull(nextServer);
	}
	
	/**
	 * Tests first request arriving in a certain machine
	 */
	@Test
	public void testGetServerForFirstRequest(){
		int clientID = 1;
		int userID = 1;
		long reqID = 1;
		long time = 1000 * 60 * 1;
		long size = 1024;
		long response = 1024;
		long [] demand = new long[]{1000 * 20};
		
		JEEventScheduler eventScheduler = EasyMock.createMock(JEEventScheduler.class);
		EasyMock.expect(eventScheduler.registerHandler(EasyMock.isA(Machine.class))).andReturn(1);
		EasyMock.expect(eventScheduler.now()).andReturn(0L);
		EasyMock.replay(eventScheduler);
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new TimeSharedMachine(eventScheduler, new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		servers.add(machine1);
		
		Request request = new Request(reqID, clientID, userID, time, size, response, demand);
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
	}
	
	/**
	 * Verifies that first machine in the collection that matches the evaluation is the machine
	 * selected
	 */
	@Test
	public void testGetServerForFirstRequestAndSeveralMachines(){
		int clientID = 1;
		int userID = 1;
		long reqID = 1;
		long time = 1000 * 60 * 1;
		long size = 1024;
		long response = 1024;
		long [] demand = new long[]{1000 * 20};
		
		JEEventScheduler eventScheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(eventScheduler.registerHandler(EasyMock.isA(Machine.class))).andReturn(1);
		EasyMock.expect(eventScheduler.now()).andReturn(0L);
		EasyMock.expect(eventScheduler.registerHandler(EasyMock.isA(Machine.class))).andReturn(2);
		EasyMock.expect(eventScheduler.now()).andReturn(0L);
		EasyMock.expect(eventScheduler.registerHandler(EasyMock.isA(Machine.class))).andReturn(3);
		EasyMock.expect(eventScheduler.now()).andReturn(0L);
		EasyMock.replay(eventScheduler);
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new TimeSharedMachine(eventScheduler, new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(eventScheduler, new MachineDescriptor(2, false, MachineType.SMALL, 0), null);
		Machine machine3 = new TimeSharedMachine(eventScheduler, new MachineDescriptor(3, false, MachineType.SMALL, 0), null);
		servers.add(machine1);
		servers.add(machine2);
		servers.add(machine3);
		
		Request request = new Request(reqID, clientID, userID, time, size, response, demand);
		Machine nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine1, nextServer);
		
		//Changing machines order
		servers.clear();
		servers.add(machine3);
		servers.add(machine2);
		servers.add(machine1);
		
		request = new Request(reqID, clientID, userID, time, size, response, demand);
		nextServer = this.heuristic.getNextServer(request, servers);
		assertNotNull(nextServer);
		assertEquals(machine3, nextServer);
		
		EasyMock.verify(eventScheduler);
	}
	
	/**
	 * This scenario contains one machine that is in the limit of the SLA to attend requests and one
	 * machine that is completely free. As a new request arrives, the solution to attend the SLA for
	 * all requests is to schedule the new one in the second machine.
	 */
	@Test
	public void testGetServerWithBusyMachine(){
		double sla = 1000 * 20;
		Configuration.getInstance().setProperty(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME, sla);
		this.heuristic = new ProfitDrivenHeuristic();
		
		int clientID = 1;
		int userID = 1;
		long reqID = 1;
		long time = 1000 * 60 * 1;
		long size = 1024;
		long response = 1024;
		long [] demand = new long[]{1000 * 10};
		
		ArrayList<Machine> servers = new ArrayList<Machine>();
		JEEventScheduler eventScheduler = new JEEventScheduler();
		
		Machine machine1 = new TimeSharedMachine(eventScheduler, new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(eventScheduler, new MachineDescriptor(2, false, MachineType.SMALL, 0), null);
		servers.add(machine1);
		servers.add(machine2);
		
		Request request = new Request(reqID, clientID, userID, time, size, response, demand);
		Request request2 = new Request(reqID+1, clientID, userID, time, size, response, demand);
		machine1.sendRequest(request);
		machine1.sendRequest(request2);
		
		demand[0] = 1000 * 15;
		Request request3 = new Request(reqID, clientID, userID, time, size, response, demand);
		Machine nextServer = this.heuristic.getNextServer(request3, servers);
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
		Configuration.getInstance().setProperty(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME, sla);
		this.heuristic = new ProfitDrivenHeuristic();
		
		int clientID = 1;
		int userID = 1;
		long reqID = 1;
		long time = 1000 * 60 * 1;
		long size = 1024;
		long response = 1024;
		long [] demand = new long[]{1000 * 10};
		
		JEEventScheduler eventScheduler = new JEEventScheduler();
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new TimeSharedMachine(eventScheduler, new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(eventScheduler, new MachineDescriptor(2, false, MachineType.SMALL, 0), null);
		servers.add(machine1);
		servers.add(machine2);
		
		Request request = new Request(reqID, clientID, userID, time, size, response, demand);
		Request request2 = new Request(reqID, clientID, userID, time, size, response, demand);
		machine1.sendRequest(request);
		machine1.sendRequest(request2);
		
		Request request3 = new Request(reqID, clientID, userID, time, size, response, demand);
		Request request4 = new Request(reqID, clientID, userID, time, size, response, demand);
		machine2.sendRequest(request3);
		machine2.sendRequest(request4);
		
		demand[0] = 1000 * 15;//in millis
		Request request5 = new Request(reqID, clientID, userID, time, size, response, demand);
		Machine nextServer = this.heuristic.getNextServer(request5, servers);
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
		Configuration.getInstance().setProperty(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME, sla);
		this.heuristic = new ProfitDrivenHeuristic();
		
		int clientID = 1;
		int userID = 1;
		long reqID = 1;
		long time = 1000 * 60 * 1;
		long size = 1024;
		long response = 1024;
		long [] demand = new long[]{1000 * 15};
		
		JEEventScheduler eventScheduler = new JEEventScheduler();
		ArrayList<Machine> servers = new ArrayList<Machine>();
		Machine machine1 = new TimeSharedMachine(eventScheduler, new MachineDescriptor(1, false, MachineType.SMALL, 0), null);
		Machine machine2 = new TimeSharedMachine(eventScheduler, new MachineDescriptor(2, false, MachineType.SMALL, 0), null);
		servers.add(machine1);
		servers.add(machine2);
		
		Request request = new Request(reqID, clientID, userID, time, size, response, demand);
		machine1.sendRequest(request);
		
		Request request3 = new Request(reqID, clientID, userID, time, size, response, demand);
		machine2.sendRequest(request3);
		
		Request request5 = new Request(reqID, clientID, userID, time, size, response, demand);
		Machine nextServer = this.heuristic.getNextServer(request5, servers);
		assertNull(nextServer);
	}
}
