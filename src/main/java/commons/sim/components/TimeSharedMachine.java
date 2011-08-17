package commons.sim.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import commons.cloud.Request;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import commons.util.Triple;

/**
 * Time sharing machine.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TimeSharedMachine extends JEAbstractEventHandler implements Machine{
	
	private static final long DEFAULT_QUANTUM = 100;

	private final LoadBalancer loadBalancer;
	protected final Queue<Request> processorQueue;
	private final MachineDescriptor descriptor;
	private final long cpuQuantumInMilis;
	private boolean shutdownOnFinish;
	private long lastUtilisationCalcTime;
	private long totalTimeUsed;
	protected JETime lastUpdate;
	
	protected JEEvent nextFinishEvent;
	
	protected List<Request> finishedRequests;
	
	/**
	 * Default constructor
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
		this.cpuQuantumInMilis = DEFAULT_QUANTUM;
		this.lastUtilisationCalcTime = 0;
		this.lastUpdate = scheduler.now();
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
	public void sendRequest(Request request) {
		this.processorQueue.add(request);
		if(processorQueue.size() == 1){
			scheduleNext();
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
	 * 
	 */
	private void tryToShutdown() {
		if(processorQueue.isEmpty() && shutdownOnFinish){
			descriptor.setFinishTimeInMillis(getScheduler().now().timeMilliSeconds);
			send(new JEEvent(JEEventType.MACHINE_TURNED_OFF, this.loadBalancer, getScheduler().now(), descriptor));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
			case PREEMPTION:
				Request request = processorQueue.poll();
				
				long processedDemand = (Long) event.getValue()[0];
				totalTimeUsed += processedDemand;
				request.update(processedDemand);
				
				if(request.isFinished()){
					descriptor.updateTransference(request.getRequestSizeInBytes(), request.getResponseSizeInBytes());
					requestFinished(request);
				}else{
					processorQueue.add(request);
				}
				
				if(!processorQueue.isEmpty()){
					scheduleNext();
				}
				
				tryToShutdown();
				
				break;
		}
	}

	protected void requestFinished(Request request) {
		getLoadBalancer().reportRequestFinished(request);
	}

	/**
	 * Schedule next {@link Request} on the processor queue.
	 */
	private void scheduleNext() {
		Request nextRequest = processorQueue.peek();
		long nextQuantum = Math.min(nextRequest.getTotalToProcess(), cpuQuantumInMilis);
		lastUpdate = getScheduler().now();
		send(new JEEvent(JEEventType.PREEMPTION, this, new JETime(nextQuantum).plus(lastUpdate), nextQuantum));
	}
	
	public boolean isBusy() {
		return this.processorQueue.size() != 0;
	}	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((descriptor == null) ? 0 : descriptor.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeSharedMachine other = (TimeSharedMachine) obj;
		if (descriptor == null) {
			if (other.descriptor != null)
				return false;
		} else if (!descriptor.equals(other.descriptor))
			return false;
		return true;
	}

	public String toString(){
		return getClass().getName() + " " + descriptor;
	}
	
	/**
	 * This method estimates CPU utilisation of current machine
	 * @param timeInMillis
	 * @return
	 */
	public double computeUtilisation(long timeInMillis){
		if(processorQueue.isEmpty()){
			double utilisation = (1.0 * totalTimeUsed)/(timeInMillis - lastUtilisationCalcTime);
			totalTimeUsed = 0;
			lastUtilisationCalcTime = timeInMillis;
			return utilisation;
		}
		
		long totalBeingProcessedNow = timeInMillis - lastUpdate.timeMilliSeconds;
		double utilisation = (1.0* (totalTimeUsed + totalBeingProcessedNow) )/(timeInMillis-lastUtilisationCalcTime);
		totalTimeUsed = -totalBeingProcessedNow;
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

	@Override
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
			Info info = times.get(request);
			long demand = Math.min(cpuQuantumInMilis, request.getTotalToProcess()-info.processedDemand);
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
