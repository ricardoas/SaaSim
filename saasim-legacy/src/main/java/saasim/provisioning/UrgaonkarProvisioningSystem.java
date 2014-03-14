package saasim.provisioning;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.Request;
import saasim.cloud.User;
import saasim.config.Configuration;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.schedulingheuristics.Statistics;
import saasim.sim.util.SimulatorProperties;
import saasim.util.TimeUnit;


/**
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public class UrgaonkarProvisioningSystem extends DynamicProvisioningSystem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8961363221315245638L;
	private static final String PROP_PREDICTION_WINDOW_SIZE= "dps.urgaonkar.windowsize";
	private static final String PROP_PERCENTILE = "dps.urgaonkar.percentile";
	private static final String PROP_ENABLE_PREDICTIVE = "dps.urgaonkar.predictive";
	private static final String PROP_ENABLE_REACTIVE = "dps.urgaonkar.reactive";
	private static final String PROP_MACHINE_TYPE = "dps.urgaonkar.type";
	private static final String PROP_REACTIVE_THRESHOLD = "dps.urgaonkar.reactive.threshold";
	private static final String PROP_RESPONSE_TIME = "dps.urgaonkar.responsetime";
	private static final String PROP_FORCE_SHUTDOWN = "dps.urgaonkar.forceshutdown";
	
	private static int DEFAULT_PREDICTION_WINDOW_SIZE = 5;
	private static final double DEFAULT_PERCENTILE = 95.0;
	private static final long predictiveTick = TimeUnit.HOUR.getMillis()/TimeUnit.SECOND.getMillis();
	private static final long predictiveTickInMillis = TimeUnit.HOUR.getMillis();
	protected long reactiveTickInSeconds;
	
	private boolean enablePredictive;
	private boolean enableReactive;
	private double threshold;
	private double responseTime;
	
	private UrgaonkarStatistics [] stat;
	private UrgaonkarHistory last;
	protected MachineType type;
	private int windowSize;
	private double percentile;
	private int lost;
	private int after;
	private double lambdaPeak;
	private double correctedPredictedArrivalRate;
	
	private LinkedList<LinkedList<MachineDescriptor>> list;
	protected boolean forceShutdown;


	/**
	 * Default constructor 
	 * @param users TODO
	 * @param providers TODO
	 * @throws ConfigurationException 
	 */
	public UrgaonkarProvisioningSystem(User[] users, Provider[] providers) {
		super(users, providers);
		enablePredictive = Configuration.getInstance().getBoolean(PROP_ENABLE_PREDICTIVE, true);
		enableReactive = Configuration.getInstance().getBoolean(PROP_ENABLE_REACTIVE, true);
		type = MachineType.valueOf(Configuration.getInstance().getString(PROP_MACHINE_TYPE).toUpperCase());
		threshold = Configuration.getInstance().getDouble(PROP_REACTIVE_THRESHOLD, 1.0);
		responseTime = Configuration.getInstance().getDouble(PROP_RESPONSE_TIME, 1000.0)/TimeUnit.SECOND.getMillis();
		reactiveTickInSeconds = Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)/TimeUnit.SECOND.getMillis();
		windowSize = Configuration.getInstance().getInt(PROP_PREDICTION_WINDOW_SIZE, DEFAULT_PREDICTION_WINDOW_SIZE);
		percentile = Configuration.getInstance().getDouble(PROP_PERCENTILE, DEFAULT_PERCENTILE);
		forceShutdown = Configuration.getInstance().getBoolean(PROP_FORCE_SHUTDOWN, false);
		
		stat = new UrgaonkarStatistics[24];
		for (int i = 0; i < stat.length; i++) {
			stat[i] = new UrgaonkarStatistics(responseTime, predictiveTick, percentile, windowSize);
		}
		last = new UrgaonkarHistory();
		list = buildMachineList();

		lost = 0;
		after = 0;
	}

	protected LinkedList<LinkedList<MachineDescriptor>> buildMachineList() {
		LinkedList<LinkedList<MachineDescriptor>> machineList = new LinkedList<LinkedList<MachineDescriptor>>();
		machineList.add(new LinkedList<MachineDescriptor>());
		return machineList;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendStatistics(long now, Statistics statistics, int tier) {

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
			
			int serversToAdd = (int) Math.ceil(correctedPredictedArrivalRate/lambdaPeak) - (statistics.totalNumberOfActiveServers+statistics.startingUpServers)*type.getNumberOfCores();
			
			
			if(serversToAdd > 0){
				
				normalizedServersToAdd = (int) Math.ceil(1.0*serversToAdd/type.getNumberOfCores());
				
				List<MachineDescriptor> machines = buyMachines(normalizedServersToAdd);
				for (MachineDescriptor machineDescriptor : machines) {
					configurable.addMachine(tier, machineDescriptor, true);
				}
					
				availableToTurnOff.addAll(machines);
				
			}else if(serversToAdd < 0){
				
				normalizedServersToAdd = (int) Math.ceil(1.0*serversToAdd/type.getNumberOfCores());

				if(-normalizedServersToAdd >= statistics.totalNumberOfActiveServers){
					normalizedServersToAdd = 1-statistics.totalNumberOfActiveServers;
				}
				
				normalizedServersToAdd = -Math.min(-normalizedServersToAdd, availableToTurnOff.size());

				for (int i = 0; i < -normalizedServersToAdd; i++) {
					configurable.removeMachine(tier,  availableToTurnOff.poll(), forceShutdown);
				}
			}
			
			configurable.config(0, lambdaPeak);
			
			Logger.getLogger(getClass()).info(String.format("STAT-URGAONKAR PRED %d %d %d %f %f %f %f %d %d %f %s", now, serversToAdd, normalizedServersToAdd, lambdaPeak, statistics.getArrivalRateInTier(predictiveTick), predictedArrivalRate, correctedPredictedArrivalRate, lost, after, lambdaPeak*statistics.totalNumberOfActiveServers, statistics));
			lost = 0;
			after = 0;
		}else if(!predictiveRound && enableReactive){
			
			double observed = statistics.getArrivalRateInLastIntervalInTier(reactiveTickInSeconds);
			
			int serversToAdd = 0;
			if (observed/(lambdaPeak * statistics.totalNumberOfActiveServers) > threshold){
				
				serversToAdd = (int) Math.ceil(observed/lambdaPeak) - (statistics.totalNumberOfActiveServers+statistics.startingUpServers)*type.getNumberOfCores();
				
				serversToAdd = Math.max(1, serversToAdd);
				
				if(serversToAdd > 0){
					normalizedServersToAdd = (int) Math.ceil(1.0*serversToAdd/type.getNumberOfCores());
					
					List<MachineDescriptor> machines = buyMachines(normalizedServersToAdd);
					for (MachineDescriptor machineDescriptor : machines) {
						configurable.addMachine(tier, machineDescriptor, true);
					}
					availableToTurnOff.addAll(machines);
				}
				
			}else if( observed/(lambdaPeak * statistics.totalNumberOfActiveServers) < threshold ){
				
				serversToAdd = (int) Math.ceil(observed/lambdaPeak) - statistics.totalNumberOfActiveServers*type.getNumberOfCores();
				
				if(serversToAdd < 0){
					normalizedServersToAdd = removeMachine(statistics, tier, availableToTurnOff, serversToAdd);
				}

			}

//			configurable.config(0, lambdaPeak);
			
			Logger.getLogger(getClass()).debug(String.format("STAT-URGAONKAR REAC %d %d %d %f %f %f %f %d %d %f %s", now, serversToAdd, normalizedServersToAdd, lambdaPeak, statistics.getArrivalRateInLastIntervalInTier(reactiveTickInSeconds), correctedPredictedArrivalRate, correctedPredictedArrivalRate, lost, after, lambdaPeak*statistics.totalNumberOfActiveServers, statistics));
		}
		list.add(availableToTurnOff);
	}

	protected int removeMachine(Statistics statistics, int tier,
			LinkedList<MachineDescriptor> availableToTurnOff, int serversToAdd) {
		return 0;
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
	 * @param statistics {@link Statistics}
	 */
	private double getPeakArrivalRatePerServer(Statistics statistics) {
		
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
