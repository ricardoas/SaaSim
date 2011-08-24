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
}