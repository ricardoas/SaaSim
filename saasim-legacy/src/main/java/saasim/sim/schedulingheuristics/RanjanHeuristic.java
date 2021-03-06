package saasim.sim.schedulingheuristics;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import saasim.cloud.Request;
import saasim.sim.components.Machine;


/**
 * Simple {@link SchedulingHeuristic} to choose servers in a Round Robin fashion.
 * 
 * This heuristic is quite similar to a Round-Robin one. The difference consists 
 * in the fact that it tries to arrange requests of a same session in the same
 * servers.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class RanjanHeuristic extends AbstractSchedulingHeuristic {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8376594311045424046L;

	private final long SESSION_LIMIT_IN_MILLIS = 1000 * 60 * 15;
	
	private Map<Long, Long> lastRequestTimes;
	private Map<Long, Machine> serversOfLastRequests;
	
	private int nextToUse;

	
	/**
	 * Default constructor
	 */
	public RanjanHeuristic() {
		super();
		this.nextToUse = 0;
		this.lastRequestTimes = new HashMap<Long, Long>();
		this.serversOfLastRequests = new LinkedHashMap<Long, Machine>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Machine next(Request request) {
		Machine machine = super.next(request);
		
		/*Machine machine = getServerOfPreviousRequestInSession(request);
		if(machine != null){//Allocates to server already serving the session
			return machine;
		}
		lastUsed = (lastUsed + 1) % machines.size();
		machine = machines.get(lastUsed);*/
		
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Machine getNextAvailableMachine() {
		int index = nextToUse++;
		return machines.isEmpty()? null: machines.get(index % machines.size());
	}
}
