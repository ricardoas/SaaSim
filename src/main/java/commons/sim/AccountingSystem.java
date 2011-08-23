package commons.sim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import provisioning.DynamicConfigurable;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.cloud.UtilityResultEntry;
import commons.config.Configuration;
import commons.io.GEISTWorkloadParser;
import commons.io.TimeBasedWorkloadParser;
import commons.sim.components.MachineDescriptor;
import commons.sim.util.SaaSAppProperties;
import commons.sim.util.SaaSUsersProperties;

public class AccountingSystem {
	
	protected Map<String, List<String>> requestsLostPerUser;
	
	private Map<Integer, User> users;
	private List<Provider> providers;

	private UtilityResult utilityResult;
	
	public AccountingSystem(){
		this.requestsLostPerUser = new HashMap<String, List<String>>();
		this.utilityResult = new UtilityResult();
		
		this.providers = Configuration.getInstance().getProviders();
		
		users = new HashMap<Integer, User>();
		List<User> listOfUsers = Configuration.getInstance().getUsers();
		for (User user : listOfUsers) {
			users.put(user.getId(), user);
		}
	}
	
	public void reportFinishedRequest(Request request){
		if(!users.containsKey(request.getUserID())){
			throw new RuntimeException("Unregistered user with ID " + request.getUserID() + ". Check configuration files.");
		}
		users.get(request.getUserID()).reportFinishedRequest(request);
	}
	
	/**
	 * @param request
	 */
	public void reportLostRequest(Request request) {
		if(!users.containsKey(request.getUserID())){
			throw new RuntimeException("Unregistered user with ID " + request.getUserID() + ". Check configuration files.");
		}
		users.get(request.getUserID()).reportLostRequest(request);
	}

	public void accountPartialUtility(long currentTimeInMillis){
		UtilityResultEntry entry = new UtilityResultEntry(currentTimeInMillis);
		calculateReceipt(entry);
		calculateCost(entry, currentTimeInMillis);
		this.utilityResult.addEntry(entry);
	}
	
	/**
	 * This method calculates the receipts incurred by SaaS providers periodically. (e.g, each month)  
	 * @param entry 
	 * @return
	 */
	private void calculateReceipt(UtilityResultEntry entry) {
		for (User user : users.values()) {
			user.calculatePartialReceipt(entry);
		}
	}
	
	/**
	 * This method calculates the costs incurred by IaaS providers periodically. (e.g, each month)  
	 * @param currentTimeInMillis 
	 * @return
	 */
	private void calculateCost(UtilityResultEntry entry, long currentTimeInMillis) {
		for (Provider provider : providers) {
			provider.calculateCost(entry, currentTimeInMillis);
		}
	}

	/**
	 * This method calculates the costs incurred by IaaS providers in a unique period. (e.g, one time
	 * during a whole year)  
	 * @return
	 */
	public UtilityResult calculateUtility(){
		for(Provider provider : providers){
			provider.calculateUniqueCost(utilityResult);
		}
		for(User user : users.values()){
			user.calculateOneTimeFees(utilityResult);
		}
		return utilityResult;
	}

	/**
	 *  
	 * @param configurable
	 */
	public void setUpConfigurables(DynamicConfigurable configurable) {
		int[] initialServersPerTier = Configuration.getInstance().getIntegerArray(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER);

		for (int tier = 0; tier < initialServersPerTier.length; tier++) {
			for (int i = 0; i < initialServersPerTier[tier]; i++) {
				configurable.addServer(tier, buyMachine(), false);
			}
		}

		String[] workloads = Configuration.getInstance().getStringArray(SaaSUsersProperties.SAAS_USER_WORKLOAD);

		configurable.setWorkloadParser(new TimeBasedWorkloadParser(new GEISTWorkloadParser(workloads), TimeBasedWorkloadParser.HOUR_IN_MILLIS));
	}
	
	public List<Provider> canBuyMachine(MachineType type, boolean isReserved){
		ArrayList<Provider> available = new ArrayList<Provider>();
		for (Provider provider : providers) {
			if(provider.canBuyMachine(isReserved, type)){
				available.add(provider);
			}
		}
		return available;
	}
	
	public MachineDescriptor buyMachine() {
		MachineDescriptor descriptor = requestReservedMachine();
		if(descriptor != null){
			return descriptor;
		}
		
		descriptor = requestOnDemandMachine();
		if(descriptor != null){
			return descriptor;
		}
		
		return descriptor;
	}

	private MachineDescriptor requestOnDemandMachine() {
		for (Provider provider : providers) {
			if(provider.canBuyMachine(false, MachineType.SMALL)){
				return provider.buyMachine(false, MachineType.SMALL);
			}
		}
		return null;
	}

	private MachineDescriptor requestReservedMachine() {
		for (Provider provider : providers) {
			if(provider.canBuyMachine(true, MachineType.SMALL)){
				return provider.buyMachine(true, MachineType.SMALL);
			}
		}
		return null;
	}

	public void reportMachineFinish(MachineDescriptor descriptor) {
		for (Provider provider : providers) {
			if(provider.shutdownMachine(descriptor)){
				return;
			}
		}
		throw new RuntimeException("No provider is responsible for machine " + descriptor);
	}
}
