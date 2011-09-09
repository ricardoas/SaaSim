package commons.sim.schedulingheuristics;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import commons.cloud.Request;
import commons.sim.components.Machine;

/**
 * Simple {@link SchedulingHeuristic} to choose servers in a Round Robin fashion.
 * 
 * This heuristic is quite similar to a Round-Robin one. The difference consists 
 * in the fact that it tries to arrange requests of a same session in the same
 * servers.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class RanjanHeuristic implements SchedulingHeuristic {
	
	private final long SESSION_LIMIT_IN_MILLIS = 1000 * 60 * 15;
	
	private Map<Long, Long> lastRequestTimes;
	private Map<Long, Machine> serversOfLastRequests;
	
	private int lastUsed;
	private long requestsArrivalCounter;
	private long finishedRequestsCounter;

	
	/**
	 * Default constructor
	 */
	public RanjanHeuristic() {
		this.lastUsed = -1;
		this.lastRequestTimes = new HashMap<Long, Long>();
		this.serversOfLastRequests = new LinkedHashMap<Long, Machine>();
		
		this.requestsArrivalCounter = 0;
		this.finishedRequestsCounter = 0;
	}

	/**
	 * This method gathers the utilisation of each machine being used in order to forward such
	 * information to provisioning heuristics.
	 */
	@Deprecated
	public double evaluateUtilization(List<Machine> servers, Long eventTime) {
		double totalUtilization = 0d;
		
		for(Machine machine : servers){
			totalUtilization += machine.computeUtilisation(eventTime);
		}
		
		return totalUtilization;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Machine getNextServer(Request request, List<Machine> servers) {
		this.requestsArrivalCounter++;
		
		Machine machine = getServerOfPreviousRequestInSession(request);
		if(machine != null){//Allocates to server already serving the session
			return machine;
		}
		lastUsed = (lastUsed + 1) % servers.size();
		machine = servers.get(lastUsed);
		
		//Updating times
		long userID = request.getUserID();
		long timeInMillis = request.getArrivalTimeInMillis();
		
		Long lastRequestTime = this.lastRequestTimes.get(userID);
		this.lastRequestTimes.put(userID, timeInMillis);
		this.serversOfLastRequests.remove(lastRequestTime);
		this.serversOfLastRequests.put(timeInMillis, machine);
		
		return machine;
		
	}
	
	private Machine getServerOfPreviousRequestInSession(Request request) {
		long timeInMillis = request.getArrivalTimeInMillis();
		long userID = request.getUserID();

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

	@Override
	public long getRequestsArrivalCounter() {
		return this.requestsArrivalCounter;
	}

	@Override
	public long getFinishedRequestsCounter() {
		return this.finishedRequestsCounter;
	}

	@Override
	public void resetCounters() {
		this.requestsArrivalCounter = 0;
		this.finishedRequestsCounter = 0;
	}

	@Override
	public void reportRequestFinished() {
		this.finishedRequestsCounter++;
	}

	@Override
	public void finishServer(Machine server, int index, List<Machine> servers) {
	}
}
