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

import commons.cloud.Request;
import commons.config.SimulatorConfiguration;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;

@RunWith(PowerMockRunner.class)
@PrepareForTest(JEEventScheduler.class)
public class RanjanMachineTest {
	
	private static final long DEFAULT_BACKLOG_SIZE = 100;
	private static final long DEFAULT_MAX_NUM_OF_THREADS = 10;
	
	private RanjanMachine machine;
	private JEEventScheduler scheduler;
	private SimulatorConfiguration config;
	
	@Before
	public void setUp() throws ConfigurationException{
	}
	
	/**
	 * This method verifies that a single request is correctly added to a machine since the limit of
	 * threads is not reached
	 * @throws Exception 
	 */
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void sendRequestWithEmptyServer() throws Exception{
		
		config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getMaximumNumberOfThreadsPerMachine()).andReturn(DEFAULT_MAX_NUM_OF_THREADS);
		EasyMock.expect(config.getMaximumBacklogSize()).andReturn(DEFAULT_BACKLOG_SIZE);
		
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0L)).times(3);
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getDemand()).andReturn(600000L).once();
		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(request, scheduler, config);
		
		machine = new RanjanMachine(scheduler, 1);
		machine.sendRequest(request);
		
		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		assertEquals(request, queue.get(0));
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(request, scheduler, config);
	}
	
	/**
	 * This method verifies that two requests are correctly added to a machine since the limit of
	 * threads is not reached
	 * @throws Exception 
	 */
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void sendTwoIdenticalRequestsAtSameTime() throws Exception{
		
		config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getMaximumNumberOfThreadsPerMachine()).andReturn(DEFAULT_MAX_NUM_OF_THREADS);
		EasyMock.expect(config.getMaximumBacklogSize()).andReturn(DEFAULT_BACKLOG_SIZE);
		
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(7);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(600000L).once();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(600000L).times(2);

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getDemand()).andReturn(600000L).once();
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(600000L).once();
		
		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(scheduler, firstRequest, secondRequest, config);
		
		machine = new RanjanMachine(scheduler, 1);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		Request firstRequestAtQueue = queue.get(0);
		assertEquals(firstRequest, firstRequestAtQueue);
		assertEquals(600000L, firstRequestAtQueue.getTotalToProcess());
		
		Request secondRequestAtQueue = queue.get(1);
		assertEquals(secondRequest, secondRequestAtQueue);
		assertEquals(600000L, secondRequestAtQueue.getTotalToProcess());
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(scheduler, firstRequest, secondRequest, config);
	}
	
	/**
	 * This method verifies that two requests are correctly added to a machine since the limit of
	 * threads is not reached
	 * @throws Exception 
	 */
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void sendDifferentRequestsAtSameTime() throws Exception{
		config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getMaximumNumberOfThreadsPerMachine()).andReturn(DEFAULT_MAX_NUM_OF_THREADS);
		EasyMock.expect(config.getMaximumBacklogSize()).andReturn(DEFAULT_BACKLOG_SIZE);
		
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(7);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(600000L).once();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(0L).once();

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getDemand()).andReturn(300000L).once();

		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(scheduler, firstRequest, secondRequest, config);
		
		machine = new RanjanMachine(scheduler, 1);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		Request firstRequestAtQueue = queue.get(0);
		assertEquals(firstRequest, firstRequestAtQueue);
		
		Request secondRequestAtQueue = queue.get(1);
		assertEquals(secondRequest, secondRequestAtQueue);
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(scheduler, firstRequest, secondRequest, config);
	}
	
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void sendTwoIdenticalRequestsAtDifferentOverlappingTimes() throws Exception{
		config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getMaximumNumberOfThreadsPerMachine()).andReturn(DEFAULT_MAX_NUM_OF_THREADS);
		EasyMock.expect(config.getMaximumBacklogSize()).andReturn(DEFAULT_BACKLOG_SIZE);
		
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(3);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(100000L)).times(4);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(600000L).once();
		firstRequest.update(100000L);
		EasyMock.expectLastCall();
		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(500000L).once();

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getDemand()).andReturn(600000L).once();
		
		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(scheduler, firstRequest, secondRequest, config);
		
		machine = new RanjanMachine(scheduler, 1);

		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		Request firstRequestAtQueue = queue.get(0);
		assertEquals(firstRequest, firstRequestAtQueue);
		
		Request secondRequestAtQueue = queue.get(1);
		assertEquals(secondRequest, secondRequestAtQueue);
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(scheduler, firstRequest, secondRequest, config);
	}
	
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void sendMoreRequestsThanMaxNumberOfThreads() throws Exception{
		long localMaxNumberOfThreads = 1l;
		
		config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getMaximumNumberOfThreadsPerMachine()).andReturn(localMaxNumberOfThreads);
		EasyMock.expect(config.getMaximumBacklogSize()).andReturn(DEFAULT_BACKLOG_SIZE);
		
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(3);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(600000L).once();

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		
		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(scheduler, firstRequest, secondRequest, config);
		
		machine = new RanjanMachine(scheduler, 1);

		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		//Verifying queue of requests that are being processed
		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		assertEquals(1, queue.size());
		Request firstRequestAtQueue = queue.get(0);
		assertEquals(firstRequest, firstRequestAtQueue);
		
		//Verifying backlog
		List<Request> backlog = machine.getBacklog();
		assertFalse(backlog.isEmpty());
		assertEquals(1, backlog.size());
		assertEquals(secondRequest, backlog.get(0));
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(scheduler, firstRequest, secondRequest, config);
	}
	
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void sendMoreRequestsThanMaxNumberOfThreadsAndBacklog() throws Exception{
		long localMaxNumberOfThreads = 1l;
		long backlogSize = 0l;
		
		config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getMaximumNumberOfThreadsPerMachine()).andReturn(localMaxNumberOfThreads);
		EasyMock.expect(config.getMaximumBacklogSize()).andReturn(backlogSize);
		
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(5);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(600000L).once();

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		EasyMock.expect(loadBalancer.getHandlerId()).andReturn(1);
		
		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(scheduler, firstRequest, secondRequest, config, loadBalancer);
		
		machine = new RanjanMachine(scheduler, 1);
		machine.setLoadBalancer(loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		//Verifying queue of requests that are being processed
		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		assertEquals(1, queue.size());
		Request firstRequestAtQueue = queue.get(0);
		assertEquals(firstRequest, firstRequestAtQueue);
		
		//Verifying backlog
		List<Request> backlog = machine.getBacklog();
		assertTrue(backlog.isEmpty());
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(scheduler, firstRequest, secondRequest, config, loadBalancer);
	}
	

	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void finishRequestAndAllowsAnotherRequestToBeProcessed() throws Exception{
		long localMaxNumberOfThreads = 1l;
		long backlogSize = 1l;
		
		config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getMaximumNumberOfThreadsPerMachine()).andReturn(localMaxNumberOfThreads);
		EasyMock.expect(config.getMaximumBacklogSize()).andReturn(backlogSize);
		
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(3);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(60000L)).times(3);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(60000L).once();
		firstRequest.update(60000L);
		EasyMock.expectLastCall();

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(10000L).times(2);
		EasyMock.expect(secondRequest.getDemand()).andReturn(10000L);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(firstRequest);
		EasyMock.expectLastCall();
		
		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(scheduler, firstRequest, secondRequest, config, loadBalancer);
		
		machine = new RanjanMachine(scheduler, 1);
		machine.setLoadBalancer(loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		//Verifying queue of requests that are being processed
		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		assertEquals(1, queue.size());
		Request firstRequestAtQueue = queue.get(0);
		assertEquals(firstRequest, firstRequestAtQueue);
		
		//Verifying backlog
		List<Request> backlog = machine.getBacklog();
		assertFalse(backlog.isEmpty());
		assertEquals(1, backlog.size());
		assertEquals(secondRequest, backlog.get(0));
		
		//Simulating that end event has arrived
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		Request[] requests = {firstRequest};
		EasyMock.expect(event.getType()).andReturn(JEEventType.REQUEST_FINISHED);
		EasyMock.expect(event.getValue()).andReturn(requests);
		EasyMock.replay(event);

		machine.handleEvent(event);
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(scheduler, firstRequest, secondRequest, config, loadBalancer, event);
		
		//Verifying queue of requests that are being processed
		queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		assertEquals(1, queue.size());
		firstRequestAtQueue = queue.get(0);
		assertEquals(secondRequest, firstRequestAtQueue);
		
		//Verifying backlog
		backlog = machine.getBacklog();
		assertTrue(backlog.isEmpty());
	}
	
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void computeUtilizationWithSequenceOfRequestsBeingProcessed() throws Exception{
		long localMaxNumberOfThreads = 1l;
		long backlogSize = 1l;
		
		config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getMaximumNumberOfThreadsPerMachine()).andReturn(localMaxNumberOfThreads);
		EasyMock.expect(config.getMaximumBacklogSize()).andReturn(backlogSize);
		
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(3);
		EasyMock.expect(scheduler.now()).andReturn(new JETime(60000L)).times(3);

		Request firstRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(firstRequest.getDemand()).andReturn(60000L).once();
		firstRequest.update(60000L);
		EasyMock.expectLastCall();

		Request secondRequest = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(secondRequest.getTotalToProcess()).andReturn(10000L).times(2);
		EasyMock.expect(secondRequest.getDemand()).andReturn(10000L);
		
		LoadBalancer loadBalancer = EasyMock.createStrictMock(LoadBalancer.class);
		loadBalancer.reportRequestFinished(firstRequest);
		EasyMock.expectLastCall();
		
		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(scheduler, firstRequest, secondRequest, config, loadBalancer);
		
		machine = new RanjanMachine(scheduler, 1);
		machine.setLoadBalancer(loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		//Verifying utilization with one request in the queue, requests in backlog
		assertEquals(1.0 / localMaxNumberOfThreads, machine.computeUtilization(0l), 0.0);
		
		//Simulating that end event has arrived
		JEEvent event = EasyMock.createStrictMock(JEEvent.class);
		Request[] requests = {firstRequest};
		EasyMock.expect(event.getType()).andReturn(JEEventType.REQUEST_FINISHED);
		EasyMock.expect(event.getValue()).andReturn(requests);
		EasyMock.replay(event);

		machine.handleEvent(event);
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(scheduler, firstRequest, secondRequest, config, loadBalancer, event);
		
		//Verifying queue of requests that are being processed
		assertEquals(1.0 / localMaxNumberOfThreads, machine.computeUtilization(0l), 0.0);
	}
	
	@Test
	public void computeUtilization2(){
		//TODO
	}
	
	
	
	
//	@Test
//	//FIXME: Esse teste está sem muito sentido! :P
//	public void sendTwoDifferentRequestsAtDifferentOverlappingTimesFinishingBefore() throws Exception{
//		scheduler = PowerMock.createStrictPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
//		EasyMock.expect(scheduler.now()).andReturn(new JETime(0)).times(3);
//		EasyMock.expect(scheduler.now()).andReturn(new JETime(100000L)).times(4);
//
//		Request firstRequest = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(firstRequest.getDemand()).andReturn(600000L).once();
//		firstRequest.update(100000L);
//		EasyMock.expectLastCall();
//		EasyMock.expect(firstRequest.getTotalToProcess()).andReturn(500000L).once();
//
//		Request secondRequest = EasyMock.createStrictMock(Request.class);
//		EasyMock.expect(secondRequest.getDemand()).andReturn(300000L).once();
//
//		EasyMock.replay(scheduler, firstRequest, secondRequest);
//
//		machine = new RanjanMachine(scheduler, 1);
//
//		machine.sendRequest(firstRequest);
//		machine.sendRequest(secondRequest);
//
//		List<Request> queue = machine.getQueue();
//
//		assertFalse(queue.isEmpty());
//		Request firstRequestAtQueue = queue.get(0);
//		assertEquals(firstRequest, firstRequestAtQueue);
//
//		Request secondRequestAtQueue = queue.get(1);
//		assertEquals(secondRequest, secondRequestAtQueue);
//		
//		EasyMock.verify(scheduler, firstRequest, secondRequest);
//	}

}
