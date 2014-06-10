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

public class RoundRobinMachine implements Machine, ResponseListener, Monitorable {
	
	
	private static final int DEFAULT_MACHINE_QUANTUM = 20;

	private static final String MACHINE_QUANTUM = "machine.roundrobin.quantum";
	
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
	
	@Inject
	public RoundRobinMachine(@Assisted InstanceDescriptor descriptor, EventScheduler scheduler, Configuration globalConf) {
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
		this.threadTokens = new FastSemaphore(globalConf.getInt(MACHINE_THREADSIZE));
		
		
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
				scheduleNext();
			}
		}else if(backlog.size() < maxBacklogSize){
			backlog.add(request);
		}else{
			failed++;
			request.getResponseListener().processDone(request, null);
		}
	}
	
	private void scheduleNext() {
		if(processingQueue.isEmpty()){
			processorTokens.release();
//			if(processorTokens.availablePermits() > descriptor.getNumberOfCPUCores()){
//				System.out.println(processorTokens.availablePermits());
//			}
			return;
		}
		
		final Request request = processingQueue.poll();
		runningNow.put(request, scheduler.now());
		final long demand = Math.min(quantum, request.getCPUTimeDemandInMillis());
		scheduler.queueEvent(new Event(scheduler.now() + demand) {
			@Override
			public void trigger() {
				preempt(request, demand);
			}
		});
	}

	protected void preempt(Request request, long demand) {
		
		if(!descriptor.isOn()){ //machine turned off ( FIXME: maybe all request should be forwarded back together at machine shutdown method)
			failed++;
			request.getResponseListener().processDone(request, null);
			return;
		}

		busy_time += demand;
		request.updateServiceTime(demand);
		
		runningNow.remove(request);

		if(request.getCPUTimeDemandInMillis() != 0){
			processingQueue.add(request);
			scheduleNext();
		}else if(shouldForward(request)){
			request.setResponseListener(this);
			request.forward();
			descriptor.getApplication().queue(request);
			scheduleNext();
		}else{
			threadTokens.release();
			request.getResponseListener().processDone(request, new Response() {});
			if(!backlog.isEmpty()){
				threadTokens.tryAcquire();
				processingQueue.add(backlog.poll());
			}
			scheduleNext();
		}
	}

	private boolean shouldForward(Request request) {
		request.forward();
		boolean forward = request.getCPUTimeDemandInMillis() > 0;
		request.rollback();
		return forward;
	}

	@Override
	public void processDone(Request request, Response response) {
		request.rollback();

		if(!descriptor.isOn()){
			failed++;
			request.getResponseListener().processDone(request, null);
			return;
		}

		request.getResponseListener().processDone(request, new Response() {});
		threadTokens.release();
		
		if(!backlog.isEmpty()){
			if(threadTokens.tryAcquire()){
				processingQueue.add(backlog.poll());
				
				if(processorTokens.tryAcquire()){
					scheduleNext();
				}
			}
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
		
		resetStatistics(busy_debt);
		
		return info;
	}
}
