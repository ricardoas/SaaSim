package saasim.provisioning;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.Request;
import saasim.cloud.User;
import saasim.cloud.UtilityResult;
import saasim.cloud.UtilityResultEntry;
import saasim.config.Configuration;
import saasim.sim.AccountingSystem;
import saasim.sim.DynamicConfigurable;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.provisioningheuristics.MachineStatistics;
import saasim.sim.util.SaaSAppProperties;


/**
 * Default implementation for {@link DPS}. It does static provisioning system ignoring all
 * report information.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class DynamicProvisioningSystem implements DPS{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6216602265072540791L;

	protected AccountingSystem accountingSystem;
	
	protected DynamicConfigurable configurable;
	
	protected User[] users;
	
	protected Provider[] providers;
	
	private long maxRT;
	
	/**
	 * Default constructor.
	 * @throws ConfigurationException 
	 */
	public DynamicProvisioningSystem(User[] users, Provider[] providers){
		this.providers = providers;
		this.users = users;
		this.accountingSystem = new AccountingSystem(users, providers);
		this.maxRT = Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME);
	}
	
	@Override
	public boolean isOptimal() {
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void registerConfigurable(DynamicConfigurable... configurables) {
		
		this.configurable = configurables[0];
		
		addServersToTier(Configuration.getInstance().getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER));
	}
	
	/**
	 * 
	 * @param numberOfInitialServers
	 */
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

	protected List<MachineDescriptor> buyMachines(int numberOfMachines) {
		
		List<MachineDescriptor> currentlyBought = new ArrayList<MachineDescriptor>();
		MachineType[] values = MachineType.values();
		for (int i = values.length - 1; i >= 0; i--) {
			MachineType type = values[i]; 
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
		}
		
		return currentlyBought;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void requestFinished(Request request) {
		assert request.getSaasClient() < users.length:"Unregistered user with ID " + request.getSaasClient() + ". Check configuration files.";
		Logger.getLogger(getClass()).debug("REQUEST-RT " + request.getArrivalTimeInMillis() + " " + request.getResponseTimeInMillis());
		if(request.getResponseTimeInMillis() < maxRT){
			reportFinishedRequest(request);
		}else{
			reportFinishedRequestAfterSLA(request);
		}
	}
	
	protected void reportFinishedRequestAfterSLA(Request request) {
		users[request.getSaasClient()].reportFinishedRequestAfterSLA(request);
	}

	protected void reportFinishedRequest(Request request) {
		users[request.getSaasClient()].reportFinishedRequest(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void requestQueued(long timeMilliSeconds, Request request, int tier) {
		reportLostRequest(request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendStatistics(long now, MachineStatistics statistics, int tier) {
		Logger.getLogger(getClass()).debug(String.format("STAT-STATIC %d %d %s", now, tier, statistics));
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
		return accountingSystem.calculateUtility();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chargeUsers(long currentTimeInMillis) {
		
		UtilityResultEntry entry = accountingSystem.accountPartialUtility(currentTimeInMillis, users, providers);
		Logger.getLogger(getClass()).info(entry);		
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
