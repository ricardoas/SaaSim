package commons.sim.components;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import commons.cloud.Request;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;

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
	private boolean shutdownOnFinish;
	
	protected double totalProcessed;
	protected JEEvent nextFinishEvent;
	
	protected List<Request> finishedRequests;
	
	public int numberOfRequestsCompletionsInPreviousInterval;
	public int numberOfRequestsArrivalsInPreviousInterval;
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
			scheduleNext();
		}
	}

	/**
	 * 
	 */
	public void shutdownOnFinish() {
		this.shutdownOnFinish = true;
		tryToShutdown();
	}

	/**
	 * 
	 */
	private void tryToShutdown() {
		if(processorQueue.isEmpty() && shutdownOnFinish){
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
			Request request = processorQueue.poll();
			request.update((Long) event.getValue()[0]);
			if(request.isFinished()){
				getLoadBalancer().reportRequestFinished(request);
			}else{
				processorQueue.add(request);
			}
			
			if(!processorQueue.isEmpty()){
				scheduleNext();
			}
			
			tryToShutdown();
			
			break;
		}
	}

	/**
	 * Schedule next requesto on the processor queue.
	 */
	private void scheduleNext() {
		Request nextRequest = processorQueue.peek();
		long nextQuantum = Math.min(nextRequest.getTotalToProcess(), cpuQuantumInMilis);
		send(new JEEvent(JEEventType.PREEMPTION, this, new JETime(nextQuantum).plus(getScheduler().now()), nextQuantum));
	}
	
	public boolean isBusy() {
		return this.processorQueue.size() != 0 && this.nextFinishEvent != null;
	}	

	public boolean isReserved(){
		return this.descriptor.isReserved();
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
