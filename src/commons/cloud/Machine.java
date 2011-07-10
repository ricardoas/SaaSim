package commons.cloud;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private JETime lastProcessingEvaluation;
	private JEEvent nextFinishEvent;
	
	private List<Request> queue;
	private List<Request> finishedRequests;
	
	
	
	/**
	 * @param id
	 */
	public Machine(long id) {
		this.id = id;
		this.queue = new ArrayList<Request>();
		this.lastProcessingEvaluation = new JETime(0);
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

	public void sendRequest(Request request) {
		// TODO Evaluate finish time, schedule first finish time.
		int requestsToShare = this.queue.size();
		if(nextFinishEvent != null){//Should evaluate next finish time
			JETime estimatedFinishTime = new JETime(request.time); 
			estimatedFinishTime = estimatedFinishTime.plus(JEEventScheduler.SCHEDULER.now());
			estimatedFinishTime = estimatedFinishTime.multiply(requestsToShare);
			
			if(estimatedFinishTime.isEarlierThan(nextFinishEvent.getScheduledTime())){
				JEEventScheduler.SCHEDULER.cancelEvent(nextFinishEvent);
				JEEvent currentFinish = new JEEvent(JEEventType.FINISHREQUEST, this, estimatedFinishTime, null);
				JEEventScheduler.SCHEDULER.queueEvent(currentFinish);
				this.nextFinishEvent = currentFinish;
			}
			
			
		}else{//Only one request is in this machine
			JETime eventTime = new JETime(request.time); 
			eventTime = eventTime.plus(JEEventScheduler.SCHEDULER.now());
			JEEvent nextFinish = new JEEvent(JEEventType.FINISHREQUEST, this, eventTime, null);
			this.nextFinishEvent = nextFinish;
			
			JEEventScheduler.SCHEDULER.queueEvent(nextFinish);
		}
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
					this.finishedRequests.add(request);
					iterator.remove();
				}
			}
			this.lastProcessingEvaluation = event.getScheduledTime();
			
			//Scheduling next finish event
			JETime nextFinishTime = JETime.INFINITY;
			for(Request nextRequest : this.queue){
				JETime estimatedFinishTime = new JETime(nextRequest.time); 
				estimatedFinishTime = estimatedFinishTime.plus(JEEventScheduler.SCHEDULER.now());
				estimatedFinishTime = estimatedFinishTime.multiply(this.queue.size());
				
				if(estimatedFinishTime.isEarlierThan(nextFinishTime)){
					nextFinishTime = estimatedFinishTime;
				}
			}
			if(nextFinishTime != JETime.INFINITY){
				JEEvent currentFinish = new JEEvent(JEEventType.FINISHREQUEST, this, nextFinishTime, null);
				JEEventScheduler.SCHEDULER.queueEvent(currentFinish);
				this.nextFinishEvent = currentFinish;
			}
			
			break;
		}
	}	
	
	
}
