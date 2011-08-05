package commons.sim.components;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import provisioning.RanjanProvisioningSystem;

import commons.cloud.Request;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventTest;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import commons.util.Triple;

/**
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TimeSharedMachine extends JEAbstractEventHandler implements JEEventHandler{
	
	private static final long DEFAULT_QUANTUM = 100;

	private final LoadBalancer loadBalancer;
	private final Queue<Request> processorQueue;
	private final MachineDescriptor descriptor;
	private final long cpuQuantumInMilis;
	
	protected double totalProcessed;
	protected JEEvent nextFinishEvent;

	protected List<Request> finishedRequests;

	public int numberOfRequestsCompletionsInPreviousInterval;
	public int numberOfRequestsArrivalsInPreviousInterval;
	protected boolean shutdownOnFinish;
	protected JETime lastUpdate;
	
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
	}
	
	/**
	 * @return
	 */
	public LoadBalancer getLoadBalancer() {
		return loadBalancer;
	}

	/**
	 * @return
	 */
	public Queue<Request> getProcessorQueue() {
		return new LinkedList<Request>(processorQueue);
	}

	/**
	 * @return
	 */
	public MachineDescriptor getDescriptor() {
		return descriptor;
	}

	/**
	 * @param request
	 */
	public void sendRequest(Request request) {
		
		this.processorQueue.add(request);
		
		if(processorQueue.size() == 1){
			long quantum = Math.min(request.getTotalToProcess(), cpuQuantumInMilis);
			send(new JEEvent(JEEventType.PREEMPTION, this, new JETime(quantum), request, quantum));
		}
	}

	/**
	 * 
	 */
	public void shutdownOnFinish() {
		this.shutdownOnFinish = true;
		if(processorQueue.isEmpty()){
			send(new JEEvent(JEEventType.MACHINE_TURNED_OFF, this.loadBalancer, getScheduler().now(), this));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
		case PREEMPTION:
			Request request = (Request) event.getValue()[0];
			Long quantum = (Long) event.getValue()[1];
			request.update(processedDemand)
			
			break;
			case REQUEST_FINISHED:
				updateFinishedDemand();
	
				Request requestFinished = (Request) event.getValue()[0];
				
				processorQueue.remove(requestFinished);
				this.numberOfRequestsCompletionsInPreviousInterval++;
				
				getLoadBalancer().reportRequestFinished(requestFinished);//Asking for accounting of a finished request
	
				if (!processorQueue.isEmpty()) {
					Request nextToFinish = processorQueue.get(0);
					for (Request request : processorQueue) {
						if (request.getTotalToProcess() < nextToFinish
								.getTotalToProcess()) {
							nextToFinish = request;
						}
					}
					
					nextFinishEvent = new JEEvent(JEEventType.REQUEST_FINISHED, this,
							getCorrectedFinishTime(nextToFinish), nextToFinish); 
					send(nextFinishEvent);
				}else{
					if(shutdownOnFinish){
						send(new JEEvent(JEEventType.MACHINE_TURNED_OFF, this.loadBalancer, getScheduler().now(), this));
					}
				}
				break;
		}
	}
	
	/**
	 * This method estimates CPU utilisation of current machine
	 * @param currentTime
	 * @return
	 */
	public double computeUtilization(long currentTime){
		if(this.processorQueue.size() != 0){//Requests need to be processed, so resource is full
			return 1.0;
		}else{//Requests were processed previously, and no pending requests exist
			if(currentTime >= RanjanProvisioningSystem.UTILIZATION_EVALUATION_PERIOD_IN_MILLIS){
				double difference = this.lastUpdate.timeMilliSeconds - (currentTime - RanjanProvisioningSystem.UTILIZATION_EVALUATION_PERIOD_IN_MILLIS);
				if(difference <= 0){
					return 0.0;
				}else{
					return difference/RanjanProvisioningSystem.UTILIZATION_EVALUATION_PERIOD_IN_MILLIS;
				}
			}
		}
		return 0.0;
	}

	public boolean isBusy() {
		return this.processorQueue.size() != 0 && this.nextFinishEvent != null;
	}	

	public boolean isReserved(){
		return this.isReserved;
	}

	/**
	 * Used by profit driven scheduler. Evaluates the delays created after inserting a new request
	 * in current machine queue.
	 * @param request
	 * @param sla 
	 * @return
	 */
	public List<Triple<Long, Long, Long>> calcExecutionTimesWithNewRequest(Request request, double sla) {
		int requestsToShare = this.processorQueue.size();

		List<Triple<Long, Long, Long>> executionTimes = new ArrayList<Triple<Long, Long, Long>>();

		for(Request currentRequest : this.processorQueue){
			Triple<Long, Long, Long> triple = new Triple<Long, Long, Long>();
			JETime estimatedFinishTime = new JETime(currentRequest.getTotalToProcess() * requestsToShare); 
			estimatedFinishTime = estimatedFinishTime.plus(getScheduler().now());
			triple.firstValue = estimatedFinishTime.timeMilliSeconds;
			estimatedFinishTime = new JETime(currentRequest.getTotalToProcess() * (requestsToShare+1)); 
			estimatedFinishTime = estimatedFinishTime.plus(getScheduler().now());
			triple.secondValue = estimatedFinishTime.timeMilliSeconds;
			triple.thirdValue = currentRequest.getDemand();

			executionTimes.add(triple);
		}

		return executionTimes;
	}

	public double getTotalProcessed() {
		return totalProcessed;
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
}
