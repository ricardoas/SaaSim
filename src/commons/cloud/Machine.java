package commons.cloud;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import commons.sim.OneTierSimulatorForPlanning;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class Machine extends JEEventHandler{
	
	private long id;
	private boolean isReserved;
	protected double totalProcessed;
	
	protected JETime lastProcessingEvaluation;
	protected JEEvent nextFinishEvent;
	
	protected List<Request> queue;
	protected List<Request> finishedRequests;
	
	public int numberOfRequestsCompletionsInPreviousInterval;
	public int numberOfRequestsArrivalsInPreviousInterval;
	
	/**
	 * @param id
	 */
	public Machine(long id) {
		this.id = id;
		this.queue = new ArrayList<Request>();
		this.finishedRequests = new ArrayList<Request>();
		this.lastProcessingEvaluation = new JETime(0);
		this.isReserved = false;
		this.totalProcessed = 0;
	}

	public Machine(long id, boolean isReserved){
		this(id);
		this.isReserved = isReserved;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		if (id != other.id)
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
		
		if(nextFinishEvent != null){//Should evaluate next finish time
			JETime estimatedFinishTime = new JETime(request.demand * requestsToShare); 
			estimatedFinishTime = estimatedFinishTime.plus(JEEventScheduler.SCHEDULER.now());
			
			if(estimatedFinishTime.isEarlierThan(nextFinishEvent.getScheduledTime())){
				JEEventScheduler.SCHEDULER.cancelEvent(nextFinishEvent);
				JEEvent currentFinish = new JEEvent(JEEventType.FINISHREQUEST, this, estimatedFinishTime, null);
				JEEventScheduler.SCHEDULER.queueEvent(currentFinish);
				this.nextFinishEvent = currentFinish;
			}
		}else{//Only one request is in this machine
			JETime eventTime = new JETime(request.demand); 
			eventTime = eventTime.plus(JEEventScheduler.SCHEDULER.now());
			JEEvent nextFinish = new JEEvent(JEEventType.FINISHREQUEST, this, eventTime, null);
			this.nextFinishEvent = nextFinish;
			
			JEEventScheduler.SCHEDULER.queueEvent(nextFinish);
		}
		
		this.numberOfRequestsArrivalsInPreviousInterval++;
	}
	
	public void resetCounters(){
		this.numberOfRequestsArrivalsInPreviousInterval = 0;
		this.numberOfRequestsCompletionsInPreviousInterval = 0;
	}
	
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
					this.totalProcessed += totalProcessingTime.timeMilliSeconds;//Accounting
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
					JEEvent currentFinish = new JEEvent(JEEventType.FINISHREQUEST, this, nextFinishTime, null);
					JEEventScheduler.SCHEDULER.queueEvent(currentFinish);
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

	
	public double calcExecutionTime() {
		if(this.totalProcessed < 0){
			throw new RuntimeException("Invalid resource "+this.id+" execution time: "+this.totalProcessed);
		}
		return this.totalProcessed;
	}
}
