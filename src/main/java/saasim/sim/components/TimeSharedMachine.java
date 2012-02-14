package saasim.sim.components;

import static saasim.sim.util.SimulatorProperties.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import saasim.cloud.Request;
import saasim.config.Configuration;
import saasim.sim.jeevent.JEAbstractEventHandler;
import saasim.sim.jeevent.JEEvent;
import saasim.sim.jeevent.JEEventScheduler;
import saasim.sim.jeevent.JEEventType;
import saasim.sim.jeevent.JEHandlingPoint;
import saasim.sim.util.FastSemaphore;
import saasim.util.Triple;


/**
 * Time sharing machine.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.0
 */
public class TimeSharedMachine extends JEAbstractEventHandler implements Machine{
	
	/**
	 * Version 1.0
	 */
	private static final long serialVersionUID = 368994926091198804L;
	
	protected int NUMBER_OF_CORES = 1;

	protected LoadBalancer loadBalancer;
	protected final Queue<Request> processorQueue;
	protected final MachineDescriptor descriptor;
	protected boolean shutdownOnFinish;
	protected long lastUtilisationCalcTime;
	protected long totalTimeUsedInLastPeriod;
	protected long totalTimeUsed;
	protected long lastUpdate;
	
	protected FastSemaphore semaphore;
	protected long maxThreads;
	protected long maxBacklogSize;
	protected Queue<Request> backlog;

	private long maxOnQueue;
	
	/**
	 * Default constructor.
	 * @param scheduler Event scheduler.
	 * @param descriptor Machine descriptor.
	 * @param loadBalancer {@link LoadBalancer} responsible for this machine.
	 */
	public TimeSharedMachine(JEEventScheduler scheduler, MachineDescriptor descriptor, 
			LoadBalancer loadBalancer) {
		super(scheduler);
		this.descriptor = descriptor;
		this.loadBalancer = loadBalancer;
		this.processorQueue = new LinkedList<Request>();
		this.lastUtilisationCalcTime = scheduler.now();
		this.totalTimeUsed = 0;
		this.totalTimeUsedInLastPeriod = 0;
		this.lastUpdate = scheduler.now();
		this.NUMBER_OF_CORES = descriptor.getType().getNumberOfCores();
		this.semaphore = new FastSemaphore(this.NUMBER_OF_CORES);
		this.maxThreads = Long.MAX_VALUE;
		this.maxBacklogSize = 0;
		this.maxThreads = Configuration.getInstance().getLong(MACHINE_NUMBER_OF_TOKENS, Long.MAX_VALUE);
		this.maxBacklogSize = Configuration.getInstance().getLong(MACHINE_BACKLOG_SIZE, 0);
		this.backlog = new LinkedList<Request>();
		this.maxOnQueue = maxThreads - NUMBER_OF_CORES;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoadBalancer getLoadBalancer() {
		return loadBalancer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Queue<Request> getProcessorQueue() {
		return new LinkedList<Request>(processorQueue);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MachineDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTotalTimeUsed(){
		return this.totalTimeUsed;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfCores() {
		return this.NUMBER_OF_CORES;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendRequest(Request request) {
		if(canRun()){
			this.processorQueue.add(request);
			request.assignTo(descriptor.getType());
			
			if(this.semaphore.tryAcquire()){
				scheduleNext();
			}
		}else if(canQueue()){
			this.backlog.add(request);
		}else{
			send(new JEEvent(JEEventType.REQUESTQUEUED, getLoadBalancer(), now(), request));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void shutdownOnFinish() {
		this.shutdownOnFinish = true;
		tryToShutdown();
	}

	/**
	 * Try to shutdown this {@link Machine}, creating an event {@link JEEventType#MACHINE_TURNED_OFF}.
	 */
	protected void tryToShutdown() {
		if(shutdownOnFinish && !isBusy()){
			long scheduledTime = now();
			descriptor.setFinishTimeInMillis(scheduledTime);
			send(new JEEvent(JEEventType.MACHINE_TURNED_OFF, this.loadBalancer, scheduledTime, descriptor));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@JEHandlingPoint(JEEventType.PREEMPTION)
	public void handlePreemption(long processedDemand, Request request) {
		this.semaphore.release();

		totalTimeUsedInLastPeriod += processedDemand;
		totalTimeUsed += processedDemand;

		lastUpdate = now();

		request.update(processedDemand);

		if(request.isFinished()){
			requestFinished(request);
		}else{
			processorQueue.add(request);
		}

		if(!processorQueue.isEmpty() && this.semaphore.tryAcquire()){
			scheduleNext();
		}

		tryToShutdown();
	}
	
	/**
	 * Finishes a specific {@link Request} and adds a new request coming from backlog in processor queue, if it exists. 
	 * @param request {@link Request} to be finish.
	 */
	protected void requestFinished(Request request) {
		if(!backlog.isEmpty()){
			Request newRequestToAdd = backlog.poll();
			newRequestToAdd.assignTo(this.descriptor.getType());
			processorQueue.add(newRequestToAdd);
		}
//		if(getScheduler().now() - request.getArrivalTimeInMillis() > 
//		Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)){
//			System.out.println("TimeSharedMachine.requestFinished()");
//		}
		//}else{
			request.setFinishTime(now());
			descriptor.updateTransference(request.getRequestSizeInBytes(), request.getResponseSizeInBytes());
			getLoadBalancer().reportRequestFinished(request);
		//}
	}

	/**
	 * Schedule next {@link Request} on the processor queue.
	 */
	protected void scheduleNext() {
		Request nextRequest = processorQueue.poll();
		long nextQuantum = Math.min(nextRequest.getTotalToProcess(), Configuration.getInstance().getPriorities()[nextRequest.getSaasClient()]);
		lastUpdate = now();
		send(new JEEvent(JEEventType.PREEMPTION, this, nextQuantum+lastUpdate, nextQuantum, nextRequest));
	}
	
	/**
	 * Verifies if this {@link TimeSharedMachine} is busy, based on the conditions about processor queue and permission of semaphore.
	 * @return <code>true</code> if the machine is busy, <code>false</code> otherwise.
	 */
	public boolean isBusy() {
		return !this.processorQueue.isEmpty() || this.semaphore.availablePermits() != this.NUMBER_OF_CORES;

	}
	
	/**
	 * Verifies if exists an available thread to process a new {@link Request}.
	 * @return <code>true</code> when there is an available thread to process this request, 
	 * and <code>false</code> otherwise.
	 */
	protected boolean canRun() {
		return processorQueue.size() != maxOnQueue;
	}
	
	/**
	 * Verifies if exists space available on the backlog.
	 * @return <code>true</code> if there is free space available at the backlog queue, 
	 * and <code>false</code> otherwise.
	 */
	protected boolean canQueue() {
		return this.backlog.size() < maxBacklogSize;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cancelShutdown() {
		shutdownOnFinish = false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public double computeUtilisation(long timeInMillis){
		if(processorQueue.isEmpty() && this.semaphore.availablePermits() == this.NUMBER_OF_CORES){
			double utilisation = (1.0 * totalTimeUsedInLastPeriod)/((timeInMillis - lastUtilisationCalcTime) * this.NUMBER_OF_CORES);
			totalTimeUsedInLastPeriod = 0;
			lastUtilisationCalcTime = timeInMillis;
			return utilisation;
		}
		
		long totalBeingProcessedNow = (timeInMillis - lastUpdate);
		totalBeingProcessedNow *= (this.NUMBER_OF_CORES - this.semaphore.availablePermits());
		
		double utilisation = (1.0* (totalTimeUsedInLastPeriod + totalBeingProcessedNow) )/((timeInMillis - lastUtilisationCalcTime) * this.NUMBER_OF_CORES);
		totalTimeUsedInLastPeriod = -totalBeingProcessedNow;
		lastUtilisationCalcTime = timeInMillis;
		return utilisation;
	}
	
	private class Info{
		private final Request request;
		private long finishTimeBefore;
		private long finishTimeAfter;
		private long processedDemand;

		public Info(Request request) {
			this.request = request;
			this.processedDemand = 0;
			this.finishTimeBefore = 0;
			this.finishTimeAfter = 0;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((request == null) ? 0 : request.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Info other = (Info) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (request == null) {
				if (other.request != null)
					return false;
			} else if (!request.equals(other.request))
				return false;
			return true;
		}

		private TimeSharedMachine getOuterType() {
			return TimeSharedMachine.this;
		}

		@Override
		public String toString() {
			return "Info [finishTimeBefore=" + finishTimeBefore + "]";
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(){
		return getClass().getName() + ": " + descriptor;
	}
	
	@Override
	@Deprecated
	public List<Triple<Long, Long, Long>> estimateFinishTime(Request newRequest) {
		
		List<Triple<Long, Long, Long>> executionTimes = new ArrayList<Triple<Long, Long, Long>>();
		Map<Request, Info> times = new HashMap<Request, Info>();
		Queue<Request> queue = getProcessorQueue();
		for (Request request : queue) {
			times.put(request, new Info(request));
		}
		
		long processedTime = 0;
		
		while(!queue.isEmpty()){
			Request request = queue.poll();
			int priority = Configuration.getInstance().getPriorities()[request.getSaasClient()];
			Info info = times.get(request);
			long demand = Math.min(priority, request.getTotalToProcess()-info.processedDemand);
			processedTime += demand;
			info.processedDemand += demand;
			if(request.getTotalToProcess() - info.processedDemand == 0){
				info.finishTimeBefore = processedTime;
			}else{
				queue.add(request);
			}
		}
		return executionTimes;
	}
}
