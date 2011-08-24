package provisioning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.config.Configuration;
import commons.io.GEISTWorkloadParser;
import commons.io.TimeBasedWorkloadParser;
import commons.sim.AccountingSystem;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.RanjanStatistics;
import commons.sim.util.SaaSAppProperties;
import commons.sim.util.SaaSUsersProperties;

/**
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class DynamicProvisioningSystem implements DPS{

	protected long availableIDs;
	
	protected AccountingSystem accountingSystem;
	
	protected DynamicConfigurable configurable;
	
	protected final Map<Long, DynamicConfigurable> configurables;

	private Map<Integer, User> users;
	
	private Map<String, Provider> providers;
	
	/**
	 * Default constructor.
	 */
	public DynamicProvisioningSystem() {
		this.availableIDs = 0;
		this.configurables = new HashMap<Long, DynamicConfigurable>();
		
		this.providers = new TreeMap<String, Provider>();
		List<Provider> listOfProviders = Configuration.getInstance().getProviders();
		for (Provider provider : listOfProviders) {
			this.providers.put(provider.getName(), provider);
		}
		
		this.users = new HashMap<Integer, User>();
		List<User> listOfUsers = Configuration.getInstance().getUsers();
		for (User user : listOfUsers) {
			this.users.put(user.getId(), user);
		}
		
		this.accountingSystem = new AccountingSystem();
	}
	
	@Override
	public void registerConfigurable(DynamicConfigurable configurable) {
		this.configurable = configurable;
		int[] initialServersPerTier = Configuration.getInstance().getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER);

		for (int tier = 0; tier < initialServersPerTier.length; tier++) {
			for (int i = 0; i < initialServersPerTier[tier]; i++) {
				//configurable.addServer(tier, buyMachine(), false);
			}
		}

		String[] workloads = Configuration.getInstance().getStringArray(SaaSUsersProperties.SAAS_USER_WORKLOAD);

		configurable.setWorkloadParser(new TimeBasedWorkloadParser(new GEISTWorkloadParser(workloads), TimeBasedWorkloadParser.HOUR_IN_MILLIS));
	}

	@Override
	public void reportRequestFinished(Request request) {
		if(!users.containsKey(request.getUserID())){
			throw new RuntimeException("Unregistered user with ID " + request.getUserID() + ". Check configuration files.");
		}
		users.get(request.getUserID()).reportFinishedRequest(request);
	}
	
	@Override
	public void requestQueued(long timeMilliSeconds, Request request, int tier) {
		
	}

	@Override
	public void evaluateUtilisation(long now, RanjanStatistics statistics, int tier) {
		
	}

	@Override
	public void machineTurnedOff(MachineDescriptor machineDescriptor) {
		for (Provider provider : providers.values()) {
			if(provider.shutdownMachine(machineDescriptor)){
				return;
			}
		}
		throw new RuntimeException("No provider is responsible for machine " + machineDescriptor);
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
		if(!users.containsKey(request.getUserID())){
			throw new RuntimeException("Unregistered user with ID " + request.getUserID() + ". Check configuration files.");
		}
		users.get(request.getUserID()).reportLostRequest(request);
	}
}
