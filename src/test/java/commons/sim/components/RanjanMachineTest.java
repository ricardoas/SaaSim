package commons.sim.components;

import static org.junit.Assert.*;

import java.util.List;

import org.easymock.EasyMock;
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
	
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void construction() throws Exception{
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		
		config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getMaximumNumberOfThreadsPerMachine()).andReturn(DEFAULT_MAX_NUM_OF_THREADS);
		EasyMock.expect(config.getMaximumBacklogSize()).andReturn(DEFAULT_BACKLOG_SIZE);
		
		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(scheduler, config);
		
		int machineID = 1;
		machine = new RanjanMachine(scheduler, new MachineDescriptor(machineID, false, 0), null);
		
		assertNotNull(machine.getBacklog());
		assertEquals(0, machine.getBacklog().size());
		assertEquals(machineID, machine.getMachineID());
		assertNull(machine.getLoadBalancer());
		assertNotNull(machine.getQueue());
		assertEquals(0, machine.getQueue().size());
		assertEquals(0, machine.getTotalProcessed(), 0.0);
		assertFalse(machine.isReserved);
		assertFalse(machine.shutdownOnFinish);
		assertNull(machine.nextFinishEvent);
		assertEquals(0, machine.numberOfRequestsArrivalsInPreviousInterval);
		assertEquals(0, machine.numberOfRequestsCompletionsInPreviousInterval);
		assertEquals(DEFAULT_MAX_NUM_OF_THREADS, machine.maximumNumberOfSimultaneousThreads);
		assertEquals(DEFAULT_BACKLOG_SIZE, machine.backlogMaximumNumberOfRequests);
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(scheduler, config);
	}
	
	/**
	 * This method verifies that a single request is correctly added to a machine, without already
	 * processing requests, since the limit of threads is not reached
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
		
		machine = new RanjanMachine(scheduler, new MachineDescriptor(1, false, 0), null);
		machine.sendRequest(request);
		
		List<Request> queue = machine.getQueue();
		
		assertFalse(queue.isEmpty());
		assertEquals(request, queue.get(0));
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(request, scheduler, config);
	}
	
	/**
	 * This method verifies that two identical requests are correctly added to a machine since the limit of
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
		
		machine = new RanjanMachine(scheduler, new MachineDescriptor(1, false, 0), null);
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
	 * This method verifies that two different requests are correctly added to a machine since the limit of
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
		
		machine = new RanjanMachine(scheduler, new MachineDescriptor(1, false, 0), null);
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
	
	/**
	 * This method verifies that two identical requests are correctly added to a machine since the limit of
	 * threads is not reached. Requests arrive at different times and as the second request arrives
	 * part of the first request is processed and both requests remain in the processing queue.
	 * @throws Exception 
	 */
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
		
		machine = new RanjanMachine(scheduler, new MachineDescriptor(1, false, 0), null);

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
	
	/**
	 * This method verifies that a RANJAN machine contains a limit in the number of simultaneous
	 * requests that can be processed. Since such limit is achieved, an incoming request is added
	 * to a backlog to be processed later.
	 * @throws Exception
	 */
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
		
		machine = new RanjanMachine(scheduler, new MachineDescriptor(1, false, 0), null);

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
	
	/**
	 * This method verifies that a RANJAN machine contains a limit in the number of simultaneous
	 * requests that can be processed. Since such limit is achieved, and the backlog is also full, the
	 * request is simply thrown away.
	 * @throws Exception
	 */
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
		
		machine = new RanjanMachine(scheduler, new MachineDescriptor(1, false, 0), null);
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
	
	/**
	 * This method verifies that a RANJAN machine contains a limit in the number of simultaneous
	 * requests that can be processed. As a request is finished, requests from the backlog are
	 * collected and added to the current processing queue.
	 * @throws Exception
	 */
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
		
		machine = new RanjanMachine(scheduler, new MachineDescriptor(1, false, 0), null);
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
	
	/**
	 * This method verifies that the utilization computed at each machine depends on the maximum number
	 * of threads that can be executed in a machine, and the current number of threads executing. In
	 * this test, the maximum number of threads and the current number of threads executing are equal.
	 * @throws Exception
	 */
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void computeUtilizationWithThreadLimitReached() throws Exception{
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
		
		machine = new RanjanMachine(scheduler, new MachineDescriptor(1, false, 0), null);
		machine.setLoadBalancer(loadBalancer);
		machine.sendRequest(firstRequest);
		machine.sendRequest(secondRequest);
		
		//Verifying utilization with one request in the queue, requests in backlog
		assertEquals(1.0 / localMaxNumberOfThreads, machine.computeUtilisation(0l), 0.0);
		
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
		assertEquals(1.0 / localMaxNumberOfThreads, machine.computeUtilisation(0l), 0.0);
	}
	
	/**
	 * This method verifies that the utilization computed at each machine depends on the maximum number
	 * of threads that can be executed in a machine, and the current number of threads executing. In
	 * this test, the maximum number of threads and the current number of threads executing are different.
	 * @throws Exception
	 */
	@PrepareForTest(SimulatorConfiguration.class)
	@Test
	public void computeUtilizationWithThreadLimitNotReached() throws Exception{
		config = EasyMock.createStrictMock(SimulatorConfiguration.class);
		PowerMock.mockStatic(SimulatorConfiguration.class);
		EasyMock.expect(SimulatorConfiguration.getInstance()).andReturn(config).times(2);
		EasyMock.expect(config.getMaximumNumberOfThreadsPerMachine()).andReturn(DEFAULT_MAX_NUM_OF_THREADS);
		EasyMock.expect(config.getMaximumBacklogSize()).andReturn(DEFAULT_BACKLOG_SIZE);
		
		scheduler = PowerMock.createPartialMockAndInvokeDefaultConstructor(JEEventScheduler.class, "now");
		EasyMock.expect(scheduler.now()).andReturn(new JETime(0L)).times(7);
		
		Request request = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request.getDemand()).andReturn(600000L).once();
		EasyMock.expect(request.getTotalToProcess()).andReturn(60000L).once();
		PowerMock.replay(SimulatorConfiguration.class);
		EasyMock.replay(request, scheduler, config);
		
		machine = new RanjanMachine(scheduler, new MachineDescriptor(1, false, 0), null);
		machine.sendRequest(request);
		
		//Verifying utilization
		assertEquals(1.0 / DEFAULT_MAX_NUM_OF_THREADS, machine.computeUtilisation(0l), 0.0);
		
		//Sending another request in the same time
		Request request2 = EasyMock.createStrictMock(Request.class);
		EasyMock.expect(request2.getDemand()).andReturn(10000L).once();
		EasyMock.replay(request2);
		
		machine.sendRequest(request2);
		
		//Verifying utilization
		assertEquals(2.0 / DEFAULT_MAX_NUM_OF_THREADS, machine.computeUtilisation(0l), 0.0);
		
		PowerMock.verify(SimulatorConfiguration.class);
		EasyMock.verify(request, scheduler, config, request2);
	}
}
