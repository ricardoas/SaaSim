package saasim.provisioning;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.Request;
import saasim.config.Configuration;
import saasim.provisioning.util.DPSInfo;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.provisioningheuristics.MachineStatistics;
import saasim.sim.util.SimulatorProperties;
import saasim.util.TimeUnit;


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
	
	private LinkedList<LinkedList<MachineDescriptor>> list;


	/**
	 * Default constructor 
	 */
	public UrgaonkarProvisioningSystem() {
		super();
		enablePredictive = Configuration.getInstance().getBoolean(PROP_ENABLE_PREDICTIVE, true);
		enableReactive = Configuration.getInstance().getBoolean(PROP_ENABLE_REACTIVE, true);
		type = MachineType.valueOf(Configuration.getInstance().getString(PROP_MACHINE_TYPE).toUpperCase());
		threshold = Configuration.getInstance().getDouble(PROP_REACTIVE_THRESHOLD, 1.0);
		responseTime = Configuration.getInstance().getLong(PROP_RESPONSE_TIME, 1000)/TimeUnit.SECOND.getMillis();
		reactiveTickInSeconds = Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)/TimeUnit.SECOND.getMillis();
		windowSize = Configuration.getInstance().getInt(PROP_PREDICTION_WINDOW_SIZE, DEFAULT_PREDICTION_WINDOW_SIZE);
		percentile = Configuration.getInstance().getDouble(PROP_PERCENTILE, DEFAULT_PERCENTILE);
		
		DPSInfo info = loadDPSInfo();
		
		list = info.list;
		stat = info.stat;
		last = info.history;
		lost = 0;
		after = 0;
	}

	/**
	 * @return {@link DPSInfo}
	 */
	protected DPSInfo loadDPSInfo() {
		DPSInfo info = super.loadDPSInfo();
		if(info.stat == null && info.history == null && info.list == null){
			info.stat = new UrgaonkarStatistics[24];
			for (int i = 0; i < info.stat.length; i++) {
				info.stat[i] = new UrgaonkarStatistics(responseTime, predictiveTick, percentile, windowSize);
			}
			info.history = new UrgaonkarHistory();
			info.list = new LinkedList<LinkedList<MachineDescriptor>>();
			for (int i = 0; i < TimeUnit.HOUR.getMillis()/(reactiveTickInSeconds*1000); i++) {
				info.list.add(new LinkedList<MachineDescriptor>());
			}
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
		
		LinkedList<MachineDescriptor> availableToTurnOff = list.poll();
		
		
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
				
				List<MachineDescriptor> machines = buyMachines(normalizedServersToAdd);
				for (MachineDescriptor machineDescriptor : machines) {
					configurable.addMachine(tier, machineDescriptor, true);
				}
					
				availableToTurnOff.addAll(machines);
				
			}else if(serversToAdd < 0){
				
				normalizedServersToAdd = (int) Math.ceil(1.0*serversToAdd/type.getNumberOfCores());
				
				if(-normalizedServersToAdd >= statistics.totalNumberOfServers){
					normalizedServersToAdd = 1-statistics.totalNumberOfServers;
				}
				
				normalizedServersToAdd = Math.min(-normalizedServersToAdd, availableToTurnOff.size());
				
				for (int i = 0; i < normalizedServersToAdd; i++) {
					configurable.removeMachine(tier,  availableToTurnOff.poll(), false);
				}
			}
			
			int sentryLimit = (int)Math.ceil(lambdaPeak * (statistics.totalNumberOfServers + normalizedServersToAdd));
			
			configurable.config(sentryLimit);
			
			log.info(String.format("STAT-URGAONKAR PRED %d %d %d %f %f %f %f %d %d %d %s", now, serversToAdd, normalizedServersToAdd, lambdaPeak, statistics.getArrivalRateInTier(predictiveTick), predictedArrivalRate, correctedPredictedArrivalRate, lost, after, sentryLimit, statistics));
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
					
					List<MachineDescriptor> machines = buyMachines(normalizedServersToAdd);
					for (MachineDescriptor machineDescriptor : machines) {
						configurable.addMachine(tier, machineDescriptor, true);
					}
					availableToTurnOff.addAll(machines);
				}
				
			}else if( observed/(lambdaPeak * statistics.totalNumberOfServers) < threshold ){
				
				serversToAdd = (int) Math.ceil(observed/lambdaPeak) - statistics.totalNumberOfServers*type.getNumberOfCores();
				
				if(serversToAdd < 0){
					normalizedServersToAdd = (int) Math.ceil(1.0*serversToAdd/type.getNumberOfCores());

					if(-normalizedServersToAdd >= statistics.totalNumberOfServers){
						normalizedServersToAdd = 1-statistics.totalNumberOfServers;
					}
					
					normalizedServersToAdd = Math.min(-normalizedServersToAdd, availableToTurnOff.size());

					for (int i = 0; i < normalizedServersToAdd; i++) {
						configurable.removeMachine(tier,  availableToTurnOff.poll(), false);
					}
				}

			}

			int sentryLimit = (int)Math.ceil(lambdaPeak * (statistics.totalNumberOfServers + normalizedServersToAdd));
			
//			configurable.config(sentryLimit);
			
			log.debug(String.format("STAT-URGAONKAR REAC %d %d %d %f %f %f %f %d %d %d %s", now, serversToAdd, normalizedServersToAdd, lambdaPeak, statistics.getArrivalRateInLastIntervalInTier(reactiveTickInSeconds), correctedPredictedArrivalRate, correctedPredictedArrivalRate, lost, after, sentryLimit, statistics));
		}
		list.add(availableToTurnOff);
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
		
		return 1.0/(statistics.averageST + (statistics.calcVarST() + statistics.calcVarIAT())/(2 * (1.0*responseTime - statistics.averageST)));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reportLostRequest(Request request) {
		super.reportLostRequest(request);
		lost++;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reportFinishedRequestAfterSLA(Request request) {
		super.reportFinishedRequestAfterSLA(request);
		after++;
	}
}
