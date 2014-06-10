package saasim.ext.infrastructure;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
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

public class SingleQueueMachine implements Machine, ResponseListener, Monitorable {
	
	
	private InstanceDescriptor descriptor;
	private long startUpDelay;
	
	private Queue<Request> backlog;
	private int maxBacklogSize;
	private EventScheduler scheduler;
	private FastSemaphore semaphore;
	private int arrived;
	private int failed;
	private long busy_time;
	
	@Inject
	public SingleQueueMachine(@Assisted InstanceDescriptor descriptor, EventScheduler scheduler, Configuration globalConf) {
		this.descriptor = descriptor;
		this.descriptor.setMachine(this);
		
		this.scheduler = scheduler;
		this.startUpDelay = globalConf.getLong(MACHINE_SETUPTIME);
		this.backlog = new LinkedList<>();
		this.maxBacklogSize = globalConf.getInt(MACHINE_BACKLOGSIZE);
		this.semaphore = new FastSemaphore(this.descriptor.getNumberOfCPUCores());
		
		resetStatistics();
	}
	
	private void resetStatistics() {
		busy_time = 0;
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
		
		if(backlog.size() < maxBacklogSize){ //can wait
			if(semaphore.tryAcquire()){ //can run
				scheduler.queueEvent(new Event(scheduler.now() + request.getCPUTimeDemandInMillis()) {
					@Override
					public void trigger() {
						SingleQueueMachine.this.run(request);
					}
				});
			}else{
				backlog.add(request);
			}
		}else{ // cannot wait
			failed++;
			request.getResponseListener().processDone(request, null);
		}
	}
	
	protected void run(Request request) {
		
		if(!descriptor.isOn()){ //machine turned off
			failed++;
			request.getResponseListener().processDone(request, null);
		}else{ // machine on
			busy_time += request.getCPUTimeDemandInMillis();
			request.updateServiceTime(request.getCPUTimeDemandInMillis());
			forward(request);
		}
		
		
		if(!backlog.isEmpty()){ // poll another request from queue
			final Request newRequest = backlog.poll();
			scheduler.queueEvent(new Event(scheduler.now() + newRequest.getCPUTimeDemandInMillis()) {
				@Override
				public void trigger() {
					SingleQueueMachine.this.run(newRequest);
				}
			});
		}else{ // release cpu core
			semaphore.release();
		}
	}

	private void forward(Request request) {
		if(shouldForward(request)){
			request.setResponseListener(this);
			request.forward();
			descriptor.getApplication().queue(request);
//			forwarded.add(request);
		}else{
//			request.rollback();
			request.getResponseListener().processDone(request, new Response() {});
		}
	}


	private boolean shouldForward(Request request) {
		request.forward();
		boolean forward = request.getCPUTimeDemandInMillis() > 0;
		request.rollback();
		return forward;
	}

	@Override
	public long getStartUpDelay() {
		return this.startUpDelay;
	}
	
	@Override
	public void processDone(Request request, Response response) {
//		forwarded.remove(request);
		request.rollback();

		if(!descriptor.isOn()){
			failed++;
			request.getResponseListener().processDone(request, null);
		}else{
			request.getResponseListener().processDone(request, new Response() {});
		}
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
		Map<String, Double> info = new TreeMap<>();
		info.put("arrived", 1.0*arrived);
		info.put("failed", 1.0*failed);
		info.put("util", 1.0* busy_time/(descriptor.getNumberOfCPUCores()*elapsedTime));
		
		resetStatistics();
		
		return info;
	}
}
