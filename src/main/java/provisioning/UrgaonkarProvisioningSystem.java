package provisioning;

import java.util.ArrayList;
import java.util.List;

import provisioning.util.DPSInfo;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.config.Configuration;
import commons.io.Checkpointer;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.util.SimulatorProperties;
import commons.util.TimeUnit;

/**
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public class UrgaonkarProvisioningSystem extends DynamicProvisioningSystem {
	
	private static final String PROP_PREDICTION_WINDOW_SIZE= "dps.urgaonkar.windowsize";
	private static final String PROP_PERCENTILE = "dps.urgaonkar.percentile";
	private static final String PROP_ENABLE_PREDICTIVE = "dps.urgaonkar.predictive";
	private static final String PROP_ENABLE_REACTIVE = "dps.urgaonkar.reactive";
	private static final String PROP_MACHINE_TYPE = "dps.urgaonkar.type";
	private static final String PROP_REACTIVE_THRESHOLD = "dps.urgaonkar.reactive.threshold";
	private static final String PROP_RESPONSE_TIME = "dps.urgaonkar.responsetime";
	
	private static int DEFAULT_PREDICTION_WINDOW_SIZE = 5;
	private static final double DEFAULT_PERCENTILE = 95.0;
	private static final long predictiveTick = TimeUnit.HOUR.getMillis()/TimeUnit.SECOND.getMillis();
	private static final long predictiveTickInMillis = TimeUnit.HOUR.getMillis();
	private long reactiveTickInSeconds;
	
	private boolean enablePredictive;
	private boolean enableReactive;
	private double threshold;
	private long responseTime;
	
	private UrgaonkarStatistics [] stat;
	private UrgaonkarHistory last;
	private MachineType type;
	private int windowSize;
	private double percentile;
	private int lost;
	private int after;
	private double lambdaPeak;
	private double correctedPredictedArrivalRate;

	/**
	 * Default constructor 
	 */
	public UrgaonkarProvisioningSystem() {
		super();
		enablePredictive = Configuration.getInstance().getBoolean(PROP_ENABLE_PREDICTIVE, true);
		enableReactive = Configuration.getInstance().getBoolean(PROP_ENABLE_REACTIVE, true);
		type = MachineType.valueOf(Configuration.getInstance().getString(PROP_MACHINE_TYPE).toUpperCase());
		threshold = Configuration.getInstance().getDouble(PROP_REACTIVE_THRESHOLD, 2.0);
		responseTime = Configuration.getInstance().getLong(PROP_RESPONSE_TIME, 1000)/TimeUnit.SECOND.getMillis();
		reactiveTickInSeconds = Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)/TimeUnit.SECOND.getMillis();
		windowSize = Configuration.getInstance().getInt(PROP_PREDICTION_WINDOW_SIZE, DEFAULT_PREDICTION_WINDOW_SIZE);
		percentile = Configuration.getInstance().getDouble(PROP_PERCENTILE, DEFAULT_PERCENTILE);
		
		DPSInfo info = loadDPSInfo();
		
		stat = info.stat;
		last = info.history;
		lost = 0;
		after = 0;
	}

	/**
	 * @return {@link DPSInfo}
	 */
	private DPSInfo loadDPSInfo() {
		DPSInfo info = Checkpointer.loadProvisioningInfo();
		if(info.stat == null && info.history == null){
			info.stat = new UrgaonkarStatistics[24];
			for (int i = 0; i < info.stat.length; i++) {
				info.stat[i] = new UrgaonkarStatistics(responseTime, predictiveTick, percentile, windowSize);
			}
			info.history = new UrgaonkarHistory();
		}
		return info;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendStatistics(long now, MachineStatistics statistics, int tier) {

		boolean predictiveRound = now % predictiveTickInMillis == 0;
		
		int normalizedServersToAdd = 0;
		
		if(predictiveRound && enablePredictive){
			int index = (int)((now%TimeUnit.DAY.getMillis())/TimeUnit.HOUR.getMillis());
			
			UrgaonkarStatistics lastTick = stat[index];
			UrgaonkarStatistics nextTick = stat[(index+1)%stat.length];
			
			lastTick.add(statistics);
			
			last.update(lastTick.getCurrentArrivalRate());
			
			lambdaPeak = getPeakArrivalRatePerServer(statistics);
			
			double predictedArrivalRate = nextTick.getPredArrivalRate();
			
			correctedPredictedArrivalRate = last.applyError(predictedArrivalRate);
			
			int serversToAdd = (int) Math.ceil(correctedPredictedArrivalRate/lambdaPeak) - statistics.totalNumberOfServers*type.getNumberOfCores();
			
			if(serversToAdd > 0){
				
				normalizedServersToAdd = (int) Math.ceil(1.0*serversToAdd/type.getNumberOfCores());
				
				if(normalizedServersToAdd > statistics.warmingDownMachines){
					normalizedServersToAdd -= statistics.warmingDownMachines;
					List<MachineDescriptor> machines = buyMachines(normalizedServersToAdd);
					for (MachineDescriptor machineDescriptor : machines) {
						configurable.addMachine(tier, machineDescriptor, true);
					}
					
					configurable.cancelMachineRemoval(tier, statistics.warmingDownMachines);
				}else{
					configurable.cancelMachineRemoval(tier, normalizedServersToAdd);
				}
				
			}else if(serversToAdd < 0){
				normalizedServersToAdd = (int) Math.ceil(1.0*serversToAdd/type.getNumberOfCores());
				for (int i = 0; i < -normalizedServersToAdd; i++) {
					configurable.removeMachine(tier, false);
				}
			}
			log.info(String.format("STAT-URGAONKAR PRED %d %d %d %f %f %f %f %d %d %s", now, serversToAdd, normalizedServersToAdd, lambdaPeak, statistics.getArrivalRateInTier(predictiveTick), predictedArrivalRate, correctedPredictedArrivalRate, lost, after, statistics));
			lost = 0;
			after = 0;
		}else if(!predictiveRound && enableReactive){
			
			double observed = statistics.getArrivalRateInLastIntervalInTier(reactiveTickInSeconds);
			
			int serversToAdd = 0;
			if (observed/(lambdaPeak * statistics.totalNumberOfServers) > threshold){
				
				serversToAdd = (int) Math.ceil(observed/lambdaPeak) - statistics.totalNumberOfServers*type.getNumberOfCores();
				
				serversToAdd = Math.max(1, serversToAdd);
				
				if(serversToAdd > 0){
					
					normalizedServersToAdd = (int) Math.ceil(1.0*serversToAdd/type.getNumberOfCores());
					
					if(normalizedServersToAdd > statistics.warmingDownMachines){
						normalizedServersToAdd -= statistics.warmingDownMachines;
						List<MachineDescriptor> machines = buyMachines(normalizedServersToAdd);
						for (MachineDescriptor machineDescriptor : machines) {
							configurable.addMachine(tier, machineDescriptor, true);
						}
						
						configurable.cancelMachineRemoval(tier, statistics.warmingDownMachines);
					}else{
						configurable.cancelMachineRemoval(tier, normalizedServersToAdd);
					}
					
				}else if(serversToAdd < 0){
//					normalizedServersToAdd = (int) Math.ceil(1.0*serversToAdd/type.getNumberOfCores());
//					for (int i = 0; i < -normalizedServersToAdd; i++) {
//						configurable.removeMachine(tier, false);
//					}
				}
				
			}
			log.debug(String.format("STAT-URGAONKAR REAC %d %d %d %f %f %f %f %d %d %s", now, serversToAdd, normalizedServersToAdd, lambdaPeak, statistics.getArrivalRateInLastIntervalInTier(reactiveTickInSeconds), correctedPredictedArrivalRate, correctedPredictedArrivalRate, lost, after, statistics));
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
	
	/**
	 * Calculate peak arrival rate a single server can handle.
	 * @param statistics {@link MachineStatistics}
	 */
	private double getPeakArrivalRatePerServer(MachineStatistics statistics) {
		
		double lambdaPeak = (statistics.averageST + (statistics.calcVarST() + statistics.calcVarIAT())/(2 * (1.0*responseTime - statistics.averageST)));
		
		return 1.0/lambdaPeak;
	}
	
	@Override
	protected void reportLostRequest(Request request) {
		super.reportLostRequest(request);
		lost++;
	}
	
	@Override
	protected void reportFinishedRequestAfterSLA(Request request) {
		super.reportFinishedRequestAfterSLA(request);
		after++;
	}
}
