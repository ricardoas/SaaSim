package saasim.ext.infrastructure;

import java.util.LinkedList;
import java.util.Queue;

import saasim.core.application.Request;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.LoadBalancer;
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

	@Inject
	public SingleQueueMachine(@Assisted InstanceDescriptor descriptor, Monitor monitor, EventScheduler scheduler) {
		this.descriptor = descriptor;
		this.monitor = monitor;
		this.scheduler = scheduler;
		this.startUpDelay = 30000;
		this.backlog = new LinkedList<>();
		this.maxBacklogSize = 1024;
		this.semaphore = new FastSemaphore(this.descriptor.getNumberOfCPUCores());
	}

	@Override
	public void reconfigure(InstanceDescriptor descriptor) {
		// TODO Auto-generated method stub
		System.out.println("SingleQueueMachine.reconfigure()");
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
			monitor.requestFailed(request);
		}
	}
	
	protected void run(Request request) {
		
		request.updateServiceTime(request.getCPUTimeDemandInMillis());
		request.setFinishTime(scheduler.now());
		
		monitor.requestFinished(request);
		
		if(!backlog.isEmpty()){
			final Request newRequest = backlog.poll();
			scheduler.queueEvent(new Event(scheduler.now() + newRequest.getArrivalTimeInMillis()) {
				@Override
				public void trigger() {
					SingleQueueMachine.this.run(newRequest);
				}
			});
		}else{
			semaphore.release();
		}
	}

	@Override
	public long getStartUpDelay() {
		return this.startUpDelay;
	}

	
	
	
	
	
	

	@Override
	public LoadBalancer getLoadBalancer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Queue<Request> getProcessorQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InstanceDescriptor getDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdownOnFinish() {
		// TODO Auto-generated method stub

	}

	@Override
	public double computeUtilisation(long timeInMillis) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTotalTimeUsed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getNumberOfCores() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void cancelShutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdownNow() {
		// TODO Auto-generated method stub

	}

	
}
