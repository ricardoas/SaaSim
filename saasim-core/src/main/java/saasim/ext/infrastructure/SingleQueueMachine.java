package saasim.ext.infrastructure;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import saasim.core.application.Request;
import saasim.core.application.Response;
import saasim.core.application.Tier;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Machine;
import saasim.core.infrastructure.MonitoringService;
import saasim.core.util.FastSemaphore;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class SingleQueueMachine implements Machine {
	
	
	private InstanceDescriptor descriptor;
	private long startUpDelay;
	
	private Queue<Request> backlog;
	private int maxBacklogSize;
	private EventScheduler scheduler;
	private FastSemaphore semaphore;
	private Tier nextTier;
	private Queue<Request> forwarded;
	private int arrived;
	private int failed;
	
	@Inject
	public SingleQueueMachine(@Assisted InstanceDescriptor descriptor, MonitoringService monitor, EventScheduler scheduler, Configuration globalConf) {
		this.descriptor = descriptor;
		this.descriptor.setMachine(this);
		
		monitor.setMonitorable(this);
		
		this.scheduler = scheduler;
		this.startUpDelay = globalConf.getLong(MACHINE_SETUPTIME);
		this.backlog = new LinkedList<>();
		this.forwarded = new LinkedList<>();
		this.maxBacklogSize = globalConf.getInt(MACHINE_BACKLOGSIZE);
		this.semaphore = new FastSemaphore(this.descriptor.getNumberOfCPUCores());
	}
	
	@Override
	public void reconfigure(InstanceDescriptor descriptor) {
		System.out.println("SingleQueueMachine.reconfigure() not yet implemented.");
	}

	@Override
	public void queue(final Request request) {
		
		arrived++;
		
		if(backlog.size() < maxBacklogSize){
			if(semaphore.tryAcquire()){
				scheduler.queueEvent(new Event(scheduler.now() + request.getCPUTimeDemandInMillis()) {
					@Override
					public void trigger() {
						SingleQueueMachine.this.run(request);
					}
				});
			}else{
				backlog.add(request);
			}
		}else{
			failed++;
			request.getResponseListener().processDone(request, null);
		}
	}
	
	protected void run(Request request) {
		
		if(!descriptor.isOn()){
			failed++;
			request.getResponseListener().processDone(request, null);
		}else{
			request.updateServiceTime(request.getCPUTimeDemandInMillis());
			forward(request, null);
		}
		
		if(!backlog.isEmpty()){
			final Request newRequest = backlog.poll();
			scheduler.queueEvent(new Event(scheduler.now() + newRequest.getCPUTimeDemandInMillis()) {
				@Override
				public void trigger() {
					SingleQueueMachine.this.run(newRequest);
				}
			});
		}else{
			semaphore.release();
		}
	}

	private void forward(Request request, Response response) {
		if(getNextTier() != null && shouldForward()){
			request.setResponseListener(this);
			getNextTier().queue(request);
			forwarded.add(request);
		}else{
			request.getResponseListener().processDone(request, response);
		}
	}


	@Override
	public boolean shouldForward() {
		return false;
	}

	@Override
	public long getStartUpDelay() {
		return this.startUpDelay;
	}
	
	@Override
	public Tier getNextTier() {
		return nextTier;
	}

	@Override
	public void processDone(Request request, Response response) {
		forwarded.remove(request);

		if(!descriptor.isOn()){
			failed++;
			request.getResponseListener().processDone(request, null);
		}else{
			forward(request, response);
		}
	}


	@Override
	public void setNextTier(Tier nextTier) {
		this.nextTier = nextTier;
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
		
		info.put("arrivalrate", (double) arrived);
		info.put("failurerate", (double) failed);
		
		arrived = 0;
		failed = 0;
		
		return info;
	}
}
