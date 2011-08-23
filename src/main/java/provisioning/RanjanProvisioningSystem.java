package provisioning;

import commons.cloud.Request;
import commons.sim.provisioningheuristics.RanjanStatistics;

/**
 * This class represents the DPS business logic according to RANJAN. Here some statistics of current
 * available machines (i.e, utilisation) is used to verify if new machines need to be added to 
 * an application tier, or if some machines can be removed from any application tier.
 * @author davidcmm
 *
 */
public class RanjanProvisioningSystem extends DynamicProvisioningSystem {

	private double TARGET_UTILIZATION = 0.66;
	public static long UTILIZATION_EVALUATION_PERIOD_IN_MILLIS = 1000 * 60 * 5;//in millis

	public RanjanProvisioningSystem() {
		super();
	}
	
	@Override
	public void evaluateUtilisation(long now, RanjanStatistics statistics, int tier) {
		long numberOfServersToAdd = evaluateNumberOfServersForNextInterval(statistics);
		if(numberOfServersToAdd > 0){
			for(int i = 0; i < numberOfServersToAdd; i++){
				evaluateMachinesToBeAdded(tier);
			}
		}else if(numberOfServersToAdd < 0){
			for (int i = 0; i < -numberOfServersToAdd; i++) {
				configurable.removeServer(tier, false);
			}
		}
	}

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
		}
		if(statistics.numberOfRequestsArrivalInLastInterval > 0 && 
				statistics.totalNumberOfServers == 0){
			return 1l;
		}
		return numberOfServersToAdd;
	}
	
	private void evaluateMachinesToBeAdded(int tier) {
		if(accountingSystem.canBuyMachine()){
			configurable.addServer(tier, accountingSystem.buyMachine(), true);
		}
	}
	
	@Override
	public void requestQueued(long timeMilliSeconds, Request request, int tier) {
		accountingSystem.reportLostRequest(request);
	}
}
