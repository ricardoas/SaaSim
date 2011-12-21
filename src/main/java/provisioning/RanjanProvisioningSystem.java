package provisioning;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.MDC;

import provisioning.util.DPSInfo;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.config.Configuration;
import commons.io.Checkpointer;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.util.SimulatorProperties;
import commons.util.TimeUnit;

/**
 * Simple implementation of QuID algorithm as depicted in: 
 * <a href='http://dx.doi.org/10.1109/IWQoS.2002.1006569'>http://dx.doi.org/10.1109/IWQoS.2002.1006569<a>
 * <br>
 * This implementation is not ready to handle the problem of heterogeneous machines.
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public class RanjanProvisioningSystem extends DynamicProvisioningSystem {
	
	private static String PROP_MACHINE_TYPE = "dps.ranjan.type";  
	private static String PROP_TARGET_UTILISATION = "dps.ranjan.target";  

	private double targetUtilisation;
	private long reactiveTick;
	private MachineType type;
	
	private LinkedList<LinkedList<MachineDescriptor>> list;

	/**
	 * Default constructor
	 */
	public RanjanProvisioningSystem() {
		super();
		type = MachineType.valueOf(Configuration.getInstance().getString(PROP_MACHINE_TYPE).toUpperCase());
		targetUtilisation = Configuration.getInstance().getDouble(PROP_TARGET_UTILISATION);
		reactiveTick = TimeUnit.HOUR.getMillis()/Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL);
		list = loadDPSInfo().list;
	}
	
	/**
	 * @return {@link DPSInfo}
	 */
	private DPSInfo loadDPSInfo() {
		DPSInfo info = Checkpointer.loadProvisioningInfo();
		if(info.list == null){
			info.list = new LinkedList<LinkedList<MachineDescriptor>>();
			for (int i = 0; i < reactiveTick; i++) {
				info.list.add(new LinkedList<MachineDescriptor>());
			}
		}
		return info;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addServersToTier(int[] numberOfInitialServersPerTier) {
		
		int numberOfMachines = 0;
		for (int i : numberOfInitialServersPerTier) {
			numberOfMachines += i;
		}
		
		List<MachineDescriptor> currentlyBought = buyMachines(numberOfMachines);
		
		while(currentlyBought.size() != 0){
			for (int i = 0; i < numberOfInitialServersPerTier.length; i++) {
				if(numberOfInitialServersPerTier[i] != 0){
					numberOfInitialServersPerTier[i]--;
					configurable.addMachine(i, currentlyBought.remove(0), false);
				}
			}
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
	 * {@inheritDoc}
	 */
	@Override
	public void sendStatistics(long now, MachineStatistics statistics, int tier) {
		int numberOfServersToAdd = evaluateNumberOfServersForNextInterval(statistics);
		
		log.debug(String.format("STAT-RANJAN %d %d %d %s", now, tier, numberOfServersToAdd, statistics));
		
		LinkedList<MachineDescriptor> availableToTurnOff = list.poll();
		
		if(numberOfServersToAdd > 0){
			
			List<MachineDescriptor> machines = buyMachines(numberOfServersToAdd);

			for (MachineDescriptor machineDescriptor : machines) {
				configurable.addMachine(tier, machineDescriptor, true);
			}

			availableToTurnOff.addAll(machines);
//			list.add(availableToTurnOff);
			
		}else if(numberOfServersToAdd < 0){
			if(!availableToTurnOff.isEmpty()){
				for (int i = 0; i < Math.min(-numberOfServersToAdd,availableToTurnOff.size()); i++) {
					configurable.removeMachine(tier,  availableToTurnOff.poll(), false);
				}
			}
		}
		
		list.add(availableToTurnOff);
	}

	/**
	 * Decides how many machines are needed to buy (release) according to collected statistics.
	 * 
	 * @param statistics {@link MachineStatistics}
	 * @return The number of machines to buy, if positive, or to release, otherwise. 
	 */
	protected int evaluateNumberOfServersForNextInterval(MachineStatistics statistics) {
		assert statistics.totalNumberOfServers != 0;
		assert statistics.requestCompletions != 0;
		
		double d = statistics.averageUtilisation / statistics.requestCompletions;
		
		double u_lign = Math.max(statistics.requestArrivals, statistics.requestCompletions) * d;
		int newNumberOfServers = (int) Math.ceil( u_lign * statistics.totalNumberOfServers / targetUtilisation );
		
		return Math.max(1, newNumberOfServers) - statistics.totalNumberOfServers;
	}
}
