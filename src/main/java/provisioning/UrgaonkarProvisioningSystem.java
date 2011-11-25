package provisioning;

import java.util.ArrayList;
import java.util.List;

import provisioning.util.DPSInfo;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.config.Configuration;
import commons.io.Checkpointer;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.util.SaaSAppProperties;
import commons.sim.util.SimulatorProperties;
import commons.util.TimeUnit;

public class UrgaonkarProvisioningSystem extends DynamicProvisioningSystem {
	
	private static final String PROP_ENABLE_PREDICTIVE = "dps.urgaonkar.predictive";
	private static final String PROP_ENABLE_REACTIVE = "dps.urgaonkar.reactive";
	private static final String PROP_MACHINE_TYPE = "dps.urgaonkar.type";
	private static final String PROP_REACTIVE_TRESHOLD = "dps.urgaonkar.reactive.threashold";
	
	private static final long predictiveTick = TimeUnit.HOUR.getMillis()/TimeUnit.SECOND.getMillis();
	private static final long predictiveTickInMillis = TimeUnit.HOUR.getMillis();
	private long reactiveTick;
	
	private boolean enablePredictive;
	private boolean enableReactive;
	private double threshold;
	private long maxRT;
	
	private UrgaonkarStatistics [] stat;
	private UrgaonkarHistory last;
	private MachineType type;

	public UrgaonkarProvisioningSystem() {
		super();
		enablePredictive = Configuration.getInstance().getBoolean(PROP_ENABLE_PREDICTIVE, true);
		enableReactive = Configuration.getInstance().getBoolean(PROP_ENABLE_REACTIVE, true);
		type = MachineType.valueOf(Configuration.getInstance().getString(PROP_MACHINE_TYPE).toUpperCase());
		threshold = Configuration.getInstance().getDouble(PROP_REACTIVE_TRESHOLD, 2.0);
		maxRT = Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)/TimeUnit.SECOND.getMillis();
		reactiveTick = Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)/TimeUnit.SECOND.getMillis();
		DPSInfo info = Checkpointer.loadProvisioningInfo();
		if(info.stat == null && info.history == null){
			info.stat = new UrgaonkarStatistics[24];
			for (int i = 0; i < info.stat.length; i++) {
				info.stat[i] = new UrgaonkarStatistics(maxRT, predictiveTick);
			}
			info.history = new UrgaonkarHistory();
		}
		stat = info.stat;
		last = info.history;
	}
	
	@Override
	public void sendStatistics(long now, MachineStatistics statistics, int tier) {

		boolean predictiveRound = now % predictiveTickInMillis == 0;
		
		int numberOfServersToAdd = 0;
		
		if(predictiveRound && enablePredictive){
			int index = (int)((now%TimeUnit.DAY.getMillis())/TimeUnit.HOUR.getMillis());
			
			UrgaonkarStatistics lastTick = stat[index];
			UrgaonkarStatistics nextTick = stat[(index+1)%stat.length];
			
			lastTick.add(statistics);
			
			last.update(lastTick.getCurrentArrivalRate());
			
			double lambdaPeak = lastTick.calcLambdaPeak();
			
			double lambdaPred = nextTick.getPredArrivalRate();
			
			double lambdaPredFromPercentile = lambdaPred;
			
			lambdaPred = last.applyError(lambdaPred);
			
			numberOfServersToAdd = (int) Math.ceil(lambdaPred/lambdaPeak) - statistics.totalNumberOfServers*type.getNumberOfCores();
			
			int antes = numberOfServersToAdd;
			if(numberOfServersToAdd > 0){
				
				numberOfServersToAdd = (int) Math.ceil(1.0*numberOfServersToAdd/type.getNumberOfCores());
				
				if(numberOfServersToAdd > statistics.warmingDownMachines){
					numberOfServersToAdd -= statistics.warmingDownMachines;
					List<MachineDescriptor> machines = buyMachines(numberOfServersToAdd);
					for (MachineDescriptor machineDescriptor : machines) {
						configurable.addMachine(tier, machineDescriptor, true);
					}
					
					configurable.cancelMachineRemoval(tier, statistics.warmingDownMachines);
				}else{
					configurable.cancelMachineRemoval(tier, numberOfServersToAdd);
				}
				
			}else if(numberOfServersToAdd < 0){
				numberOfServersToAdd = (int) Math.ceil(1.0*numberOfServersToAdd/type.getNumberOfCores());
				for (int i = 0; i < -numberOfServersToAdd; i++) {
					configurable.removeMachine(tier, false);
				}
			}
			log.info(String.format("STAT-URGAONKAR PRED %d %d %d %f %f %f %f %s", now, antes, numberOfServersToAdd, lambdaPeak, statistics.getArrivalRate(predictiveTick), lambdaPredFromPercentile, lambdaPred, statistics));
		}else if(!predictiveRound && enableReactive){
			
			long interval = (now % TimeUnit.HOUR.getMillis())/1000;

			int index = (int)((now%TimeUnit.DAY.getMillis())/TimeUnit.HOUR.getMillis());
			UrgaonkarStatistics currentStat = stat[index];
			double pred = currentStat.calcLambdaPeak();
			double observed = statistics.getArrivalRate(interval)/(statistics.totalNumberOfServers*type.getNumberOfCores());
			
			if (observed/pred > threshold){
				numberOfServersToAdd = (int) Math.ceil(currentStat.getPredArrivalRate()/pred) - (statistics.totalNumberOfServers*type.getNumberOfCores());
				
				
//				antes = numberOfServersToAdd;
//				if(numberOfServersToAdd > 0){
//					
//					numberOfServersToAdd = (int) Math.ceil(1.0*numberOfServersToAdd/type.getNumberOfCores());
//					
//					if(numberOfServersToAdd > statistics.warmingDownMachines){
//						numberOfServersToAdd -= statistics.warmingDownMachines;
//						List<MachineDescriptor> machines = buyMachines(numberOfServersToAdd);
//						for (MachineDescriptor machineDescriptor : machines) {
//							configurable.addMachine(tier, machineDescriptor, true);
//						}
//						
//						configurable.cancelMachineRemoval(tier, statistics.warmingDownMachines);
//					}else{
//						configurable.cancelMachineRemoval(tier, numberOfServersToAdd);
//					}
//					
//				}
				
			}
//			log.info(String.format("STAT-URGAONKAR READ %d %d %d %f %f %s", now, antes, numberOfServersToAdd, lambda_pred, statistics.getArrivalRate(predictiveTick), statistics));
			log.info(String.format("STAT-URGAONKAR REAC %d %d %d %s", now, tier, numberOfServersToAdd, statistics));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<MachineDescriptor> buyMachines(int numberOfMachines) {
		
		List<MachineDescriptor> currentlyBought = new ArrayList<MachineDescriptor>();
		
		for (Provider provider : providers) {
			while(currentlyBought.size() != numberOfMachines && provider.canBuyMachine(true, type)){
				currentlyBought.add(provider.buyMachine(true, type));
			}
		}
		for (Provider provider : providers) {
			while(currentlyBought.size() != numberOfMachines && provider.canBuyMachine(false, type)){
				currentlyBought.add(provider.buyMachine(false, type));
			}
		}
		
		return currentlyBought;
	}
	
	
	@Override
	public DPSInfo getDPSInfo() {
		DPSInfo info = new DPSInfo();
		info.history = last;
		info.stat = stat;
		return info;
	}
	


}
