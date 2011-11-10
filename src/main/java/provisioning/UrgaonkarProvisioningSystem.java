package provisioning;

import commons.config.Configuration;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.util.SaaSAppProperties;
import commons.util.TimeUnit;

public class UrgaonkarProvisioningSystem extends DynamicProvisioningSystem {
	
	private static final String PROP_ENABLE_PREDICTIVE = "dps.urgaonkar.predictive";
	private static final String PROP_ENABLE_REACTIVE = "dps.urgaonkar.reactive";
	
	private boolean enablePredictive;
	private boolean enableReactive;
	private long averageRT;
	
	private double [] lambda_pred = new double[24 * 7];

	public UrgaonkarProvisioningSystem() {
		super();
		enablePredictive = Configuration.getInstance().getBoolean(PROP_ENABLE_PREDICTIVE, true);
		enableReactive = Configuration.getInstance().getBoolean(PROP_ENABLE_REACTIVE, true);
		averageRT = Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME);
	}
	
	@Override
	public void sendStatistics(long now, MachineStatistics statistics, int tier) {
		boolean predictiveRound = now % TimeUnit.HOUR.getMillis() == 0;
		
		if(predictiveRound){
			double lambda_pred = 1/(statistics.averageST + (statistics.varRT + statistics.varIAT)/(2 * (averageRT - statistics.averageST)));
			int n = (int) Math.ceil(statistics.arrivalRate/lambda_pred);
			
		}else{
			
		}
		
		log.info(String.format("STAT-URGAONKAR %s %d %d %s", now, tier, statistics));
	}
	
	

}
