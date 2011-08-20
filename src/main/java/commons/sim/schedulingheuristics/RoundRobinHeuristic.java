/**
 * 
 */
package commons.sim.schedulingheuristics;

import java.util.List;

import commons.cloud.Request;
import commons.sim.components.Machine;

/**
 * Simple {@link SchedulingHeuristic} to choose servers in a Round Robin fashion.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class RoundRobinHeuristic implements SchedulingHeuristic {
	
	private int lastUsed;
	
	/**
	 * Default constructor
	 */
	public RoundRobinHeuristic() {
		this.lastUsed = -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Machine getNextServer(Request request, List<Machine> servers) {
		
		lastUsed = (lastUsed + 1) % servers.size();
		return servers.get(lastUsed);
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

	@Override
	public void finishServer(Machine server, int index, List<Machine> servers){
		if(lastUsed == index){
			lastUsed = (lastUsed + 1) % servers.size();
		}else if(lastUsed > index){
			lastUsed = Math.max(lastUsed - 1, 0);
		}
	}
}
