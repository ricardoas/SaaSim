package commons.sim.provisioningheuristics;

/**
 * This class represents the RANJAN heuristic that is responsible for deciding the amount of machines
 * to be purchased or finished at the cloud for a certain time interval 
 * @author davidcmm
 *
 */
public class RanjanProvHeuristic implements ProvisioningHeuristic {

	private double TARGET_UTILIZATION = 0.66;
	public static long UTILIZATION_EVALUATION_PERIOD_IN_MILLIS = 1000 * 60 * 5;//in millis
	
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
		
		long numberOfServersToAdd = (newNumberOfServers - statistics.totalNumberOfServers);
		if(numberOfServersToAdd != 0){
			return numberOfServersToAdd;
		}else{
			if(statistics.numberOfRequestsArrivalInLastInterval > 0 && 
					statistics.totalNumberOfServers == 0){
				return 1l;
			}
			return numberOfServersToAdd;
		}
	}
}
