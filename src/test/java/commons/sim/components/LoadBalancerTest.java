package commons.sim.components;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import provisioning.DPS;
import provisioning.Monitor;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import commons.sim.provisioningheuristics.RanjanStatistics;
import commons.sim.schedulingheuristics.ProfitDrivenHeuristic;
import commons.sim.schedulingheuristics.SchedulingHeuristic;
import commons.sim.util.MachineFactory;
import commons.sim.util.SaaSAppProperties;
import commons.sim.util.SimulatorProperties;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class, MachineFactory.class})

public class LoadBalancerTest {
	
	private LoadBalancer lb;
	private JEEventScheduler eventScheduler;
	private SchedulingHeuristic schedulingHeuristic;

	@Before
	public void setUp(){
		this.eventScheduler = new JEEventScheduler();
	}
	
	@Test
	public void testAddServerWithSetupDelay(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.SMALL);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SETUP_TIME)).andReturn(1000l);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(LoadBalancer.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeSharedMachine.class))).andReturn(2);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(2);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(captured));
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config, scheduler, this.schedulingHeuristic);
		
		lb = new LoadBalancer(scheduler, null, schedulingHeuristic, Integer.MAX_VALUE, 0);

		MachineFactory factory = EasyMock.createStrictMock(MachineFactory.class);
		PowerMock.mockStatic(MachineFactory.class);
		EasyMock.expect(MachineFactory.getInstance()).andReturn(factory);
		EasyMock.expect(factory.createMachine(scheduler, descriptor, lb)).andReturn(new TimeSharedMachine(scheduler, descriptor, lb));
		PowerMock.replay(MachineFactory.class);
		EasyMock.replay(factory);
		
		lb.addServer(descriptor, true);
		assertEquals(0, lb.getServers().size());
		
		JEEvent event = captured.getValue();
		assertEquals(JEEventType.ADD_SERVER, event.getType());
		assertEquals(1000l, event.getScheduledTime().timeMilliSeconds);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testAddServerWithoutSetupDelay(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.SMALL);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(LoadBalancer.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeSharedMachine.class))).andReturn(2);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(2);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(captured));
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		PowerMock.replay(Configuration.class);
		EasyMock.replay(scheduler, this.schedulingHeuristic);
		
		lb = new LoadBalancer(scheduler, null, schedulingHeuristic, Integer.MAX_VALUE, 0);

		MachineFactory factory = EasyMock.createStrictMock(MachineFactory.class);
		PowerMock.mockStatic(MachineFactory.class);
		EasyMock.expect(MachineFactory.getInstance()).andReturn(factory);
		EasyMock.expect(factory.createMachine(scheduler, descriptor, lb)).andReturn(new TimeSharedMachine(scheduler, descriptor, lb));
		PowerMock.replay(MachineFactory.class);
		EasyMock.replay(factory);
		
		lb.addServer(descriptor, false);
		assertEquals(0, lb.getServers().size());
		
		JEEvent event = captured.getValue();
		assertEquals(JEEventType.ADD_SERVER, event.getType());
		assertEquals(0, event.getScheduledTime().timeMilliSeconds);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testRemoveServer(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.SMALL);
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		this.schedulingHeuristic.finishServer(EasyMock.isA(Machine.class), EasyMock.anyInt(), EasyMock.isA(List.class));
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		
		PowerMock.replayAll(this.schedulingHeuristic, config);

		lb = new LoadBalancer(eventScheduler, null, schedulingHeuristic, Integer.MAX_VALUE, 1);
		
		MachineFactory factory = EasyMock.createStrictMock(MachineFactory.class);
		PowerMock.mockStatic(MachineFactory.class);
		EasyMock.expect(MachineFactory.getInstance()).andReturn(factory);
		EasyMock.expect(factory.createMachine(eventScheduler, descriptor, lb)).andReturn(new TimeSharedMachine(eventScheduler, descriptor, lb));
		PowerMock.replay(MachineFactory.class);
		EasyMock.replay(factory);
		
		lb.addServer(descriptor, false);
		eventScheduler.start();
		
		//Removing a server
		lb.removeServer(descriptor, false);
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testRemoveServerThatDoesNotExist(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.SMALL);
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		
		PowerMock.replayAll(this.schedulingHeuristic, config);

		lb = new LoadBalancer(eventScheduler, null, schedulingHeuristic, Integer.MAX_VALUE, 1);
		
		//Removing a server
		lb.removeServer(descriptor, false);
		
		PowerMock.verifyAll();
	}
	
	/**
	 * Scheduling a new request with one machine artificially chosen by the heuristic
	 * @throws ConfigurationException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testHandleEventNewRequestWithOneMachine() throws ConfigurationException{
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.SMALL);
		
		Request request = EasyMock.createStrictMock(Request.class);
		JEEvent newRequestEvent = EasyMock.createStrictMock(JEEvent.class);
		Machine machine = EasyMock.createStrictMock(TimeSharedMachine.class);
		EasyMock.expect(machine.getDescriptor()).andReturn(descriptor);
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(new Class[] {ProfitDrivenHeuristic.class});
		
		EasyMock.expect(newRequestEvent.getType()).andReturn(JEEventType.NEWREQUEST).once();
		EasyMock.expect(newRequestEvent.getValue()).andReturn(new Request [] {request}).once();
		
		EasyMock.expect(schedulingHeuristic.getNextServer(
				EasyMock.isA(Request.class) , EasyMock.isA(List.class))).andReturn(machine);
		machine.sendRequest(request);
		
		PowerMock.replay(Configuration.class);
		EasyMock.replay(newRequestEvent, schedulingHeuristic, request, machine, config);
		
		lb = new LoadBalancer(eventScheduler, null, schedulingHeuristic, Integer.MAX_VALUE, 1);
		lb.addServer(descriptor, false);
		JEEvent machineIsUpEvent = new JEEvent(JEEventType.ADD_SERVER, lb, new JETime(0), machine);
		lb.handleEvent(machineIsUpEvent);
		lb.handleEvent(newRequestEvent);
		
		PowerMock.verify(Configuration.class);
		EasyMock.verify(newRequestEvent, schedulingHeuristic, request, machine, config);
		
		assertEquals(1, lb.getServers().size());
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
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		EasyMock.expect(event.getType()).andReturn(JEEventType.NEWREQUEST).once();
		EasyMock.expect(event.getValue()).andReturn(new Request [] {request}).once();
		
		EasyMock.expect(schedulingHeuristic.getNextServer(
				EasyMock.isA(Request.class) , EasyMock.isA(List.class))).andReturn(null);
		
		EasyMock.replay(event, schedulingHeuristic, request, dps);
		
		//Load balancer being constructed without machines!
		lb = new LoadBalancer(eventScheduler, dps, schedulingHeuristic, Integer.MAX_VALUE, 1);
		lb.handleEvent(event);
		
		EasyMock.verify(event, schedulingHeuristic, request, dps);
	}
	
	
	@Test
	public void testHandleEventEvaluateUtilisation(){
		long evaluationTime = 1000;
		double utilisation1 = 0.9;
		double utilisation2 = 0.5;
		long totalArrivals = 100l;
		long totalCompletions = 100l;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(3);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SETUP_TIME)).andReturn(1000l).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.RANJAN_HEURISTIC_REPEAT_INTERVAL)).andReturn(1000l);
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Mocking machines actions
		MachineDescriptor descriptor = new MachineDescriptor(0, false, MachineType.SMALL);
		MachineDescriptor descriptor2 = new MachineDescriptor(1, true, MachineType.MEDIUM);
		
		RanjanMachine machine1 = EasyMock.createStrictMock(RanjanMachine.class);
		RanjanMachine machine2 = EasyMock.createStrictMock(RanjanMachine.class);
		EasyMock.expect(machine1.getDescriptor()).andReturn(descriptor);
		EasyMock.expect(machine1.computeUtilisation(evaluationTime)).andReturn(utilisation1);
		EasyMock.expect(machine2.getDescriptor()).andReturn(descriptor2);
		EasyMock.expect(machine2.computeUtilisation(evaluationTime)).andReturn(utilisation2);
		EasyMock.replay(machine1);
		EasyMock.replay(machine2);
		
		//Mocking scheduling heuristic actions
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.expect(this.schedulingHeuristic.getRequestsArrivalCounter()).andReturn(totalArrivals);
		EasyMock.expect(this.schedulingHeuristic.getFinishedRequestsCounter()).andReturn(totalCompletions);
		this.schedulingHeuristic.resetCounters();
		EasyMock.replay(this.schedulingHeuristic);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.evaluateUtilisation(0, new RanjanStatistics(utilisation1+utilisation2, totalArrivals, totalCompletions, 2), 0);
		EasyMock.replay(monitor);
		
		lb = new LoadBalancer(eventScheduler, monitor, schedulingHeuristic, Integer.MAX_VALUE, 0);
		
		//Mocking machines creation
		MachineFactory factory = EasyMock.createStrictMock(MachineFactory.class);
		PowerMock.mockStatic(MachineFactory.class);
		EasyMock.expect(MachineFactory.getInstance()).andReturn(factory).times(2);
		EasyMock.expect(factory.createMachine(eventScheduler, descriptor, lb)).andReturn(machine1);
		EasyMock.expect(factory.createMachine(eventScheduler, descriptor2, lb)).andReturn(machine2);
		PowerMock.replay(MachineFactory.class);
		EasyMock.replay(factory);
		
		lb.addServer(descriptor, true);
		lb.addServer(descriptor2, true);
		
		JEEvent machineIsUpEvent = new JEEvent(JEEventType.ADD_SERVER, lb, new JETime(0), machine1);
		JEEvent machineIsUpEvent2 = new JEEvent(JEEventType.ADD_SERVER, lb, new JETime(0), machine2);
		
		lb.handleEvent(machineIsUpEvent);
		lb.handleEvent(machineIsUpEvent2);
		
		//Calculating utilisation
		lb.handleEvent(new JEEvent(JEEventType.EVALUATEUTILIZATION, lb, new JETime(0), evaluationTime));
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testHandleEventEvaluateUtilisationWithoutMachines(){
		long evaluationTime = 1000;
		long totalArrivals = 100l;
		long totalCompletions = 0;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getLong(SimulatorProperties.RANJAN_HEURISTIC_REPEAT_INTERVAL)).andReturn(1000l);
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Mocking scheduling heuristic actions
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.expect(this.schedulingHeuristic.getRequestsArrivalCounter()).andReturn(totalArrivals);
		EasyMock.expect(this.schedulingHeuristic.getFinishedRequestsCounter()).andReturn(totalCompletions);
		this.schedulingHeuristic.resetCounters();
		EasyMock.replay(this.schedulingHeuristic);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.evaluateUtilisation(0, new RanjanStatistics(0, totalArrivals, totalCompletions, 0), 0);
		EasyMock.replay(monitor);
		
		lb = new LoadBalancer(eventScheduler, monitor, schedulingHeuristic, Integer.MAX_VALUE, 0);
		
		//Calculating utilisation
		lb.handleEvent(new JEEvent(JEEventType.EVALUATEUTILIZATION, lb, new JETime(0), evaluationTime));
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testHandleEventMachineTurnedOff(){
		MachineDescriptor machineDescriptor = new MachineDescriptor(1, false, MachineType.HIGHCPU);

		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.machineTurnedOff(machineDescriptor);
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(monitor, this.schedulingHeuristic);
		
		lb = new LoadBalancer(eventScheduler, monitor, schedulingHeuristic, Integer.MAX_VALUE, 0);
		
		lb.handleEvent(new JEEvent(JEEventType.MACHINE_TURNED_OFF, lb, new JETime(0), machineDescriptor));
		
		EasyMock.verify(monitor, this.schedulingHeuristic);
	}
	
	@Test
	public void testHandleEventRequestQueued(){
		Request request = EasyMock.createStrictMock(Request.class);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.requestQueued(0, request, 0);
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(monitor, this.schedulingHeuristic, request);
		
		lb = new LoadBalancer(eventScheduler, monitor, schedulingHeuristic, Integer.MAX_VALUE, 0);
		
		lb.handleEvent(new JEEvent(JEEventType.REQUESTQUEUED, lb, new JETime(0), request));
		
		EasyMock.verify(monitor, this.schedulingHeuristic, request);
	}
}
