package commons.sim.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import commons.util.Triple;

/**
 * Time sharing machine.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class MultiCoreTimeSharedMachine extends TimeSharedMachine{
	
	protected List<Request> currentExecuting;
	private int NUMBER_OF_CORES = 1;
	
	/**
	 * Default constructor
	 * @param scheduler Event scheduler.
	 * @param descriptor Machine descriptor.
	 * @param loadBalancer {@link LoadBalancer} responsible for this machine.
	 */
	public MultiCoreTimeSharedMachine(JEEventScheduler scheduler, MachineDescriptor descriptor, 
			LoadBalancer loadBalancer) {
		super(scheduler, descriptor, loadBalancer);
		this.NUMBER_OF_CORES = (int) Math.floor(Configuration.getInstance().getRelativePower(descriptor.getType()));
		this.currentExecuting = new ArrayList<Request>(this.NUMBER_OF_CORES); 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendRequest(Request request) {
		this.processorQueue.add(request);
		request.assignTo(descriptor.getType());
		
		if(!this.processorQueue.isEmpty() && this.currentExecuting.size() < this.NUMBER_OF_CORES){
			scheduleNext();
		}
	}

	/**
	 * 
	 */
	@Override
	protected void tryToShutdown() {
		if(processorQueue.isEmpty() && currentExecuting.isEmpty() && shutdownOnFinish){
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
				Request request = (Request) event.getValue()[1];
				currentExecuting.remove(request);
				
				long processedDemand = (Long) event.getValue()[0];
				totalTimeUsed += processedDemand;
				
				lastUpdate = getScheduler().now();
				
				request.update(processedDemand);
				
				if(request.isFinished()){
					descriptor.updateTransference(request.getRequestSizeInBytes(), request.getResponseSizeInBytes());
					requestFinished(request);
				}else{
					processorQueue.add(request);
				}
				
				if(!processorQueue.isEmpty() && currentExecuting.size() < this.NUMBER_OF_CORES){
					scheduleNext();
				}
				
				tryToShutdown();
				
				break;
		}
	}

	/**
	 * Schedule next {@link Request} on the processor queue.
	 */
	@Override
	protected void scheduleNext() {
		Request nextRequest = processorQueue.poll();
		currentExecuting.add(nextRequest);
		long nextQuantum = Math.min(nextRequest.getTotalToProcess(), cpuQuantumInMilis);
		lastUpdate = getScheduler().now();
		send(new JEEvent(JEEventType.PREEMPTION, this, new JETime(nextQuantum).plus(lastUpdate), nextQuantum, nextRequest));
	}
	
	@Override
	public boolean isBusy() {
		return this.processorQueue.size() != 0 || this.currentExecuting.size() != 0;
	}	

	/**
	 * This method estimates CPU utilisation of current machine
	 * @param timeInMillis
	 * @return
	 */
	@Override
	public double computeUtilisation(long timeInMillis){
		if(processorQueue.isEmpty() && currentExecuting.isEmpty()){
			double utilisation = (1.0 * totalTimeUsed)/((timeInMillis - lastUtilisationCalcTime) * this.NUMBER_OF_CORES);
			totalTimeUsed = 0;
			lastUtilisationCalcTime = timeInMillis;
			return utilisation;
		}
		
		//FIXME: Suponha que aconteca um preemption e a request continue executando, a diferenca nao eh mais essa certo?
		long totalBeingProcessedNow = (timeInMillis - lastUpdate.timeMilliSeconds);
		totalBeingProcessedNow *= this.currentExecuting.size();
		
		double utilisation = (1.0* (totalTimeUsed + totalBeingProcessedNow) )/((timeInMillis - lastUtilisationCalcTime) * this.NUMBER_OF_CORES);
		totalTimeUsed = -totalBeingProcessedNow;
		lastUtilisationCalcTime = timeInMillis;
		return utilisation;
	}
	
	public Queue<Request> getExecutingQueue() {
		return new LinkedList<Request>(currentExecuting);
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

		private MultiCoreTimeSharedMachine getOuterType() {
			return MultiCoreTimeSharedMachine.this;
		}

		@Override
		public String toString() {
			return "Info [finishTimeBefore=" + finishTimeBefore + "]";
		}
	}

	@Override
	public List<Triple<Long, Long, Long>> estimateFinishTime(Request newRequest) {
		//FIXME
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
