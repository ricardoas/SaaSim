package commons.sim.components;

import static commons.sim.util.SimulatorProperties.*;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
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
public class MultiCoreRanjanMachine extends MultiCoreTimeSharedMachine implements Serializable {
	
	private Queue<Request> backlog;
	
	protected long maximumNumberOfSimultaneousThreads;
	protected long backlogMaximumNumberOfRequests;
	
	/**
	 * @see commons.sim.components.ProcessorSharedMachine
	 */
	public MultiCoreRanjanMachine(JEEventScheduler scheduler, MachineDescriptor descriptor, LoadBalancer loadBalancer){
		super(scheduler, descriptor, loadBalancer);
		this.backlog = new LinkedList<Request>();
		this.maximumNumberOfSimultaneousThreads = Configuration.getInstance().getLong(RANJAN_HEURISTIC_NUMBER_OF_TOKENS);
		this.backlogMaximumNumberOfRequests = Configuration.getInstance().getLong(RANJAN_HEURISTIC_BACKLOG_SIZE);
	}
	
	public MultiCoreRanjanMachine(MachineDescriptor descriptor, List<Request> processorQueue, 
			long cpuQuantumInMilis, long lastUtilisationCalcTime, long totalTimeUsed, 
			long lastUpdate, long totalTimeUsedInLastPeriod, Queue<Request> backlog, long maximumNumberOfSimultaneousThreads, long backlogMaximumNumberOfRequests){
		super(descriptor, processorQueue, cpuQuantumInMilis, lastUtilisationCalcTime, totalTimeUsed, lastUpdate, totalTimeUsedInLastPeriod);
		this.backlog = backlog;
		this.maximumNumberOfSimultaneousThreads = maximumNumberOfSimultaneousThreads;
		this.backlogMaximumNumberOfRequests = backlogMaximumNumberOfRequests;
	}
	
	@Override
	public void restart(LoadBalancer loadBalancer, JEEventScheduler scheduler) {
		super.restart(loadBalancer, scheduler);
	}
	
	public Queue<Request> getBacklog() {
		return backlog;
	}

	public void setBacklog(Queue<Request> backlog) {
		this.backlog = backlog;
	}

	public long getMaximumNumberOfSimultaneousThreads() {
		return maximumNumberOfSimultaneousThreads;
	}

	public void setMaximumNumberOfSimultaneousThreads(
			long maximumNumberOfSimultaneousThreads) {
		this.maximumNumberOfSimultaneousThreads = maximumNumberOfSimultaneousThreads;
	}

	public long getBacklogMaximumNumberOfRequests() {
		return backlogMaximumNumberOfRequests;
	}

	public void setBacklogMaximumNumberOfRequests(
			long backlogMaximumNumberOfRequests) {
		this.backlogMaximumNumberOfRequests = backlogMaximumNumberOfRequests;
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
			request = null;
		}
	}

	private boolean hasTokenLeft() {
		return processorQueue.size() + (this.NUMBER_OF_CORES - this.semaphore.availablePermits()) < maximumNumberOfSimultaneousThreads;
	}
	
	private boolean canWaitForToken() {
		return this.backlog.size() < backlogMaximumNumberOfRequests;
	}
	
	@Override
	protected void requestFinished(Request request) {
		if(!backlog.isEmpty()){
			Request newRequestToAdd = backlog.poll();
			newRequestToAdd.assignTo(this.descriptor.getType());
			processorQueue.add(newRequestToAdd);
		}
		super.requestFinished(request);
	}
}
