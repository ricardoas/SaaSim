package saasim.ext.infrastructure;

import java.util.LinkedList;
import java.util.Queue;

import saasim.core.application.Request;
import saasim.core.application.Response;
import saasim.core.application.Tier;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Machine;
import saasim.core.infrastructure.Monitor;
import saasim.core.util.FastSemaphore;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class SingleQueueMachine implements Machine {
	
	
	private InstanceDescriptor descriptor;
	private long startUpDelay;
	
	private Queue<Request> backlog;
	private Monitor monitor;
	private int maxBacklogSize;
	private EventScheduler scheduler;
	private FastSemaphore semaphore;
	private Tier nextTier;
	private Queue<Request> forwarded;
	
	@Inject
	public SingleQueueMachine(@Assisted InstanceDescriptor descriptor, Monitor monitor, EventScheduler scheduler, Configuration configuration) {
		this.descriptor = descriptor;
		this.descriptor.setMachine(this);
		
		this.monitor = monitor;
		
		this.scheduler = scheduler;
		this.startUpDelay = configuration.getLong("machine.setuptime");
		this.backlog = new LinkedList<>();
		this.forwarded = new LinkedList<>();
		this.maxBacklogSize = configuration.getInt("machine.backlogsize");
		this.semaphore = new FastSemaphore(this.descriptor.getNumberOfCPUCores());
	}
	
	@Override
	public void reconfigure(InstanceDescriptor descriptor) {
		System.out.println("SingleQueueMachine.reconfigure() not yet implemented.");
	}

	@Override
	public void queue(final Request request) {
		
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
			monitor.requestFailedAtMachine(request, descriptor);
		}
	}
	
	protected void run(Request request) {
		
		if(!descriptor.isOn()){
			monitor.requestFailedAtMachine(request, descriptor);
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
			monitor.requestFailedAtMachine(request, descriptor);
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
			monitor.requestFailedAtMachine(request, descriptor);
		}
		backlog.clear();
	}
}
