package saasim.sim.components;

import static saasim.sim.util.SimulatorProperties.*;

import java.util.LinkedList;
import java.util.Queue;

import saasim.cloud.Request;
import saasim.config.Configuration;
import saasim.sim.core.AbstractEventHandler;
import saasim.sim.core.Event;
import saasim.sim.core.EventScheduler;
import saasim.sim.core.EventType;
import saasim.sim.core.HandlingPoint;
import saasim.sim.util.FastSemaphore;


/**
 * Time sharing machine.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.0
 */
public class TimeSharedMachine extends AbstractEventHandler implements Machine{
	
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

	private boolean shutdown;

	private long quantum;

	private boolean enableCorrectionFactor;

	private long correctionFactorIddleness;

	private double correctionFactorValue;
	
	/**
	 * Default constructor.
	 * @param scheduler Event scheduler.
	 * @param descriptor Machine descriptor.
	 * @param loadBalancer {@link LoadBalancer} responsible for this machine.
	 */
	public TimeSharedMachine(EventScheduler scheduler, MachineDescriptor descriptor, 
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
		this.shutdown = false;
		this.quantum = Configuration.getInstance().getLong(MACHINE_QUANTUM, Long.MAX_VALUE);
		if(this.quantum == 0){
			this.quantum = Long.MAX_VALUE;
		}
		this.enableCorrectionFactor = Configuration.getInstance().getBoolean(MACHINE_ENABLE_CORRECTION_FACTOR, false);
		if(enableCorrectionFactor){
			this.correctionFactorIddleness = Configuration.getInstance().getLong(MACHINE_CORRECTION_FACTOR_IDLENESS, 0);
			this.correctionFactorValue = Configuration.getInstance().getDouble(MACHINE_CORRECTION_FACTOR_VALUE, 1);
		}
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
			if(this.enableCorrectionFactor && this.processorQueue.isEmpty()){
//				request.changeDemand(Math.max(1, 1.961530 -0.003983* (now()-lastUpdate)));
				if(now() - lastUpdate> correctionFactorIddleness){
					request.changeDemand(correctionFactorValue);
				}
			}
			this.processorQueue.add(request);
			request.assignTo(descriptor.getType());
			
			if(this.semaphore.tryAcquire()){
				scheduleNext();
			}
		}else if(canQueue()){
			this.backlog.add(request);
		}else{
			send(new Event(EventType.REQUESTQUEUED, getLoadBalancer(), now(), request));
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
	 * Try to shutdown this {@link Machine}, creating an event {@link EventType#MACHINE_TURNED_OFF}.
	 */
	protected void tryToShutdown() {
		if(shutdownOnFinish && !isBusy()){
			long scheduledTime = now();
			descriptor.setFinishTimeInMillis(scheduledTime);
			send(new Event(EventType.MACHINE_TURNED_OFF, this.loadBalancer, scheduledTime, descriptor));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@HandlingPoint(EventType.PREEMPTION)
	public void handlePreemption(long processedDemand, Request request) {
		if(!shutdown){
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
		}else{
			this.semaphore.release();
			descriptor.updateTransference(request.getRequestSizeInBytes(), 0);
			send(new Event(EventType.REQUESTQUEUED, getLoadBalancer(), now(), request));
		}
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
		request.setFinishTime(now());
		descriptor.updateTransference(request.getRequestSizeInBytes(), request.getResponseSizeInBytes());
		getLoadBalancer().reportRequestFinished(request);
	}

	/**
	 * Schedule next {@link Request} on the processor queue.
	 */
	protected void scheduleNext() {
		Request nextRequest = processorQueue.poll();
		long nextQuantum = Math.min(nextRequest.getTotalToProcess(), quantum);
		lastUpdate = now();
		send(new Event(EventType.PREEMPTION, this, nextQuantum+lastUpdate, nextQuantum, nextRequest));
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
	public void shutdownNow() {
		shutdown = true;
		long scheduledTime = now();
		descriptor.setFinishTimeInMillis(scheduledTime);
		send(new Event(EventType.MACHINE_TURNED_OFF, this.loadBalancer, scheduledTime, descriptor));
		
		for (Request request : processorQueue) {
			descriptor.updateTransference(request.getRequestSizeInBytes(), 0);
			send(new Event(EventType.REQUESTQUEUED, getLoadBalancer(), now(), request));
		}
	}
}
