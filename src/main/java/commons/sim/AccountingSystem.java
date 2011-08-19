package commons.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import provisioning.DynamicConfigurable;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.cloud.UtilityResult.UtilityResultEntry;
import commons.config.Configuration;
import commons.io.GEISTWorkloadParser;
import commons.io.TimeBasedWorkloadParser;
import commons.sim.components.MachineDescriptor;
import commons.sim.util.SaaSAppProperties;
import commons.sim.util.SaaSUsersProperties;

public class AccountingSystem {
	
	protected Map<String, List<String>> requestsFinishedPerUser;
	protected Map<String, List<String>> requestsLostPerUser;
	
	private List<User> users;
	private List<Provider> providers;
	
	public AccountingSystem(){
		this.requestsFinishedPerUser = new HashMap<String, List<String>>();
		this.requestsLostPerUser = new HashMap<String, List<String>>();
		
		this.providers = Configuration.getInstance().getProviders();
		
		this.users = Configuration.getInstance().getUsers();
		Collections.sort(users);
	}
	
	public void reportRequestFinished(Request request){
		List<String> requestsFinished = this.requestsFinishedPerUser.get(request.getUserID());
		if(requestsFinished == null){
			requestsFinished = new ArrayList<String>();
			this.requestsFinishedPerUser.put(request.getUserID(), requestsFinished);
		}
		
		requestsFinished.add(request.getReqID());
	}
	
	public int getRequestsFinished(String userID){
		List<String> requestsFinished = this.requestsFinishedPerUser.get(userID);
		return (requestsFinished != null) ? requestsFinished.size() : 0;
	}
	
	public UtilityResultEntry calculateUtility(long currentTimeInMillis){
		UtilityResultEntry entry = new UtilityResultEntry(currentTimeInMillis);
		calculateReceipt(entry);
		calculateCost(entry, currentTimeInMillis);
		return entry;
	}
	
	/**
	 * This method calculates the receipts incurred by SaaS providers periodically. (e.g, each month)  
	 * @param entry 
	 * @return
	 */
	private void calculateReceipt(UtilityResultEntry entry) {
		for (User user : users) {
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
	 * This method calculates the receipts incurred by SaaS providers in a unique period. (e.g, one time
	 * during a whole year)  
	 * @return
	 */
	public double calculateUniqueReceipt(){
		double unicReceipt = 0d;
		for(User user : users){
			unicReceipt += user.calculateOneTimeFees();
		}
		return unicReceipt;
	}
	
	/**
	 * This method calculates the costs incurred by IaaS providers in a unique period. (e.g, one time
	 * during a whole year)  
	 * @param result TODO
	 * @return
	 */
	public double calculateUniqueUtility(UtilityResult result){
		double uniqueCost = 0d;
		for(Provider provider : providers){
			uniqueCost += provider.calculateUniqueCost();
		}
		return uniqueCost;
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
	
	public boolean canBuyMachine(){
		for (Provider provider : providers) {
			if(provider.canBuyMachine(true, MachineType.SMALL) || provider.canBuyMachine(false, MachineType.SMALL)){
				return true;
			}
		}
		return false;
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

	public void reportRequestLost(Request request) {
		List<String> requestsFinished = this.requestsLostPerUser.get(request.getUserID());
		if(requestsFinished == null){
			requestsFinished = new ArrayList<String>();
			this.requestsLostPerUser.put(request.getUserID(), requestsFinished);
		}
		
		requestsFinished.add(request.getReqID());
	}

	public void reportMachineFinish(MachineDescriptor descriptor) {
		for (Provider provider : providers) {
			if(provider.shutdownMachine(descriptor)){
				return;
			}
		}
	}
}
