package commons.sim.components;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import provisioning.DPS;
import provisioning.Monitor;
import util.ValidConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.schedulingheuristics.SchedulingHeuristic;
import commons.sim.util.MachineFactory;

public class LoadBalancerTest extends ValidConfigurationTest {
	
	
	@Test
	public void testAddServerWithSetupDelay() throws ConfigurationException{
		buildFullConfiguration();
		
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(LoadBalancer.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeSharedMachine.class))).andReturn(2);
		EasyMock.expect(scheduler.now()).andReturn(0l).times(2);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(captured));
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(scheduler, schedulingHeuristic);
		
		LoadBalancer lb = new LoadBalancer(scheduler, schedulingHeuristic, Integer.MAX_VALUE, 0);

		lb.addServer(descriptor, true);
		assertEquals(0, lb.getServers().size());
		
		JEEvent event = captured.getValue();
		assertEquals(JEEventType.ADD_SERVER, event.getType());
		assertEquals(300000l, event.getScheduledTime());
		
		EasyMock.verify(scheduler, schedulingHeuristic);
	}
	
	@Test
	public void testAddServerWithoutSetupDelay(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(LoadBalancer.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeSharedMachine.class))).andReturn(2);
		EasyMock.expect(scheduler.now()).andReturn(0l).times(2);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(captured));
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(scheduler, schedulingHeuristic);
		
		LoadBalancer lb = new LoadBalancer(scheduler, schedulingHeuristic, Integer.MAX_VALUE, 0);

		lb.addServer(descriptor, false);
		assertEquals(0, lb.getServers().size());
		
		JEEvent event = captured.getValue();
		assertEquals(JEEventType.ADD_SERVER, event.getType());
		assertEquals(0, event.getScheduledTime());
		
		EasyMock.verify(scheduler, schedulingHeuristic);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveServer(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		schedulingHeuristic.finishServer(EasyMock.isA(Machine.class), EasyMock.anyInt(), EasyMock.isA(List.class));
		
		
		EasyMock.replay(schedulingHeuristic);

		LoadBalancer lb = new LoadBalancer(JEEventScheduler.getInstance(), schedulingHeuristic, Integer.MAX_VALUE, 1);
		
		lb.addServer(descriptor, false);
		JEEventScheduler.getInstance().start();
		
		//Removing a server
		lb.removeServer(descriptor, false);
		
		EasyMock.verify(schedulingHeuristic);
	}
	
	@Test
	public void testRemoveServerThatDoesNotExist(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		EasyMock.replay(schedulingHeuristic);

		LoadBalancer lb = new LoadBalancer(JEEventScheduler.getInstance(), schedulingHeuristic, Integer.MAX_VALUE, 1);
		
		//Removing a server
		lb.removeServer(descriptor, false);
		
		EasyMock.verify(schedulingHeuristic);
	}
	
	/**
	 * Scheduling a new request with one machine artificially chosen by the heuristic
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleEventNewRequestWithOneMachine(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		Request request = EasyMock.createStrictMock(Request.class);
		JEEvent newRequestEvent = EasyMock.createStrictMock(JEEvent.class);
		Machine machine = EasyMock.createStrictMock(TimeSharedMachine.class);
		EasyMock.expect(machine.getDescriptor()).andReturn(descriptor);
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		EasyMock.expect(newRequestEvent.getType()).andReturn(JEEventType.NEWREQUEST).once();
		EasyMock.expect(newRequestEvent.getValue()).andReturn(new Request [] {request}).once();
		
		EasyMock.expect(schedulingHeuristic.getNextServer(
				EasyMock.isA(Request.class) , EasyMock.isA(List.class))).andReturn(machine);
		machine.sendRequest(request);
		
		EasyMock.replay(newRequestEvent, schedulingHeuristic, request, machine);
		
		LoadBalancer lb = new LoadBalancer(JEEventScheduler.getInstance(), schedulingHeuristic, Integer.MAX_VALUE, 1);
		lb.addServer(descriptor, false);
		JEEvent machineIsUpEvent = new JEEvent(JEEventType.ADD_SERVER, lb, 0l, machine);
		lb.handleEvent(machineIsUpEvent);
		lb.handleEvent(newRequestEvent);
		
		assertEquals(1, lb.getServers().size());
		
		EasyMock.verify(newRequestEvent, schedulingHeuristic, request, machine);
		
	}
	
	/**
	 * Scheduling a new request while the heuristic does not chooses any machines.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleEventNewRequestWithNoAvailableMachine(){
		Request request = EasyMock.createStrictMock(Request.class);
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		DPS dps = EasyMock.createStrictMock(DPS.class);
		dps.requestQueued(0, request, 1);
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		EasyMock.expect(event.getType()).andReturn(JEEventType.NEWREQUEST).once();
		EasyMock.expect(event.getValue()).andReturn(new Request [] {request}).once();
		
		EasyMock.expect(schedulingHeuristic.getNextServer(
				EasyMock.isA(Request.class) , EasyMock.isA(List.class))).andReturn(null);
		
		EasyMock.replay(event, schedulingHeuristic, request, dps);
		
		//Load balancer being constructed without machines!
		LoadBalancer lb = new LoadBalancer(JEEventScheduler.getInstance(), schedulingHeuristic, Integer.MAX_VALUE, 1);
		lb.setMonitor(dps);
		
		lb.handleEvent(event);
		
		EasyMock.verify(event, schedulingHeuristic, request, dps);
	}
	
	
	@Test
	public void testHandleEventEvaluateUtilisation() throws ConfigurationException{
		buildFullConfiguration();
		
		long evaluationTime = 1000;
		double utilisation1 = 0.9;
		double utilisation2 = 0.5;
		long totalArrivals = 100l;
		long totalCompletions = 100l;
		
		//Mocking machines actions
		MachineDescriptor descriptor = new MachineDescriptor(0, false, MachineType.M1_SMALL, 0);
		MachineDescriptor descriptor2 = new MachineDescriptor(1, true, MachineType.C1_MEDIUM, 0);
		
		RanjanMachine machine1 = EasyMock.createStrictMock(RanjanMachine.class);
		RanjanMachine machine2 = EasyMock.createStrictMock(RanjanMachine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.computeUtilisation(evaluationTime)).andReturn(utilisation1);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.computeUtilisation(evaluationTime)).andReturn(utilisation2);
		
		//Mocking scheduling heuristic actions
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.expect(schedulingHeuristic.getRequestsArrivalCounter()).andReturn(totalArrivals);
		EasyMock.expect(schedulingHeuristic.getFinishedRequestsCounter()).andReturn(totalCompletions);
		schedulingHeuristic.resetCounters();
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.sendStatistics(1000, new MachineStatistics((utilisation1+utilisation2)/2, totalArrivals, totalCompletions, 2), 0);
		EasyMock.replay(machine1, machine2, schedulingHeuristic, monitor);
		
		LoadBalancer lb = new LoadBalancer(JEEventScheduler.getInstance(), schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor);
		
		lb.addServer(descriptor, true);
		lb.addServer(descriptor2, true);
		
		JEEvent machineIsUpEvent = new JEEvent(JEEventType.ADD_SERVER, lb, 0l, machine1);
		JEEvent machineIsUpEvent2 = new JEEvent(JEEventType.ADD_SERVER, lb, 0l, machine2);
		
		lb.handleEvent(machineIsUpEvent);
		lb.handleEvent(machineIsUpEvent2);
		
		//Calculating utilisation
		lb.collectStatistics(evaluationTime);
		
		EasyMock.verify(machine1, machine2, schedulingHeuristic, monitor);
	}
	
	@Test
	public void testHandleEventEvaluateUtilisationWithoutMachines(){
		long evaluationTime = 1000;
		long totalArrivals = 100l;
		long totalCompletions = 0;
		
		//Mocking scheduling heuristic actions
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.expect(schedulingHeuristic.getRequestsArrivalCounter()).andReturn(totalArrivals);
		EasyMock.expect(schedulingHeuristic.getFinishedRequestsCounter()).andReturn(totalCompletions);
		schedulingHeuristic.resetCounters();
		EasyMock.replay(schedulingHeuristic);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.sendStatistics(0, new MachineStatistics(0, totalArrivals, totalCompletions, 0), 0);
		EasyMock.replay(monitor);
		
		LoadBalancer lb = new LoadBalancer(JEEventScheduler.getInstance(), schedulingHeuristic, Integer.MAX_VALUE, 0);
		
		//Calculating utilisation
		lb.handleEvent(new JEEvent(JEEventType.COLLECT_STATISTICS, lb, 0l, evaluationTime));
	}
	
	@Test
	public void testHandleEventMachineTurnedOff(){
		MachineDescriptor machineDescriptor = new MachineDescriptor(1, false, MachineType.C1_XLARGE, 0);

		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.machineTurnedOff(machineDescriptor);
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(monitor, schedulingHeuristic);
		
		LoadBalancer lb = new LoadBalancer(JEEventScheduler.getInstance(), schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor);
		
		lb.handleEvent(new JEEvent(JEEventType.MACHINE_TURNED_OFF, lb, 0l, machineDescriptor));
		
		EasyMock.verify(monitor, schedulingHeuristic);
	}
	
	@Test
	public void testHandleEventRequestQueued(){
		Request request = EasyMock.createStrictMock(Request.class);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.requestQueued(0, request, 0);
		
		SchedulingHeuristic schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(monitor, schedulingHeuristic, request);
		
		LoadBalancer lb = new LoadBalancer(JEEventScheduler.getInstance(), schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor);
		
		lb.handleEvent(new JEEvent(JEEventType.REQUESTQUEUED, lb, 0l, request));
		
		EasyMock.verify(monitor, schedulingHeuristic, request);
	}
}
