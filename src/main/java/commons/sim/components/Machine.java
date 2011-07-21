package commons.sim.components;

import java.util.ArrayList;
import java.util.Iterator;
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
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class Machine extends JEAbstractEventHandler implements JEEventHandler{
	
	private final long machineID;
	private boolean isReserved;
	private double totalProcessed;
	
	protected JETime lastProcessingEvaluation;
	protected JEEvent nextFinishEvent;
	
	protected List<Request> queue;
	protected List<Request> finishedRequests;
	
	public int numberOfRequestsCompletionsInPreviousInterval;
	public int numberOfRequestsArrivalsInPreviousInterval;
	private boolean shutdownOnFinish;
	
	/**
	 * @param machineID
	 */
	public Machine(JEEventScheduler scheduler, long machineID) {
		super(scheduler);
		this.machineID = machineID;
		this.queue = new ArrayList<Request>();
		this.finishedRequests = new ArrayList<Request>();
		this.lastProcessingEvaluation = new JETime(0);
		this.isReserved = false;
		this.setTotalProcessed(0);
	}

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
	
	public int getNumberOfRequestsCompletionsInPreviousInterval() {
		return numberOfRequestsCompletionsInPreviousInterval;
	}

	public int getNumberOfRequestsArrivalsInPreviousInterval() {
		return numberOfRequestsArrivalsInPreviousInterval;
	}

	public void sendRequest(Request request) {
		this.queue.add(request);
		int requestsToShare = this.queue.size();
		
		//FIXME: Should compute time from previous arrival until now before adding a new request!
		if(nextFinishEvent != null){//Should evaluate next finish time
			JETime estimatedFinishTime = new JETime(request.demand * requestsToShare); 
			estimatedFinishTime = estimatedFinishTime.plus(getScheduler().now());
			
			if(estimatedFinishTime.isEarlierThan(nextFinishEvent.getScheduledTime())){
				getScheduler().cancelEvent(nextFinishEvent);
				JEEvent currentFinish = new JEEvent(JEEventType.FINISHREQUEST, this, estimatedFinishTime);
				getScheduler().queueEvent(currentFinish);
				this.nextFinishEvent = currentFinish;
			}
		}else{//Only one request is in this machine
			JETime eventTime = new JETime(request.demand); 
			eventTime = eventTime.plus(getScheduler().now());
			JEEvent nextFinish = new JEEvent(JEEventType.FINISHREQUEST, this, eventTime);
			this.nextFinishEvent = nextFinish;
			
			getScheduler().queueEvent(nextFinish);
		}
		
		this.numberOfRequestsArrivalsInPreviousInterval++;
	}
	
	public void resetCounters(){
		this.numberOfRequestsArrivalsInPreviousInterval = 0;
		this.numberOfRequestsCompletionsInPreviousInterval = 0;
	}
	
	/**
	 * Used by RANJAN Scheduler, this method estimates CPU utilization of current machine
	 * @param currentTime
	 * @return
	 */
	public double computeUtilization(long currentTime){
		if(this.queue.size() != 0){//Requests need to be processed, so resource is full
			return 1.0;
		}else{//Requests were processed previously, and no pending requests exist
			if(currentTime >= OneTierSimulatorForPlanning.UTILIZATION_EVALUATION_PERIOD){
				double difference = this.lastProcessingEvaluation.timeMilliSeconds - (currentTime - OneTierSimulatorForPlanning.UTILIZATION_EVALUATION_PERIOD);
				if(difference <= 0){
					return 0.0;
				}else{
					return difference/OneTierSimulatorForPlanning.UTILIZATION_EVALUATION_PERIOD;
				}
			}
		}
		return 0.0;
	}
	
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
			case FINISHREQUEST:
				//Debit processing time in all queued requests
				JETime totalProcessingTime = new JETime(0);
				totalProcessingTime = totalProcessingTime.plus(event.getScheduledTime());
				totalProcessingTime = totalProcessingTime.minus(lastProcessingEvaluation);
				totalProcessingTime = totalProcessingTime.divide(this.queue.size());
				
				Iterator<Request> iterator = this.queue.iterator();
				while(iterator.hasNext()){
					Request request = iterator.next();
					request.process(totalProcessingTime.timeMilliSeconds);
					if(request.isFinished()){
						this.numberOfRequestsCompletionsInPreviousInterval++;
						this.finishedRequests.add(request);
						iterator.remove();
					}
					this.setTotalProcessed(this.getTotalProcessed()
							+ totalProcessingTime.timeMilliSeconds);//Accounting
				}
				this.lastProcessingEvaluation = event.getScheduledTime();
				
				//Searching for next finish event
				JETime nextFinishTime = JETime.INFINITY;
				for(Request nextRequest : this.queue){
					JETime estimatedFinishTime = new JETime(nextRequest.getTotalToProcess() * this.queue.size()); 
					estimatedFinishTime = estimatedFinishTime.plus(event.getScheduledTime());
					
					if(estimatedFinishTime.isEarlierThan(nextFinishTime)){
						nextFinishTime = estimatedFinishTime;
					}
				}
				if(nextFinishTime != JETime.INFINITY){//Scheduling next finish event, if it exists
					JEEvent currentFinish = new JEEvent(JEEventType.FINISHREQUEST, this, nextFinishTime);
					getScheduler().queueEvent(currentFinish);
					this.nextFinishEvent = currentFinish;
				}else{
					this.nextFinishEvent = null;
				}
				
				break;
		}
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

	public String toString(){
		return "Mac "+this.machineID;
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
			triple.thirdValue = currentRequest.demand;
			
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
}
