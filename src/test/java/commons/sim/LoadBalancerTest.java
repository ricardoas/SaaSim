package commons.sim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import provisioning.DPS;

import commons.cloud.Request;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.schedulingheuristics.SchedulingHeuristic;


public class LoadBalancerTest {
	
	private LoadBalancer lb;
	private JEEventScheduler eventScheduler;
	private SchedulingHeuristic schedulingHeuristic;

	@Before
	public void setUp(){
		this.eventScheduler = new JEEventScheduler();
	}
	
	/**
	 * Scheduling a new request with one machine artificially chosen by the heuristic
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void handleEventNewRequestWithOneMachine(){
		Request request = EasyMock.createStrictMock(Request.class);
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		Machine machine = EasyMock.createStrictMock(Machine.class);
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		EasyMock.expect(event.getType()).andReturn(JEEventType.NEWREQUEST).once();
		EasyMock.expect(event.getValue()).andReturn(new Request [] {request}).once();
		
		EasyMock.expect(schedulingHeuristic.getNextServer(
				EasyMock.isA(Request.class) , EasyMock.isA(List.class))).andReturn(machine);
		machine.sendRequest(request);
		
		EasyMock.replay(event, schedulingHeuristic, machine, request);
		
		lb = new LoadBalancer(eventScheduler, null, schedulingHeuristic, Integer.MAX_VALUE, machine);
		lb.handleEvent(event);
		
		EasyMock.verify(event, schedulingHeuristic, machine, request);
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
		
		lb = new LoadBalancer(eventScheduler, dps, schedulingHeuristic, Integer.MAX_VALUE);
		lb.handleEvent(event);
		
		EasyMock.verify(event, schedulingHeuristic, request, dps);
	}
	
	/**
	 * Scheduling a new request without machines does not throws any error
	 */
	@Test
	public void handleEventNewRequestWithOneMachines(){
		String clientID = "c1";
		String userID = "u1";
		String reqID = "1";
		long time = 1000 * 60 * 1;
		long size = 1024;
		Integer requestOption = 0;
		String URL = "";
		String httpOperation = "GET";
		long demand = 1000 * 60 * 20;

		Request[] requests = new Request[1];
		requests[0] = new Request(clientID, userID, reqID, time, size, requestOption, httpOperation, URL, demand);
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		DPS dps = EasyMock.createStrictMock(DPS.class);
		Machine machine = EasyMock.createStrictMock(Machine.class);
		
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		EasyMock.expect(event.getType()).andReturn(JEEventType.NEWREQUEST).once();
		EasyMock.expect(event.getValue()).andReturn(requests).once();
		EasyMock.expect(schedulingHeuristic.getNextServer(
				EasyMock.isA(Request.class) , EasyMock.isA(List.class))).andReturn(machine);
		machine.sendRequest(requests[0]);
		EasyMock.expectLastCall().once();
		
		EasyMock.replay(event, machine, dps, schedulingHeuristic);
		
		lb = new LoadBalancer(eventScheduler, dps, schedulingHeuristic, Integer.MAX_VALUE);
		lb.getServers().add(machine);//Creating a machine to serve the request
		lb.handleEvent(event);
		
		EasyMock.verify(event, machine, dps, schedulingHeuristic);
		
		assertNotNull(lb.getServers());
		assertEquals(1, lb.getServers().size());
	}
	
}
