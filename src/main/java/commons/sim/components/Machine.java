package commons.sim.components;

import java.util.ArrayList;
import java.util.List;

import commons.cloud.Request;
import commons.sim.OneTierSimulatorForPlanning;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import commons.util.Triple;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class Machine extends JEAbstractEventHandler implements JEEventHandler{

	private final long machineID;
	private boolean isReserved;
	private double totalProcessed;

//	protected JETime lastProcessingEvaluation;
	private JEEvent nextFinishEvent;

	private final List<Request> queue;
	protected List<Request> finishedRequests;

	public int numberOfRequestsCompletionsInPreviousInterval;
	public int numberOfRequestsArrivalsInPreviousInterval;
	private boolean shutdownOnFinish;
	private JETime lastUpdate;
	private LoadBalancer loadBalancer;
	
	//TODO: Create RANJAN MACHINE: backlog, tokens, etc.!


	/**
	 * @param machineID
	 */
	public Machine(JEEventScheduler scheduler, long machineID) {
		super(scheduler);
		this.machineID = machineID;
		this.queue = new ArrayList<Request>();
		this.finishedRequests = new ArrayList<Request>();
//		this.lastProcessingEvaluation = new JETime(0);
		this.isReserved = false;
		this.setTotalProcessed(0);
		this.shutdownOnFinish = false;
		this.lastUpdate = new JETime(0);
		
		this.numberOfRequestsArrivalsInPreviousInterval = 0;
		this.numberOfRequestsCompletionsInPreviousInterval = 0;
	}

	/**
	 * @param scheduler
	 * @param id
	 * @param isReserved
	 */
	public Machine(JEEventScheduler scheduler, long id, boolean isReserved){
		this(scheduler, id);
		this.isReserved = isReserved;
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
	private JETime getCorrectedFinishTime(Request nextRequestToFinish) {
		return new JETime( getScheduler().now().timeMilliSeconds + nextRequestToFinish.getTotalToProcess() * queue.size());
	}

	/**
	 * 
	 */
	private void updateFinishedDemand() {
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
	private JETime calcEstimatedFinishTime(Request request, int queueSize) {
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
			
			getLoadBalancer().report(requestFinished);//Asking for accounting of a finished request

			if (!queue.isEmpty()) {
				Request nextToFinish = queue.get(0);
				for (Request request : queue) {
					if (request.getTotalToProcess() < nextToFinish
							.getTotalToProcess()) {
						nextToFinish = request;
					}
				}
				send(new JEEvent(JEEventType.REQUEST_FINISHED, this,
						calcEstimatedFinishTime(nextToFinish, queue.size()),
						nextToFinish));
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
		Machine other = (Machine) obj;
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
	 * Used by RANJAN Scheduler, this method estimates CPU utilization of current machine
	 * @param currentTime
	 * @return
	 */
	@Deprecated
	public double computeUtilization(long currentTime){
		if(this.queue.size() != 0){//Requests need to be processed, so resource is full
			return 1.0;
		}else{//Requests were processed previously, and no pending requests exist
			if(currentTime >= OneTierSimulatorForPlanning.UTILIZATION_EVALUATION_PERIOD){
				double difference = this.lastUpdate.timeMilliSeconds - (currentTime - OneTierSimulatorForPlanning.UTILIZATION_EVALUATION_PERIOD);
				if(difference <= 0){
					return 0.0;
				}else{
					return difference/OneTierSimulatorForPlanning.UTILIZATION_EVALUATION_PERIOD;
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
	public List<Triple<Long, Long, Long>> calcExecutionTimesWithNewRequest(Request request, double sla) {
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

	public void setTotalProcessed(double totalProcessed) {
		this.totalProcessed = totalProcessed;
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
}
