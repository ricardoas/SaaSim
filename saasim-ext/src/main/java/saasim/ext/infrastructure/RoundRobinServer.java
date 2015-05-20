package saasim.ext.infrastructure;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.iaas.Monitorable;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Machine;
import saasim.core.saas.Request;
import saasim.core.saas.Response;
import saasim.core.saas.ResponseListener;
import saasim.core.util.FastSemaphore;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class RoundRobinServer implements Machine, ResponseListener, Monitorable {
	
	
	private static final int DEFAULT_MACHINE_QUANTUM = 20;

	private static final String MACHINE_QUANTUM = "machine.roundrobin.quantum";
	private static final String MACHINE_NETWORKDELAY = "machine.network.delay";
	private static final String MACHINE_CONTEXTCHANGEDELAY = "machine.contextchange.delay";

	private InstanceDescriptor descriptor;
	private long startUpDelay;
	
	private Queue<Request> backlog;
	private int maxBacklogSize;
	private EventScheduler scheduler;
	private int arrived;
	private int failed;
	private long busy_time;
	private Queue<Request> processingQueue;
	private FastSemaphore processorTokens;
	private FastSemaphore threadTokens;
	private Map<Request,  Long> runningNow;
	private int quantum;

	private int tier;

	private long networkDelay;
	private long[] contextChangeDelay;

	private int maxThreads;
	
	@Inject
	public RoundRobinServer(@Assisted InstanceDescriptor descriptor, EventScheduler scheduler, Configuration globalConf) {
		this.descriptor = descriptor;
		this.descriptor.setMachine(this);
		
		this.scheduler = scheduler;
		this.startUpDelay = globalConf.getLong(MACHINE_SETUPTIME);
		this.maxBacklogSize = globalConf.getInt(MACHINE_BACKLOGSIZE);
		this.quantum = globalConf.getInt(MACHINE_QUANTUM, DEFAULT_MACHINE_QUANTUM);
		
		this.backlog = new LinkedList<>();
		this.processingQueue = new LinkedList<>();
		this.runningNow = new HashMap<>();
		
		this.processorTokens = new FastSemaphore(this.descriptor.getNumberOfCPUCores());
		maxThreads = globalConf.getInt(MACHINE_THREADSIZE);
		this.threadTokens = new FastSemaphore(maxThreads);
		tier = Integer.valueOf(descriptor.toString());
		
		this.networkDelay = globalConf.getLong(MACHINE_NETWORKDELAY, 0);
		this.contextChangeDelay = globalConf.getLongArray(MACHINE_CONTEXTCHANGEDELAY);

		resetStatistics(0);
	}
	
	private void resetStatistics(long busy_debt) {
		busy_time = -busy_debt;
		arrived = 0;
		failed = 0;
	}

	
	@Override
	public void reconfigure(InstanceDescriptor descriptor) {
		System.out.println("SingleQueueMachine.reconfigure() not yet implemented.");
	}

	@Override
	public void queue(final Request request) {
		
		arrived++;

		if(threadTokens.tryAcquire()){
			processingQueue.add(request);
			
			if(processorTokens.tryAcquire()){
				scheduleNext(1, -1);
			}
		}else if(backlog.size() < maxBacklogSize){
			backlog.add(request);
		}else{
			failed++;
			request.getResponseListener().processDone(request, null);
		}
	}
	
	private void scheduleNext(final int factor, long lastRequestID) {
		if(processingQueue.isEmpty()){
			processorTokens.release();
////			if(processorTokens.availablePermits() > descriptor.getNumberOfCPUCores()){
////				System.out.println(processorTokens.availablePermits());
////			}
			return;
		}
		
		final Request request = processingQueue.poll();
		runningNow.put(request, scheduler.now());
		final long demand = Math.min(quantum, request.getCPUTimeDemandInMillis());
		final int factorCS = request.getID() == lastRequestID? 0: 1;
		scheduler.queueEvent(new Event(scheduler.now() + demand + factorCS * contextChangeDelay[tier]) {
			@Override
			public void trigger() {
				preempt(request, demand, factor * contextChangeDelay[tier]);
			}
		});
	}

	protected void preempt(Request request, long demand, long contextDelay) {
		
		if(!descriptor.isOn()){ //machine turned off ( FIXME: maybe all request should be forwarded back together at machine shutdown method)
			failed++;
			request.getResponseListener().processDone(request, null);
			return;
		}

		busy_time += demand + contextDelay;
		request.updateServiceTime(demand);
		
		runningNow.remove(request);

		if(request.getCPUTimeDemandInMillis() != 0){
			processingQueue.add(request);
			scheduleNext(1, request.getID());
//			scheduleNext(1);
//			scheduleNext(0);
		}else{
			if(shouldForward(request)){
				request.setResponseListener(this);
				scheduler.queueEvent(new Event(scheduler.now() + networkDelay ) {
					@Override
					public void trigger() {
						descriptor.getApplication().queue(request);

					}
				});
			}else{
				threadTokens.release();
				if(!backlog.isEmpty()){
					if(threadTokens.tryAcquire()){
						processingQueue.add(backlog.poll());
					}
				}
				request.getResponseListener().processDone(request, new Response() {});
			}
			scheduleNext(1, request.getID());
//			scheduleNext(0);
		}
	}

	private boolean shouldForward(Request request) {
		int previousTier = request.getCurrentTier();
		request.forward();
		return request.getCurrentTier() > previousTier;
	}

	@Override
	public void processDone(Request request, Response response) {

		if(!descriptor.isOn()){
			failed++;
			request.getResponseListener().processDone(request, null);
			return;
		}
		
		processingQueue.add(request);
		
		if(processorTokens.tryAcquire()){
			scheduleNext(1, -1);
//			scheduleNext(0);
		}
	}

	@Override
	public long getStartUpDelay() {
		return this.startUpDelay;
	}

	@Override
	public void shutdown() {
		for (Request request : backlog) {
			failed++;
			request.getResponseListener().processDone(request, null);
		}
		backlog.clear();
	}

	@Override
	public Map<String,Double> collect(long now, long elapsedTime) {
		long busy_debt = 0;
		
		Set<Entry<Request,Long>> entrySet = runningNow.entrySet();
		for (Entry<Request, Long> entry : entrySet) {
			long start = entry.getValue();
			long d = Math.min(quantum, entry.getKey().getCPUTimeDemandInMillis());
			busy_time += now - start;
			busy_debt += (start + d) - now;
		}

		
		Map<String, Double> info = new TreeMap<>();
		info.put("arrived", 1.0*arrived);
		info.put("failed", 1.0*failed);
		info.put("util", 1.0* busy_time/(descriptor.getNumberOfCPUCores()*elapsedTime));
		info.put("pq", 1.0*processingQueue.size());
		info.put("bq", 1.0*backlog.size());
		info.put("ap", 1.0*processorTokens.availablePermits());
		info.put("at", 1.0*threadTokens.availablePermits());
		
		System.out.print(descriptor + " ");
		System.out.println(info);
		resetStatistics(busy_debt);
		
		return info;
	}

	@Override
	public boolean canShutdown() {
		return processorTokens.availablePermits() == descriptor.getNumberOfCPUCores();
	}
}
