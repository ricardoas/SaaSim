package commons.sim.components;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import provisioning.DPS;

import commons.cloud.Request;
import commons.config.SimulatorConfiguration;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.schedulingheuristics.ProfitDrivenHeuristic;
import commons.sim.schedulingheuristics.SchedulingHeuristic;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SimulatorConfiguration.class)
public class LoadBalancerTest {
	
	private static final long ONE_MINUTE_IN_MILLIS = 1000 * 60l;
	private LoadBalancer lb;
	private JEEventScheduler eventScheduler;
	private SchedulingHeuristic schedulingHeuristic;

	@Before
	public void setUp(){
		this.eventScheduler = new JEEventScheduler();
	}
	
	/**
	 * Scheduling a new request with one machine artificially chosen by the heuristic
	 * @throws ConfigurationException 
	 */
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void handleEventNewRequestWithOneMachine() throws ConfigurationException{
		Request request = EasyMock.createStrictMock(Request.class);
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		MachineDescriptor descriptor = EasyMock.createStrictMock(MachineDescriptor.class);
		Machine machine = EasyMock.createStrictMock(TimeSharedMachine.class);
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		SimulatorConfiguration config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config);
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(new Class[] {ProfitDrivenHeuristic.class});
		
		EasyMock.expect(event.getType()).andReturn(JEEventType.NEWREQUEST).once();
		EasyMock.expect(event.getValue()).andReturn(new Request [] {request}).once();
		
		EasyMock.expect(schedulingHeuristic.getNextServer(
				EasyMock.isA(Request.class) , EasyMock.isA(List.class))).andReturn(machine);
		machine.sendRequest(request);
		
		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(event, schedulingHeuristic, descriptor, request, machine, config);
		
		lb = new LoadBalancer(eventScheduler, null, schedulingHeuristic, Integer.MAX_VALUE, descriptor);
		lb.handleEvent(event);
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(event, schedulingHeuristic, descriptor, request, machine, config);
		
		assertEquals(1, lb.getServers().size());
	}
	
	/**
	 * Scheduling a new request while the heuristic does not chooses any machines.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void handleEventNewRequestWithNoAvailableMachine(){
		Request request = EasyMock.createStrictMock(Request.class);
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		DPS dps = EasyMock.createStrictMock(DPS.class);
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		EasyMock.expect(event.getType()).andReturn(JEEventType.NEWREQUEST).once();
		EasyMock.expect(event.getValue()).andReturn(new Request [] {request}).once();
		EasyMock.expect(dps.getHandlerId()).andReturn(1).once();
		
		EasyMock.expect(schedulingHeuristic.getNextServer(
				EasyMock.isA(Request.class) , EasyMock.isA(List.class))).andReturn(null);
		
		EasyMock.replay(event, schedulingHeuristic, request, dps);
		
		//Load balancer being constructed without machines!
		lb = new LoadBalancer(eventScheduler, dps, schedulingHeuristic, Integer.MAX_VALUE);
		lb.handleEvent(event);
		
		EasyMock.verify(event, schedulingHeuristic, request, dps);
	}
	
	//TODO: Não entendi esse teste!
//	/**
//	 * Scheduling requests without machines does not throws any error
//	 */
//	@PrepareForTest(SimulatorConfiguration.class)
//	@Test
//	public void handleEventsNewRequestWithOneMachine(){
//		String clientID = "c1";
//		String userID = "u1";
//		String reqID = "1";
//		long time = 1000 * 60 * 1;
//		long size = 1024;
//		Integer requestOption = 0;
//		String URL = "";
//		String httpOperation = "GET";
//		long demand = 1000 * 60 * 20;
//
//		Request[] requests = new Request[1];
//		requests[0] = new Request(clientID, userID, reqID, time, size, requestOption, httpOperation, URL, demand);
//		
//		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
//		DPS dps = EasyMock.createStrictMock(DPS.class);
//		ProcessorSharedMachine machine = EasyMock.createStrictMock(ProcessorSharedMachine.class);
//		machine.setLoadBalancer(EasyMock.isA(LoadBalancer.class));
//		EasyMock.expectLastCall();
//		
//		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
//		EasyMock.expect(event.getType()).andReturn(JEEventType.NEWREQUEST).once();
//		EasyMock.expect(event.getValue()).andReturn(requests).once();
//		EasyMock.expect(schedulingHeuristic.getNextServer(EasyMock.isA(Request.class) , EasyMock.isA(List.class))).andReturn(machine);
//		machine.sendRequest(requests[0]);
//		EasyMock.expectLastCall().once();
//		
//		SimulatorConfiguration config = EasyMock.createStrictMock(SimulatorConfiguration.class);
//		PowerMock.mockStatic(SimulatorConfiguration.class);
//		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config);
//		EasyMock.expect(config.getLong(SimulatorProperties.SETUP_TIME)).andReturn(ONE_MINUTE_IN_MILLIS);
//		
//		PowerMock.replay(SimulatorConfiguration.class);
//		EasyMock.replay(event, machine, dps, schedulingHeuristic, config);
//		
//		lb = new LoadBalancer(eventScheduler, dps, schedulingHeuristic, Integer.MAX_VALUE);
//		lb.addServer(machine);//Creating a machine to serve the request
//		lb.handleEvent(new JEEvent(JEEventType.ADD_SERVER, lb, eventScheduler.now(), machine));
//		lb.handleEvent(event);
//		
//		PowerMock.verify(SimulatorConfiguration.class);
//		EasyMock.verify(event, machine, dps, schedulingHeuristic, config);
//		
//		assertNotNull(lb.getServers());
//		assertEquals(1, lb.getServers().size());
//	}
}