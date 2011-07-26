package commons.sim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.cloud.Request;
import commons.sim.components.Machine;
import commons.sim.schedulingheuristics.SchedulingHeuristic;

public class RanjanScheduler implements SchedulingHeuristic {

	private long SESSION_LIMIT = 1000 * 60 * 15;//in millis
	private double TARGET_UTILIZATION = 0.66;
	
	private Map<String, Long> lastRequestTimes; 
	private Map<Long, Machine> serversOfLastRequests;
	private int roundRobinIndex;
	
	public RanjanScheduler(){
		this.lastRequestTimes = new HashMap<String, Long>();
		this.serversOfLastRequests = new HashMap<Long, Machine>();
		this.roundRobinIndex = 0;
	}
	
	@Override
	public Machine getNextServer(Request request, List<Machine> servers) {
		Machine machine = getServerOfPreviousRequestInSession(request);
		if(machine != null){//Allocates to server already serving the session
			return machine;
		}else{//Round-robin
			if(this.roundRobinIndex >= servers.size()){
				this.roundRobinIndex = 0;
			}
			if(servers.size() > 0){
				Machine nextServer = servers.get(this.roundRobinIndex);
				this.roundRobinIndex++;
				
				//Updating times
				Long lastRequestTime = this.lastRequestTimes.get(request.getUserID());
				this.lastRequestTimes.put(request.getUserID(), request.getTimeInMillis());
				this.serversOfLastRequests.remove(lastRequestTime);
				this.serversOfLastRequests.put(request.getTimeInMillis(), nextServer);
				
				return nextServer;
			}else{
				return null;
			}
		}
	}

	private Machine getServerOfPreviousRequestInSession(Request request) {
		Long lastRequestTime = this.lastRequestTimes.get(request.getUserID());
		if(lastRequestTime != null && request.getTimeInMillis() - lastRequestTime <= SESSION_LIMIT){
			this.lastRequestTimes.put(request.getUserID(), request.getTimeInMillis());
			Machine lastMachine = this.serversOfLastRequests.get(lastRequestTime);
			this.serversOfLastRequests.remove(lastRequestTime);
			this.serversOfLastRequests.put(request.getTimeInMillis(), lastMachine);
			return lastMachine;
		}
		return null;
	}

	@Deprecated
	public double evaluateUtilization(List<Machine> servers, Long eventTime){
		double averageUtilization = 0d;
		double totalNumberOfCompletions = 0d;
		double totalNumberOfArrivals = 0d;
		
		for(Machine machine : servers){
			averageUtilization += machine.computeUtilization(eventTime);
			totalNumberOfCompletions += machine.getNumberOfRequestsCompletionsInPreviousInterval();
			totalNumberOfArrivals += machine.getNumberOfRequestsArrivalsInPreviousInterval();
			machine.resetCounters();//Resetting completions and arrivals counter
		}
		
//		averageUtilization = averageUtilization / servers.size();
		double d;
		if(totalNumberOfCompletions == 0){
			d = averageUtilization;
		}else{
			d = averageUtilization / totalNumberOfCompletions;
		}
		
		double u_lign = Math.max(totalNumberOfArrivals, totalNumberOfCompletions) * d;
		int newNumberOfServers = (int)Math.ceil( servers.size() * u_lign / TARGET_UTILIZATION );
		
		return (newNumberOfServers - servers.size());
	}

}
