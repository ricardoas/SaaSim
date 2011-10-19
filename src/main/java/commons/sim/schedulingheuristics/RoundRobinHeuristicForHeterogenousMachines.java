/**
 * 
 */
package commons.sim.schedulingheuristics;

import java.util.ArrayList;
import java.util.List;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.components.Machine;

/**
 * Simple {@link SchedulingHeuristic} to choose servers in a Round Robin fashion.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class RoundRobinHeuristicForHeterogenousMachines implements SchedulingHeuristic {
	
	private int nextToUse;
	private List<Integer> allocationsPerServer;
	
	/**
	 * Default constructor
	 */
	public RoundRobinHeuristicForHeterogenousMachines() {
		this.nextToUse = 0;
		this.allocationsPerServer = new ArrayList<Integer>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Machine getNextServer(Request request, List<Machine> servers) {
		initList(servers);
		
		Integer alreadyAllocated = this.allocationsPerServer.get(nextToUse);
		//Retrieving server
		Machine server = servers.get(nextToUse);
		
		//Incrementing allocations for current server
		alreadyAllocated++;
		this.allocationsPerServer.set(nextToUse, alreadyAllocated);
		
		if(alreadyAllocated >= servers.get(nextToUse).getDescriptor().getType().getNumberOfCores()){//Limit reached for current machine type, next server will be used
			this.allocationsPerServer.set(nextToUse, 0);
			nextToUse = (nextToUse + 1) % servers.size();
		}
		
		return server;
	}

	/**
	 * Fixing allocations list if new servers have been added
	 * @param servers
	 */
	private void initList(List<Machine> servers) {
		int difference = servers.size() - this.allocationsPerServer.size();
		if(difference > 0){
			for(int i = this.allocationsPerServer.size(); i < servers.size(); i++){
				this.allocationsPerServer.add(i, 0);
			}
		}
	}

	@Override
	public long getRequestsArrivalCounter() {
		return 0;
	}

	@Override
	public long getFinishedRequestsCounter() {
		return 0;
	}

	@Override
	public void resetCounters() {
	}

	@Override
	public void reportRequestFinished() {
	}

	/**
	 * As a server is finished, allocation index may be fixed!
	 */
	@Override
	public void finishServer(Machine server, int index, List<Machine> servers) {
		if(nextToUse > index){
			nextToUse = nextToUse - 1;
		}
		this.allocationsPerServer.remove(index);
	}
}
