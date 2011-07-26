package commons.sim.provisioningheuristics;

public class RanjanProvHeuristic implements ProvisioningHeuristic {

	private double TARGET_UTILIZATION = 0.66;
	
	public long evaluateNumberOfServersForNextInterval(RanjanStatistics statistics) {
		double averageUtilization = statistics.totalUtilizationInLastInterval / statistics.totalNumberOfServers;
		double d;
		if(statistics.numberOfRequestsCompletionsInLastInterval == 0){
			d = averageUtilization;
		}else{
			d = averageUtilization / statistics.numberOfRequestsCompletionsInLastInterval;
		}
		
		double u_lign = Math.max(statistics.numberOfRequestsArrivalInLastInterval, statistics.numberOfRequestsCompletionsInLastInterval) * d;
		long newNumberOfServers = (int)Math.ceil( statistics.totalNumberOfServers * u_lign / TARGET_UTILIZATION );
		
		return (newNumberOfServers - statistics.totalNumberOfServers);
	}
}
