package provisioning;

import java.util.ArrayList;
import java.util.List;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.config.Configuration;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.util.SaaSAppProperties;

/**
 * This class represents the DPS business logic according to RANJAN. Here some statistics of current
 * available machines (i.e, utilisation) is used to verify if new machines need to be added to 
 * an application tier, or if some machines can be removed from any application tier.
 * @author davidcmm
 *
 */
public class RanjanProvisioningSystem extends DynamicProvisioningSystem {
	
	private static String PROP_MACHINE_TYPE = "dps.ranjan.type";  

	private double TARGET_UTILIZATION = 0.66;
	private MachineType type;

	public static long UTILIZATION_EVALUATION_PERIOD_IN_MILLIS = 1000 * 60 * 5;//in millis

	@SuppressWarnings("unchecked")
	public RanjanProvisioningSystem() {
		super();
		type = MachineType.valueOf(Configuration.getInstance().getString(PROP_MACHINE_TYPE).toUpperCase());
	}
	
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
					configurable.addServer(i, currentlyBought.remove(0), false);
				}
			}
		}
	}

	private List<MachineDescriptor> buyMachines(int numberOfMachines) {
		List<MachineDescriptor> currentlyBought = new ArrayList<MachineDescriptor>();
		
		while(currentlyBought.size() != numberOfMachines){
			for (Provider provider : providers) {
				if(provider.canBuyMachine(true, type)){
					currentlyBought.add(provider.buyMachine(true, type));
				}
			}
			for (Provider provider : providers) {
				if(provider.canBuyMachine(false, type)){
					currentlyBought.add(provider.buyMachine(false, type));
				}
			}
		}
		return currentlyBought;
	}
	
	@Override
	public void sendStatistics(long now, MachineStatistics statistics, int tier) {
		super.sendStatistics(now, statistics, tier);
		int numberOfServersToAdd = evaluateNumberOfServersForNextInterval(statistics);
		
		if(numberOfServersToAdd > 0){
			
			if(numberOfServersToAdd > statistics.warmingDownMachines){
				numberOfServersToAdd -= statistics.warmingDownMachines;
				List<MachineDescriptor> machines = buyMachines(numberOfServersToAdd);
				for (MachineDescriptor machineDescriptor : machines) {
					configurable.addServer(tier, machineDescriptor, true);
				}
				
				configurable.cancelMachineShutdown(tier, statistics.warmingDownMachines);
			}else{
				configurable.cancelMachineShutdown(tier, numberOfServersToAdd);
			}
			
		}else if(numberOfServersToAdd < 0){
			for (int i = 0; i < -numberOfServersToAdd; i++) {
				configurable.removeServer(tier, false);
			}
		}
	}

	public int evaluateNumberOfServersForNextInterval(MachineStatistics statistics) {
		double averageUtilisation = statistics.averageUtilisation / statistics.totalNumberOfServers;
		double d;
		if(statistics.numberOfRequestsCompletionsInLastInterval == 0){
			d = averageUtilisation;
		}else{
			d = averageUtilisation / statistics.numberOfRequestsCompletionsInLastInterval;
		}
		
		double u_lign = Math.max(statistics.numberOfRequestsArrivalInLastInterval, statistics.numberOfRequestsCompletionsInLastInterval) * d;
		int newNumberOfServers = (int) Math.ceil( u_lign * statistics.totalNumberOfServers / TARGET_UTILIZATION );
		
		int numberOfServersToAdd = (newNumberOfServers - statistics.totalNumberOfServers);
		
		if(newNumberOfServers == 0)
		
		if(numberOfServersToAdd != 0){
			return numberOfServersToAdd;
		}
		if(statistics.numberOfRequestsArrivalInLastInterval > 0 && 
				statistics.totalNumberOfServers == 0){
			return 1;
		}
		return numberOfServersToAdd;
	}
	
	private void evaluateMachinesToBeAdded(int tier) {
		List<Provider> providers = canBuyMachine(MachineType.M1_SMALL, false);
		if(!providers.isEmpty()){
			configurable.addServer(tier, buyMachine(providers.get(0), MachineType.M1_SMALL, false), true);
		}
	}
}
