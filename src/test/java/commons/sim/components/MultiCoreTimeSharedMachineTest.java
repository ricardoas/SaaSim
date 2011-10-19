package commons.sim.components;

import static org.junit.Assert.*;

import java.util.Queue;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import util.MockedConfigurationTest;

import commons.cloud.MachineType;
import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest(Configuration.class)
public class MultiCoreTimeSharedMachineTest extends MockedConfigurationTest {

	private MachineDescriptor smallMachine;
	private MachineDescriptor largeMachine;
	private MachineDescriptor xLargeMachine;

	@Override
	public void setUp() {
		super.setUp();
		this.smallMachine = new MachineDescriptor(1, false, MachineType.M1_SMALL, 0);
		this.largeMachine = new MachineDescriptor(1, false, MachineType.M1_LARGE, 0);
		this.xLargeMachine = new MachineDescriptor(1, false, MachineType.M1_XLARGE, 0);
	}

	@Test
	public void testConstructor(){
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(JEEventScheduler.getInstance(), xLargeMachine, null);
		assertEquals(xLargeMachine, machine.getDescriptor());
		assertNull(machine.getLoadBalancer());
		assertNotNull(machine.getProcessorQueue());
		assertTrue(machine.getProcessorQueue().isEmpty());
	}
	
	private static Configuration mockConfiguration(int times) {
		Configuration config = EasyMock.createStrictMock(Configuration.class);
		PowerMock.mockStatic(Configuration.class);
		EasyMock.expect(Configuration.getInstance()).andReturn(config).times(times);
		return config;
	}

	
	@Test
	public void testSmallRequestExecutionWithSingleCoreMachine(){
	
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Capture<Request> captured = new Capture<Request>();
		loadBalancer.reportRequestFinished(EasyMock.capture(captured));
		EasyMock.replay(loadBalancer);
		
		Request request = new Request(0, 0, 0, 0, 10, 100, new long[]{50});
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(JEEventScheduler.getInstance(), smallMachine, loadBalancer);
		
		machine.sendRequest(request);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertTrue(queue.isEmpty());
		
		JEEventScheduler.getInstance().start();
		
		assertEquals(request, captured.getValue());
		assertEquals(50, JEEventScheduler.getInstance().now());
		
		EasyMock.verify(loadBalancer);
	}

	/**
	 * This method verifies that a single request is correctly added to a machine, without already
	 * processing requests, since the limit of threads is not reached
	 * @throws Exception 
	 */
	@Test
	public void testBigRequestExecutionWithSingleCoreMachine(){
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		Capture<Request> captured = new Capture<Request>();
		loadBalancer.reportRequestFinished(EasyMock.capture(captured));
		EasyMock.replay(loadBalancer);
		
		Request request = new Request(0, 0, 0, 0, 10, 100, new long[]{2500});
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(JEEventScheduler.getInstance(), smallMachine, loadBalancer);
		
		machine.sendRequest(request);
		Queue<Request> queue = machine.getProcessorQueue();
		assertNotNull(queue);
		assertTrue(queue.isEmpty());
		
		JEEventScheduler.getInstance().start();
		
		assertEquals(request, captured.getValue());
		assertEquals(2500, JEEventScheduler.getInstance().now());
		
		EasyMock.verify(loadBalancer);
	}

	@Test
	public void testSendRequestsSingleCoreMachine() throws SecurityException, IllegalArgumentException{
		Request request = new Request(0, 0, 0, 0, 10, 100, new long[]{5000});
		Request request2 = new Request(0, 0, 0, 0, 10, 100, new long[]{5000});
		Request request3 = new Request(0, 0, 0, 0, 10, 100, new long[]{5000});
		Request request4 = new Request(0, 0, 0, 0, 10, 100, new long[]{5000});
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(request);
		loadBalancer.reportRequestFinished(request2);
		loadBalancer.reportRequestFinished(request3);
		loadBalancer.reportRequestFinished(request4);
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.M1_SMALL, 0), loadBalancer);
		
		machine.sendRequest(request);
		machine.sendRequest(request2);
		machine.sendRequest(request3);
		machine.sendRequest(request4);
		
		Queue<Request> processorQueue = machine.getProcessorQueue();
		assertEquals(3, processorQueue.size());
		assertEquals(request2, processorQueue.poll());
		assertEquals(request3, processorQueue.poll());
		assertEquals(request4, processorQueue.poll());
		assertNull(processorQueue.poll());
		
		JEEventScheduler.getInstance().start();
		
		assertEquals(20000, JEEventScheduler.getInstance().now());
		
		EasyMock.verify(loadBalancer);
	}
	
	@Test
	public void testSendRequestsMultiCoreMachine() throws SecurityException, IllegalArgumentException{
		Request request = new Request(0, 0, 0, 0, 10, 100, new long[]{5000, 5000, 5000, 5000});
		Request request2 = new Request(0, 0, 0, 0, 10, 100, new long[]{5000, 5000, 5000, 5000});
		Request request3 = new Request(0, 0, 0, 0, 10, 100, new long[]{5000, 5000, 5000, 5000});
		Request request4 = new Request(0, 0, 0, 0, 10, 100, new long[]{5000, 5000, 5000, 5000});
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(request);
		loadBalancer.reportRequestFinished(request2);
		loadBalancer.reportRequestFinished(request3);
		loadBalancer.reportRequestFinished(request4);
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(JEEventScheduler.getInstance(), new MachineDescriptor(1, false, MachineType.M1_XLARGE, 0), loadBalancer);
		
		machine.sendRequest(request);
		machine.sendRequest(request2);
		machine.sendRequest(request3);
		machine.sendRequest(request4);
		
		Queue<Request> processorQueue = machine.getProcessorQueue();
		assertTrue(processorQueue.isEmpty());
		
		JEEventScheduler.getInstance().start();
		
		assertEquals(5000, JEEventScheduler.getInstance().now());
		
		EasyMock.verify(loadBalancer);
	}

	@Test
	public void testShutdownWithEmptyMachine(){
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(scheduler.registerHandler(loadBalancer));
		Capture<JEEvent> captured = new Capture<JEEvent>();
		loadBalancer.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, smallMachine, loadBalancer);
		machine.shutdownOnFinish();
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, event.getType());
		assertEquals(machine.getDescriptor(), event.getValue()[0]);
		
		assertEquals(0, JEEventScheduler.getInstance().now());
		
		EasyMock.verify(loadBalancer);
	}
	
	@Test
	public void testShutdownWithNonEmptyMachine(){
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		Request request = new Request(0, 0, 0, 0, 10, 100, new long[]{5000});
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(request);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(scheduler.registerHandler(loadBalancer));
		Capture<JEEvent> captured = new Capture<JEEvent>();
		loadBalancer.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		EasyMock.replay(loadBalancer);

		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, smallMachine, loadBalancer);
		machine.sendRequest(request);
		
		machine.shutdownOnFinish();
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, event.getType());
		assertEquals(machine.getDescriptor(), event.getValue()[0]);
		
		assertEquals(5000, JEEventScheduler.getInstance().now());
		
		EasyMock.verify(loadBalancer);
	}

		/**
	 * In this scenario events the scheduler deals with some preemption events until the request
	 * is fully processed. After the request is fully processed, the machine is turned off.
	 */
	@Test
	public void testHandlePreemptionOfLastRequestOnQueueWithShutdown(){
		JEEventScheduler scheduler = JEEventScheduler.getInstance();

		Request request = new Request(0, 0, 0, 0, 100000L, 100000L, new long[]{450, 450, 450, 450});
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(request);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(scheduler.registerHandler(loadBalancer));
		Capture<JEEvent> captured = new Capture<JEEvent>();
		loadBalancer.handleEvent(EasyMock.capture(captured));
		EasyMock.expectLastCall();
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, xLargeMachine, loadBalancer);
		machine.sendRequest(request);
		machine.shutdownOnFinish();
		
		scheduler.start();
		
		JEEvent event = captured.getValue();
		assertNotNull(event);
		assertEquals(JEEventType.MACHINE_TURNED_OFF, event.getType());
		assertEquals(machine.getDescriptor(), event.getValue()[0]);
		
		EasyMock.verify(loadBalancer);
	}

	/**
	 * The second request finishes first.
	 */
	@Test
	public void testHandlePreemptionOfTwoRequestAndOneCore(){
		JEEventScheduler scheduler = JEEventScheduler.getInstance();

		Request firstRequest = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{500});
		Request secondRequest = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{400});
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(secondRequest);
		EasyMock.expectLastCall();
		loadBalancer.reportRequestFinished(firstRequest);
		EasyMock.expectLastCall();
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, smallMachine, loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		scheduler.start();
		
		EasyMock.verify(loadBalancer);
	}
	
	@Test
	public void testHandlePreemptionOfTwoRequestAndMultiCores(){
		JEEventScheduler scheduler = JEEventScheduler.getInstance();

		Request firstRequest = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{500, 500, 500});
		Request secondRequest = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{400, 400, 400});
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(secondRequest);
		EasyMock.expectLastCall();
		loadBalancer.reportRequestFinished(firstRequest);
		EasyMock.expectLastCall();
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, largeMachine, loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		scheduler.start();
		
		EasyMock.verify(loadBalancer);
	}
	
	@Test
	public void testComputeUtilisationOfEmptyMachine(){
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.replay(loadBalancer);
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, largeMachine, loadBalancer);
		assertEquals(Double.NaN, machine.computeUtilisation(scheduler.now()), 0.0001);
		assertEquals(0, machine.computeUtilisation(scheduler.now() + 300000), 0.0001);
		
		EasyMock.verify(loadBalancer);
	}
	
	/**
	 * As the request finish event has not happened, the time that is an attribute of computeUtilisation
	 * is used as the computing time
	 */
	@Test
	public void testComputeUtilisationOfMachineWithRunningRequestAndOneCore(){
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(MultiCoreTimeSharedMachine.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(100l).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{150});
		
		EasyMock.replay(loadBalancer, scheduler);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, smallMachine, loadBalancer);
		machine.sendRequest(request);
		
		assertEquals(Double.NaN, machine.computeUtilisation(0), 0.0001);
		assertEquals(1, machine.computeUtilisation(50), 0.0001);
		
		//Simulating a preemption event
		machine.handleEvent(new JEEvent(JEEventType.PREEMPTION, machine, 100l, 100l, request));
		
		assertEquals(1, machine.computeUtilisation(100), 0.0001);
		assertEquals(1, machine.computeUtilisation(150), 0.0001);
		
		EasyMock.verify(loadBalancer, scheduler);
	}
	
	@Test
	public void testComputeUtilisationOfMachineWithRunningRequestAndOneCore2(){
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(MultiCoreTimeSharedMachine.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(100l).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(150l);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(EasyMock.isA(Request.class));
		
		Request request = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{150});
		
		EasyMock.replay(loadBalancer, scheduler);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, smallMachine, loadBalancer);
		machine.sendRequest(request);
		
		//Simulating a preemption event
		machine.handleEvent(new JEEvent(JEEventType.PREEMPTION, machine, 100l, 100l, request));
		
		//As a compute utilisation is performed, the next compute utilisation will start from this time!
		assertEquals(1, machine.computeUtilisation(100), 0.0001);
		
		machine.handleEvent(new JEEvent(JEEventType.PREEMPTION, machine, 150l, 50l, request));
		assertEquals(0.25, machine.computeUtilisation(300), 0.0001);
		
		EasyMock.verify(loadBalancer, scheduler);
	}

	/**
	 * As the request finish event has not happened, the time that is an attribute of computeUtilisation
	 * is used as the computing time
	 */
	@Test
	public void testComputeUtilisationOfMachineWithRunningRequestAndDualCore(){
		
		JEEventScheduler scheduler = EasyMock.createStrictMock(JEEventScheduler.class);
		EasyMock.expect(scheduler.registerHandler(EasyMock.isA(MultiCoreTimeSharedMachine.class))).andReturn(1);
		EasyMock.expect(scheduler.now()).andReturn(0l).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		EasyMock.expect(scheduler.now()).andReturn(100l).times(2);
		scheduler.queueEvent(EasyMock.isA(JEEvent.class));
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{150, 150, 150});
		
		EasyMock.replay(loadBalancer, scheduler);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, largeMachine, loadBalancer);
		machine.sendRequest(request);
		
		assertEquals(Double.NaN, machine.computeUtilisation(0), 0.0001);
		assertEquals(0.5, machine.computeUtilisation(50), 0.0001);
		
		//Simulating a preemption event
		machine.handleEvent(new JEEvent(JEEventType.PREEMPTION, machine, 100l, 100l, request));
		
		assertEquals(0.5, machine.computeUtilisation(100), 0.0001);
		assertEquals(0.5, machine.computeUtilisation(150), 0.0001);
		
		EasyMock.verify(loadBalancer, scheduler);
	}

	/**
	 * As the request finish event has not happened, the time that is an attribute of computeUtilisation
	 * is used as the computing time
	 */
	@Test
	public void testComputeUtilisationOfMachineWithRunningRequestAndQuadCore(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{50, 50, 50, 50});
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, xLargeMachine, loadBalancer);
		machine.sendRequest(request);
		
		assertEquals(Double.NaN, machine.computeUtilisation(scheduler.now()), 0.0001);
		assertEquals(0.25, machine.computeUtilisation(scheduler.now() + 50), 0.0001);
		
		EasyMock.verify(loadBalancer);
	}

	@Test
	public void testComputeUtilisationOfMachineAsRequestFinishesAndOneCore(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{50});
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, smallMachine, loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		assertEquals(1, machine.computeUtilisation(50), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(99), 0.0001);
		
		EasyMock.verify(loadBalancer);
	}
	
	@Test
	public void testComputeUtilisationOfMachineAsRequestFinishesAndQuadCore(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{50, 50, 50, 50});
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, xLargeMachine, loadBalancer);
		machine.sendRequest(request);

		scheduler.start();
		
		assertEquals(0.25, machine.computeUtilisation(50), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(299), 0.0001);
		
		EasyMock.verify(loadBalancer);
	}

	@Test
	public void testComputeUtilisationOfMachineAfterRequestFinishesAndOneCore(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{50});
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, smallMachine, loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		//Computing utilisation
		assertEquals(0.5, machine.computeUtilisation(100), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(150), 0.0001);
		
		EasyMock.verify(loadBalancer);
	}
	
	@Test
	public void testComputeUtilisationOfMachineAfterRequestFinishesAndQuadCore(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{50, 50, 50, 50});
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, xLargeMachine, loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		//Computing utilisation
		assertEquals(0.125, machine.computeUtilisation(100), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(150), 0.0001);
		
		EasyMock.verify(loadBalancer);
	}

	@Test
	public void testComputeUtilisationOfMachineAfterRequestFinishesAndOneCore2(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{50});
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, smallMachine, loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		assertEquals(1.0/3, machine.computeUtilisation(150), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(160), 0.0001);
		
		EasyMock.verify(loadBalancer);
	}
	
	@Test
	public void testComputeUtilisationOfMachineAfterRequestFinishesAndDualCore2(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{50, 50, 50});
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, largeMachine, loadBalancer);
		machine.sendRequest(request);
		
		scheduler.start();
		
		assertEquals(1.0/6, machine.computeUtilisation(150), 0.0001);
		//After computing utilization, counters are reseted for next period ...
		assertEquals(0.0, machine.computeUtilisation(151), 0.0001);
		
		EasyMock.verify(loadBalancer);
	}

	@Test
	public void testIsBusyWithRequests(){
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{50, 50, 50});
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, largeMachine, loadBalancer);
		machine.sendRequest(request);
		
		//Verifying if machine is busy
		assertTrue(machine.isBusy());
		
		EasyMock.verify(loadBalancer);
	}
	
	@Test
	public void testMachineIsBusyAfterRequestFinishes(){
		
		JEEventScheduler scheduler = JEEventScheduler.getInstance();
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		
		Request request = new Request(0, 0, 0, 0, 100L, 100000L, new long[]{50, 50, 50});
		
		loadBalancer.reportRequestFinished(request);
		
		EasyMock.replay(loadBalancer);
		
		MultiCoreTimeSharedMachine machine = new MultiCoreTimeSharedMachine(scheduler, largeMachine, loadBalancer);
		
		assertFalse(machine.isBusy());//Verifying if machine is busy
		
		machine.sendRequest(request);
		
		assertTrue(machine.isBusy());//Verifying if machine is busy
		
		scheduler.start();
		
		assertFalse(machine.isBusy());//Verifying if machine is busy
		
		EasyMock.verify(loadBalancer);
	}
}



