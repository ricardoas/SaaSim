package provisioning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.config.Configuration;
import commons.io.Checkpointer;
import commons.io.WorkloadParserFactory;
import commons.sim.AccountingSystem;
import commons.sim.DynamicConfigurable;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.util.SaaSAppProperties;

/**
 * Default implementation for {@link DPS}. It does static provisioning system ignoring all
 * report information.
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
		this.providers = Checkpointer.loadProviders();
		this.users = Checkpointer.loadUsers();
		this.accountingSystem = Checkpointer.loadAccountingSystem();
	}
	
	@Override
	public boolean isOptimal() {
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerConfigurable(DynamicConfigurable configurable) {
		
		this.configurable = configurable;
		
		if(Checkpointer.loadSimulationInfo().isFirstDay()){
			int[] initialServersPerTier = Configuration.getInstance().getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER);
			
			List<MachineType> typeList = Arrays.asList(MachineType.values());
			Collections.reverse(typeList);
			//Looking for reserved instances!
			for (int tier = 0; tier < initialServersPerTier.length; tier++) {
				addServersToTier(configurable, tier, initialServersPerTier[tier], typeList);
			}
		}
		
		configurable.setWorkloadParser(WorkloadParserFactory.getWorkloadParser());
		configurable.setMonitor(this);
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
		
		//Adding on-demand machines
		for (Provider provider : this.providers) {
			while(provider.canBuyMachine(false, MachineType.M1_XLARGE) && serversAdded < numberOfInitialServers){
				configurable.addServer(tier, provider.buyMachine(false, MachineType.M1_XLARGE), false);
				serversAdded++;
			}
			if(serversAdded == numberOfInitialServers){
				return;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestFinished(Request request) {
		assert request.getSaasClient() < users.length:"Unregistered user with ID " + request.getSaasClient() + ". Check configuration files.";
		
		try{
			users[request.getSaasClient()].reportFinishedRequest(request);
		}catch(NullPointerException e){
			throw e;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void requestQueued(long timeMilliSeconds, Request request, int tier) {
		reportLostRequest(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendStatistics(long now, MachineStatistics statistics, int tier) {
		// Nothing to do
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void machineTurnedOff(MachineDescriptor machineDescriptor) {
		assert machineDescriptor.getProviderID() < providers.length: "Inexistent provider, check configuration files.";
		providers[machineDescriptor.getProviderID()].shutdownMachine(machineDescriptor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UtilityResult calculateUtility() {
		return this.accountingSystem.calculateUtility();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chargeUsers(long currentTimeInMillis) {
		this.accountingSystem.accountPartialUtility(currentTimeInMillis);
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
