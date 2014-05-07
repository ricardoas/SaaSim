package saasim.core.infrastructure;

import java.util.LinkedList;
import java.util.TreeMap;

import saasim.core.application.Request;

public class RoundRobinLoadBalancer implements LoadBalancer {
	
	private TreeMap<InstanceDescriptor, Machine> machines;
	private LinkedList<InstanceDescriptor> roundRobinQueue;
	
	private int roundRobinIndex;
	

	public RoundRobinLoadBalancer() {
		this.machines = new TreeMap<>();
		this.roundRobinIndex = -1;
	}
	

	@Override
	public void queue(Request request) {
		machines.get(getNextAvailableMachine()).queue(request);
	}
	
	private InstanceDescriptor getNextAvailableMachine() {
		assert !roundRobinQueue.isEmpty(): "There's no machine registered in this load balancer";
		
		roundRobinIndex = (++roundRobinIndex) % machines.size();
		return roundRobinQueue.get(roundRobinIndex);
	}


	@Override
	public void addMachine(InstanceDescriptor descriptor,
			boolean useStartUpDelay) {
		machines.put(descriptor, null);
		roundRobinQueue.add(descriptor);
	}

	@Override
	public void removeMachine(InstanceDescriptor descriptor, boolean force) {
		machines.remove(descriptor);
		
		int index = roundRobinQueue.indexOf(descriptor);
		if(index <= roundRobinIndex){
			roundRobinIndex--;
		}
		roundRobinQueue.remove(index);
	}

	@Override
	public void reconfigureMachine(InstanceDescriptor descriptor, boolean force) {
		Machine machine = machines.remove(descriptor);
		machine.reconfigure(descriptor);
		machines.put(descriptor, machine);
		
		roundRobinQueue.set(roundRobinQueue.indexOf(descriptor), descriptor);
	}

	@Override
	public void registerDrop(Request request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverIsUp(InstanceDescriptor descriptor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serverIsDown(InstanceDescriptor descriptor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestWasQueued(Request request) {
		// TODO Auto-generated method stub

	}

	@Override
	public void collectStatistics(long now, long timeInterval,
			int numberOfRequests, int peakArrivalRate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reportRequestQueued(Request requestQueued) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reportRequestFinished(Request requestFinished) {
		// TODO Auto-generated method stub

	}

	@Override
	public void config(double threshold) {
		// TODO Auto-generated method stub

	}

}
