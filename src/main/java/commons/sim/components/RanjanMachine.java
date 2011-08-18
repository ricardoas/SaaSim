package commons.sim.components;

import static commons.sim.util.SimulatorProperties.*;
import static commons.sim.util.SimulatorProperties.RANJAN_HEURISTIC_BACKLOG_SIZE;

import java.util.LinkedList;
import java.util.Queue;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;

/**
 * This class represents a Machine/Server in the context of the Ranjan Provisionning Heuristic. In this 
 * context a machine uses a pool of threads to process requests and it also has a backlog queue that is 
 * used to store requests waiting for a thread.
 * 
 * @author David Candeia
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class RanjanMachine extends TimeSharedMachine {
	
	private Queue<Request> backlog;
	
	protected long maximumNumberOfSimultaneousThreads;
	protected long backlogMaximumNumberOfRequests;
	
	/**
	 * @see commons.sim.components.ProcessorSharedMachine
	 */
	public RanjanMachine(JEEventScheduler scheduler, MachineDescriptor descriptor, LoadBalancer loadBalancer){
		super(scheduler, descriptor, loadBalancer);
		this.backlog = new LinkedList<Request>();
		this.maximumNumberOfSimultaneousThreads = Configuration.getInstance().getLong(RANJAN_HEURISTIC_NUMBER_OF_TOKENS);
		this.backlogMaximumNumberOfRequests = Configuration.getInstance().getLong(RANJAN_HEURISTIC_BACKLOG_SIZE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendRequest(Request request) {
		if(hasTokenLeft()){
			super.sendRequest(request);
		}else if(canWaitForToken()){
			this.backlog.add(request);
		}else{
			send(new JEEvent(JEEventType.REQUESTQUEUED, getLoadBalancer(), getScheduler().now(), request));
		}
	}

	private boolean hasTokenLeft() {
		return processorQueue.size() < maximumNumberOfSimultaneousThreads;
	}
	
	private boolean canWaitForToken() {
		return this.backlog.size() < backlogMaximumNumberOfRequests;
	}
	
	@Override
	protected void requestFinished(Request request) {
		if(!backlog.isEmpty()){
			processorQueue.add(backlog.poll());
		}
		super.requestFinished(request);
	}
	
	@Override
	public double computeUtilisation(long currentTime){
		return ((double)processorQueue.size()) / this.maximumNumberOfSimultaneousThreads;
	}
}
