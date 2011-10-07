package provisioning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import provisioning.util.WorkloadParserFactory;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.config.Configuration;
import commons.sim.AccountingSystem;
import commons.sim.DynamicConfigurable;
import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.util.SaaSAppProperties;

/**
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class DynamicProvisioningSystem implements DPS{

	protected final AccountingSystem accountingSystem;
	
	protected DynamicConfigurable configurable;
	
	protected final User[] users;
	
	protected final Provider[] providers;
	
	/**
	 * Default constructor.
	 */
	public DynamicProvisioningSystem() {
		Configuration config = Configuration.getInstance();
		this.providers = config.getProviders();
		this.users = config.getUsers();
		this.accountingSystem = new AccountingSystem(users.length, providers.length);
	}
	
	@Override
	public void registerConfigurable(DynamicConfigurable configurable) {
		Configuration config = Configuration.getInstance();
		
		this.configurable = configurable;
		int[] initialServersPerTier = config.getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER);
		if(config.hasPreviousMachines()){
			for (int tier = 0; tier < initialServersPerTier.length; tier++) {
				addPreviousMachinesToTier(configurable, tier, config.getPreviousMachines());
			}
		}else{
			List<MachineType> typeList = Arrays.asList(MachineType.values());
			Collections.reverse(typeList);
			//Looking for reserved instances!
			for (int tier = 0; tier < initialServersPerTier.length; tier++) {
				addServersToTier(configurable, tier, initialServersPerTier[tier], typeList);
			}
		}
		
		configurable.setWorkloadParser(WorkloadParserFactory.getWorkloadParser());
	}
	
	private void addPreviousMachinesToTier(DynamicConfigurable configurable, int tier, List<Machine> previousMachines) {
		for(Machine machine : previousMachines){
			configurable.addServer(tier, machine);
		}
	}

	private void addServersToTier(DynamicConfigurable configurable, int tier, int numberOfInitialServers, List<MachineType> typeList) {
		int serversAdded = 0;
		for(MachineType machineType : typeList){
			for (Provider provider : this.providers) {
				while(provider.canBuyMachine(true, machineType) && serversAdded < numberOfInitialServers){
					configurable.addServer(tier, provider.buyMachine(true, machineType), false);
					serversAdded++;
				}
				if(serversAdded == numberOfInitialServers){
					return;
				}
			}
		}
	}

	@Override
	public void reportRequestFinished(Request request) {
		assert request.getSaasClient() < users.length:"Unregistered user with ID " + request.getSaasClient() + ". Check configuration files.";
		
		try{
			users[request.getSaasClient()].reportFinishedRequest(request);
		}catch(NullPointerException e){
			throw e;
		}
	}
	
	@Override
	public void requestQueued(long timeMilliSeconds, Request request, int tier) {
		reportLostRequest(request);
	}

	@Override
	public void sendStatistics(long now, MachineStatistics statistics, int tier) {
		// Nothing to do
		
	}

	@Override
	public void machineTurnedOff(MachineDescriptor machineDescriptor) {
		assert machineDescriptor.getProviderID() < providers.length: "Inexistent provider, check configuration files.";
		providers[machineDescriptor.getProviderID()].shutdownMachine(machineDescriptor);
	}

	@Override
	public UtilityResult calculateUtility() {
		return this.accountingSystem.calculateUtility(users, providers);
	}

	@Override
	public void chargeUsers(long currentTimeInMillis) {
		this.accountingSystem.accountPartialUtility(currentTimeInMillis, users, providers);
	}

	/**
	 * @param request
	 */
	protected void reportLostRequest(Request request) {
		assert request.getSaasClient() < users.length: "Unregistered user with ID " + request.getSaasClient() + ". Check configuration files.";
		
		users[request.getSaasClient()].reportLostRequest(request);
	}
	
	protected List<Provider> canBuyMachine(MachineType type, boolean isReserved){
		List<Provider> available = new ArrayList<Provider>();
		for (Provider provider : providers) {
			if(provider.canBuyMachine(isReserved, type)){
				available.add(provider);
			}
		}
		return available;
	}

	protected MachineDescriptor buyMachine(Provider provider, MachineType instanceType, boolean isReserved){
		return provider.buyMachine(isReserved, instanceType);
	}
}
