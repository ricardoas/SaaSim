package commons.sim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.cloud.Machine;
import commons.cloud.Request;

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
			Machine nextServer = servers.get(this.roundRobinIndex);
			this.roundRobinIndex++;
			return nextServer;
		}
	}

	private Machine getServerOfPreviousRequestInSession(Request request) {
		Long lastRequestTime = this.lastRequestTimes.get(request.userID);
		if(request.time - lastRequestTime <= SESSION_LIMIT){
			return this.serversOfLastRequests.get(lastRequestTime);
		}
		return null;
	}

	@Override
	public int evaluateUtilization(Long eventTime) {
		//TODO Collect machines time usage in the last interval, calculate number of machines for next interval based on target utilization
		
		return 0;
	}

}
