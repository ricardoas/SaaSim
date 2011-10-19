package commons.sim.components;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import provisioning.DPS;
import provisioning.Monitor;
import util.MockedConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.schedulingheuristics.ProfitDrivenHeuristic;
import commons.sim.schedulingheuristics.SchedulingHeuristic;
import commons.sim.util.MachineFactory;
import commons.sim.util.SaaSAppProperties;
import commons.sim.util.SimulatorProperties;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Configuration.class, MachineFactory.class})
public class LoadBalancerTest extends MockedConfigurationTest {
	
	private LoadBalancer lb;
	private JEEventScheduler eventScheduler;
	private SchedulingHeuristic schedulingHeuristic;

	@Before
	public void setUp(){
		this.eventScheduler = JEEventScheduler.getInstance();
	}
	
	@Test
	public void testAddServerWithSetupDelay(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SETUP_TIME)).andReturn(1000l);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(LoadBalancer.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeSharedMachine.class))).andReturn(2);
		EasyMock.expect(scheduler.now()).andReturn(0l).times(2);
		Capture<JEEvent> captured = new Capture<JEEvent>();
		scheduler.queueEvent(EasyMock.capture(captured));
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config, scheduler, this.schedulingHeuristic);
		
		lb = new LoadBalancer(scheduler, schedulingHeuristic, Integer.MAX_VALUE, 0);

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
		assertEquals(1000l, event.getScheduledTime());
		
		PowerMock.verifyAll();
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
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		PowerMock.replay(Configuration.class);
		EasyMock.replay(scheduler, this.schedulingHeuristic);
		
		lb = new LoadBalancer(scheduler, schedulingHeuristic, Integer.MAX_VALUE, 0);

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
		assertEquals(0, event.getScheduledTime());
		
		PowerMock.verifyAll();
	}
	
	@Ignore("method does now exists") @Test
	public void testAddNewServerDirectlyToLoadBalancer() throws InterruptedException{
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(LoadBalancer.class))).andReturn(1);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeSharedMachine.class))).andReturn(2);
		EasyMock.expect(scheduler.now()).andReturn(0l);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(TimeSharedMachine.class))).andReturn(2);
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		
		PowerMock.replayAll(this.schedulingHeuristic, config, scheduler);
		
		lb = new LoadBalancer(scheduler, schedulingHeuristic, Integer.MAX_VALUE, 0);
		TimeSharedMachine machine = new TimeSharedMachine(scheduler, descriptor, lb);
		machine.semaphore.acquire(2);
		
		assertEquals(0, lb.getServers().size());
		assertEquals(0, machine.semaphore.availablePermits());
		
//		lb.addServer(machine);//It also resets machine ...
		assertEquals(1, lb.getServers().size());
		assertEquals(2, machine.semaphore.availablePermits());
		
		PowerMock.verifyAll();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveServer(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		this.schedulingHeuristic.finishServer(EasyMock.isA(Machine.class), EasyMock.anyInt(), EasyMock.isA(List.class));
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		
		PowerMock.replayAll(this.schedulingHeuristic, config);

		lb = new LoadBalancer(eventScheduler, schedulingHeuristic, Integer.MAX_VALUE, 1);
		
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
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		
		PowerMock.replayAll(this.schedulingHeuristic, config);

		lb = new LoadBalancer(eventScheduler, schedulingHeuristic, Integer.MAX_VALUE, 1);
		
		//Removing a server
		lb.removeServer(descriptor, false);
		
		PowerMock.verifyAll();
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
		
		lb = new LoadBalancer(eventScheduler, schedulingHeuristic, Integer.MAX_VALUE, 1);
		lb.addServer(descriptor, false);
		JEEvent machineIsUpEvent = new JEEvent(JEEventType.ADD_SERVER, lb, 0l, machine);
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
		lb = new LoadBalancer(eventScheduler, schedulingHeuristic, Integer.MAX_VALUE, 1);
		lb.setMonitor(dps);
		
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
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SETUP_TIME)).andReturn(1000l).times(2);
		EasyMock.expect(config.getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)).andReturn(1000l);
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		//Mocking machines actions
		MachineDescriptor descriptor = new MachineDescriptor(0, false, MachineType.M1_SMALL, 0);
		MachineDescriptor descriptor2 = new MachineDescriptor(1, true, MachineType.C1_MEDIUM, 0);
		
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
		monitor.sendStatistics(0, new MachineStatistics((utilisation1+utilisation2)/2, totalArrivals, totalCompletions, 2), 0);
		EasyMock.replay(monitor);
		
		lb = new LoadBalancer(eventScheduler, schedulingHeuristic, Integer.MAX_VALUE, 0);
		
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
		
		JEEvent machineIsUpEvent = new JEEvent(JEEventType.ADD_SERVER, lb, 0l, machine1);
		JEEvent machineIsUpEvent2 = new JEEvent(JEEventType.ADD_SERVER, lb, 0l, machine2);
		
		lb.handleEvent(machineIsUpEvent);
		lb.handleEvent(machineIsUpEvent2);
		
		//Calculating utilisation
		lb.handleEvent(new JEEvent(JEEventType.COLLECT_STATISTICS, lb, 0l, evaluationTime));
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testHandleEventEvaluateUtilisationWithoutMachines(){
		long evaluationTime = 1000;
		long totalArrivals = 100l;
		long totalCompletions = 0;
		
		//Mocking scheduling heuristic actions
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.expect(this.schedulingHeuristic.getRequestsArrivalCounter()).andReturn(totalArrivals);
		EasyMock.expect(this.schedulingHeuristic.getFinishedRequestsCounter()).andReturn(totalCompletions);
		this.schedulingHeuristic.resetCounters();
		EasyMock.replay(this.schedulingHeuristic);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.sendStatistics(0, new MachineStatistics(0, totalArrivals, totalCompletions, 0), 0);
		EasyMock.replay(monitor);
		
		lb = new LoadBalancer(eventScheduler, schedulingHeuristic, Integer.MAX_VALUE, 0);
		
		//Calculating utilisation
		lb.handleEvent(new JEEvent(JEEventType.COLLECT_STATISTICS, lb, 0l, evaluationTime));
	}
	
	@Test
	public void testHandleEventMachineTurnedOff(){
		MachineDescriptor machineDescriptor = new MachineDescriptor(1, false, MachineType.C1_XLARGE, 0);

		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.machineTurnedOff(machineDescriptor);
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(monitor, this.schedulingHeuristic);
		
		lb = new LoadBalancer(eventScheduler, schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor);
		
		lb.handleEvent(new JEEvent(JEEventType.MACHINE_TURNED_OFF, lb, 0l, machineDescriptor));
		
		EasyMock.verify(monitor, this.schedulingHeuristic);
	}
	
	@Test
	public void testHandleEventRequestQueued(){
		Request request = EasyMock.createStrictMock(Request.class);
		
		Monitor monitor = EasyMock.createStrictMock(Monitor.class);
		monitor.requestQueued(0, request, 0);
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.replay(monitor, this.schedulingHeuristic, request);
		
		lb = new LoadBalancer(eventScheduler, schedulingHeuristic, Integer.MAX_VALUE, 0);
		lb.setMonitor(monitor);
		
		lb.handleEvent(new JEEvent(JEEventType.REQUESTQUEUED, lb, 0l, request));
		
		EasyMock.verify(monitor, this.schedulingHeuristic, request);
	}
}
