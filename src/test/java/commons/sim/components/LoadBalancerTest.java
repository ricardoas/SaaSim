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

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import commons.sim.schedulingheuristics.ProfitDrivenHeuristic;
import commons.sim.schedulingheuristics.SchedulingHeuristic;
import commons.sim.util.MachineFactory;
import commons.sim.util.SaaSAppProperties;

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
	public void testAddServer(){
		MachineDescriptor descriptor = new MachineDescriptor(1, false, MachineType.SMALL);
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SETUP_TIME)).andReturn(1000l);
		EasyMock.expect(config.getApplicationHeuristics()).andReturn(new Class[]{ProfitDrivenHeuristic.class});
		
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
		
		JEEvent event = captured.getValue();
		assertEquals(JEEventType.ADD_SERVER, event.getType());
		assertEquals(1000l, event.getScheduledTime().timeMilliSeconds);
		
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
		int evaluationTime = 1000;
		
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config);
		EasyMock.expect(config.getLong(SaaSAppProperties.APPLICATION_SETUP_TIME)).andReturn(1000l);
		PowerMock.replay(Configuration.class);
		EasyMock.replay(config);
		
		MachineDescriptor descriptor = new MachineDescriptor(0, false, MachineType.SMALL);
		MachineDescriptor descriptor2 = new MachineDescriptor(1, true, MachineType.MEDIUM);
		
		RanjanMachine machine1 = EasyMock.createStrictMock(RanjanMachine.class);
		RanjanMachine machine2 = EasyMock.createStrictMock(RanjanMachine.class);
		EasyMock.expect(machine1.computeUtilisation(evaluationTime)).andReturn(0.9);
		EasyMock.expect(machine2.computeUtilisation(evaluationTime)).andReturn(0.5);
		EasyMock.replay(machine1);
		EasyMock.replay(machine2);
		
		this.schedulingHeuristic = EasyMock.createStrictMock(SchedulingHeuristic.class);
		EasyMock.expect(this.schedulingHeuristic.getRequestsArrivalCounter()).andReturn(100l);
		EasyMock.expect(this.schedulingHeuristic.getFinishedRequestsCounter()).andReturn(100l);
		this.schedulingHeuristic.resetCounters();
		
		lb = new LoadBalancer(eventScheduler, null, schedulingHeuristic, Integer.MAX_VALUE, 0);

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
}
