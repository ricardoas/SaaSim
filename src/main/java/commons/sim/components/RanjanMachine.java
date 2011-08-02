package commons.sim.components;

import java.util.ArrayList;
import java.util.List;

import commons.cloud.Request;
import commons.config.SimulatorConfiguration;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;

/**
 * This class represents a Machine/Server in the context of the Ranjan Provisionning Heuristic. In this 
 * context a machine uses a pool of threads to process requests and it also has a backlog queue that is 
 * used to store requests waiting for a thread.
 * 
 * @author David Candeia
 *
 */
public class RanjanMachine extends Machine {
	
	private long maximumNumberOfSimultaneousThreads;
	private long backlogMaximumNumberOfRequests;
	private List<Request> backlog;//This list represents the set of requests waiting to be processed by a thread in this server
	
	/**
	 * @see commons.sim.components.Machine
	 */
	public RanjanMachine(JEEventScheduler scheduler, long machineID){
		super(scheduler, machineID);
		this.maximumNumberOfSimultaneousThreads = SimulatorConfiguration.getInstance().getMaximumNumberOfThreadsPerMachine();
		this.backlogMaximumNumberOfRequests = SimulatorConfiguration.getInstance().getMaximumBacklogSize();
		this.backlog = new ArrayList<Request>();
	}
	
	/**
	 * @see commons.sim.components.Machine
	 */
	public RanjanMachine(JEEventScheduler scheduler, long id, boolean isReserved){
		super(scheduler, id, isReserved);
		this.maximumNumberOfSimultaneousThreads = SimulatorConfiguration.getInstance().getMaximumNumberOfThreadsPerMachine();
		this.backlogMaximumNumberOfRequests = SimulatorConfiguration.getInstance().getMaximumBacklogSize();
		this.backlog = new ArrayList<Request>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendRequest(Request request) {
		if(this.queue.size() < this.maximumNumberOfSimultaneousThreads){//Can process new request
			super.sendRequest(request);
		}else{//Number of maximum threads allowed was alreay achieved!
			if(this.backlog.size() < this.backlogMaximumNumberOfRequests){//Request can be added to backlog
				this.backlog.add(request);
			}else{//Request missed!
				send(new JEEvent(JEEventType.REQUESTQUEUED, this.loadBalancer, getScheduler().now(), request));
			}
		}
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
	
				//Since a request finished, now requests from backlog can be transferred to queue to be processed
				while(this.queue.size() < this.maximumNumberOfSimultaneousThreads && this.backlog.size() > 0){
					this.queue.add(this.backlog.remove(0));
				}
				
				if (!queue.isEmpty()) {
					Request nextToFinish = queue.get(0);
					for (Request request : queue) {
						if (request.getTotalToProcess() < nextToFinish.getTotalToProcess()) {
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
	
	@Override
	public double computeUtilization(long currentTime){
		return ((double)this.queue.size()) / this.maximumNumberOfSimultaneousThreads;
	}

	public List<Request> getBacklog(){
		return this.backlog;
	}
}
