package commons.sim.components;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import provisioning.RanjanProvisioningSystem;

import commons.cloud.Request;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import commons.util.Triple;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class ProcessorSharedMachine extends JEAbstractEventHandler implements Machine{

	protected final long machineID;
	protected boolean isReserved;
	protected double totalProcessed;

	protected JEEvent nextFinishEvent;

	protected final List<Request> queue;
	protected List<Request> finishedRequests;

	public int numberOfRequestsCompletionsInPreviousInterval;
	public int numberOfRequestsArrivalsInPreviousInterval;
	protected boolean shutdownOnFinish;
	protected JETime lastUpdate;
	protected LoadBalancer loadBalancer;
	
	/**
	 * @param machineID
	 */
	public ProcessorSharedMachine(JEEventScheduler scheduler, MachineDescriptor descriptor, LoadBalancer loadBalancer) {
		super(scheduler);
		this.machineID = descriptor.getMachineID();
		this.queue = new ArrayList<Request>();
		this.finishedRequests = new ArrayList<Request>();
		this.isReserved = descriptor.isReserved();
		this.totalProcessed = 0;
		this.shutdownOnFinish = false;
		this.lastUpdate = new JETime(0);
		this.loadBalancer = loadBalancer;
		this.numberOfRequestsArrivalsInPreviousInterval = 0;
		this.numberOfRequestsCompletionsInPreviousInterval = 0;
	}

	/**
	 * @return the machineID
	 */
	public long getMachineID() {
		return machineID;
	}

	/**
	 * @return
	 */
	public List<Request> getQueue() {
		return queue;
	}

	/**
	 * @param request
	 */
	public void sendRequest(Request request) {

		updateFinishedDemand();
		queue.add(request);

		JEEvent event;
		if(nextFinishEvent == null){
			event = new JEEvent(JEEventType.REQUEST_FINISHED, this, calcEstimatedFinishTime(request, queue.size()), request);
		}else{
			JETime estimatedFinishTime = calcEstimatedFinishTime(request, queue.size());
			JETime correctedFinishTime = getCorrectedFinishTime((Request) nextFinishEvent.getValue()[0]);
			if(estimatedFinishTime.isEarlierThan(correctedFinishTime)){
				event = new JEEvent(JEEventType.REQUEST_FINISHED, this, estimatedFinishTime, request);
			}else{
				event = new JEEvent(JEEventType.REQUEST_FINISHED, this, correctedFinishTime, nextFinishEvent.getValue());
			}
			getScheduler().cancelEvent(nextFinishEvent);
		}
		nextFinishEvent = event;
		send(event);
		this.numberOfRequestsArrivalsInPreviousInterval++;
	}

	/**
	 * TODO check is this correction does not alters the total demand.
	 * @param nextRequestToFinish 
	 * @return
	 */
	protected JETime getCorrectedFinishTime(Request nextRequestToFinish) {
		return new JETime( getScheduler().now().timeMilliSeconds + nextRequestToFinish.getTotalToProcess() * queue.size());
	}

	/**
	 * 
	 */
	protected void updateFinishedDemand() {
		JETime now = getScheduler().now();
		if(lastUpdate.isEarlierThan(now) && !queue.isEmpty()){
			long processedDemand = (now.timeMilliSeconds - lastUpdate.timeMilliSeconds)/(queue.size());
			for (Request request : queue) {
				request.update(processedDemand);
			}
		}
		lastUpdate = now;
	}

	/**
	 * @param request
	 * @param queueSize
	 * @return
	 */
	protected JETime calcEstimatedFinishTime(Request request, int queueSize) {
		return new JETime(request.getDemand() * queueSize).plus(getScheduler().now());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
			case REQUEST_FINISHED:
				updateFinishedDemand();
	
				Request requestFinished = (Request) event.getValue()[0];
				
				queue.remove(requestFinished);
				this.numberOfRequestsCompletionsInPreviousInterval++;
				
				getLoadBalancer().reportRequestFinished(requestFinished);//Asking for accounting of a finished request
	
				if (!queue.isEmpty()) {
					Request nextToFinish = queue.get(0);
					for (Request request : queue) {
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
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (machineID ^ (machineID >>> 32));
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProcessorSharedMachine other = (ProcessorSharedMachine) obj;
		if (machineID != other.machineID)
			return false;
		return true;
	}

	public String toString(){
		return "Mac "+this.machineID;
	}

	@Deprecated
	public int getNumberOfRequestsCompletionsInPreviousInterval() {
		return numberOfRequestsCompletionsInPreviousInterval;
	}

	@Deprecated
	public int getNumberOfRequestsArrivalsInPreviousInterval() {
		return numberOfRequestsArrivalsInPreviousInterval;
	}

	@Deprecated
	public void resetCounters(){
		this.numberOfRequestsArrivalsInPreviousInterval = 0;
		this.numberOfRequestsCompletionsInPreviousInterval = 0;
	}

	/**
	 * This method estimates CPU utilisation of current machine
	 * @param currentTime
	 * @return
	 */
	public double computeUtilisation(long currentTime){
		if(this.queue.size() != 0){//Requests need to be processed, so resource is full
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
		return this.queue.size() != 0 && this.nextFinishEvent != null;
	}	

	public boolean isReserved(){
		return this.isReserved;
	}

	/**
	 * This method retrieves the total amount of cpu processing time of current machine
	 * @return
	 */
	public double calcExecutionTime() {
		if(this.getTotalProcessed() < 0){
			throw new RuntimeException("Invalid resource "+this.machineID+" execution time: "+this.getTotalProcessed());
		}
		return this.getTotalProcessed();
	}

	/**
	 * Used by profit driven scheduler. Evaluates the delays created after inserting a new request
	 * in current machine queue.
	 * @param request
	 * @param sla 
	 * @return
	 */
	@Override
	public List<Triple<Long, Long, Long>> estimateFinishTime(Request request) {
		int requestsToShare = this.queue.size();

		List<Triple<Long, Long, Long>> executionTimes = new ArrayList<Triple<Long, Long, Long>>();

		for(Request currentRequest : this.queue){
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

	public void shutdownOnFinish() {
		this.shutdownOnFinish = true;
	}

	public void setLoadBalancer(LoadBalancer loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

	public LoadBalancer getLoadBalancer() {
		return loadBalancer;
	}

	@Override
	public Queue<Request> getProcessorQueue() {
		return new LinkedList<Request>(queue);
	}

	@Override
	public MachineDescriptor getDescriptor() {
		return null;
	}

}
