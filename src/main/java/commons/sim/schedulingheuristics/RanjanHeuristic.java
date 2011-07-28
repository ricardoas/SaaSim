/**
 * This heuristic is quite similar to a Round-Robin one. The difference consists 
 * in the fact that it tries to arrange requests of a same session in the same
 * servers.
 */
package commons.sim.schedulingheuristics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.cloud.Request;
import commons.sim.components.Machine;

/**
 * Simple {@link SchedulingHeuristic} to choose servers in a Round Robin fashion.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class RanjanHeuristic implements SchedulingHeuristic {
	
	private final long SESSION_LIMIT_IN_MILLIS = 1000 * 60 * 15;
	private Map<String, Long> lastRequestTimes; 
	private Map<Long, Machine> serversOfLastRequests;
	
	private int lastUsed;
	
	/**
	 * Default constructor
	 */
	public RanjanHeuristic() {
		this.lastUsed = -1;
		this.lastRequestTimes = new HashMap<String, Long>();
		this.serversOfLastRequests = new HashMap<Long, Machine>();
	}

	/**
	 * This method gathers the utilisation of each machine being used in order to forward such
	 * information to provisioning heuristics.
	 */
	@Deprecated
	public double evaluateUtilization(List<Machine> servers, Long eventTime) {
		double totalUtilization = 0d;
		
		for(Machine machine : servers){
			totalUtilization += machine.computeUtilization(eventTime);
		}
		
		return totalUtilization;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Machine getNextServer(Request request, List<Machine> servers) {
		Machine machine = getServerOfPreviousRequestInSession(request);
		if(machine != null){//Allocates to server already serving the session
			return machine;
		}else{
			lastUsed = (lastUsed + 1) % servers.size();
			machine = servers.get(lastUsed);
			
			//Updating times
			String userID = request.getUserID();
			long timeInMillis = request.getTimeInMillis();
			
			Long lastRequestTime = this.lastRequestTimes.get(userID);
			this.lastRequestTimes.put(userID, timeInMillis);
			this.serversOfLastRequests.remove(lastRequestTime);
			this.serversOfLastRequests.put(timeInMillis, machine);
			
			return machine;
		}
		
	}
	
	private Machine getServerOfPreviousRequestInSession(Request request) {
		long timeInMillis = request.getTimeInMillis();
		String userID = request.getUserID();

		Long lastRequestTime = this.lastRequestTimes.get(userID);
		if(lastRequestTime != null && timeInMillis - lastRequestTime <= SESSION_LIMIT_IN_MILLIS){
			this.lastRequestTimes.put(userID, timeInMillis);
			Machine lastMachine = this.serversOfLastRequests.get(lastRequestTime);
			this.serversOfLastRequests.remove(lastRequestTime);
			this.serversOfLastRequests.put(timeInMillis, lastMachine);
			return lastMachine;
		}
		return null;
	}
}