package commons.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import provisioning.DynamicConfigurable;

import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.config.Configuration;
import commons.io.GEISTWorkloadParser;
import commons.io.TimeBasedWorkloadParser;
import commons.sim.components.MachineDescriptor;
import commons.sim.util.SaaSUsersProperties;

public class AccountingSystem {
	
	protected Map<String, List<String>> requestsFinishedPerUser;
	protected Map<String, List<String>> requestsLostPerUser;
	
	private List<User> users;

	private List<Provider> providers;
	private int maximumReservedResources;
	
	public AccountingSystem(){
		this.requestsFinishedPerUser = new HashMap<String, List<String>>();
		this.requestsLostPerUser = new HashMap<String, List<String>>();
		
		this.providers = Configuration.getInstance().getProviders();
		
		this.users = Configuration.getInstance().getUsers();
		Collections.sort(users);
		
		this.maximumReservedResources = 0;
	}
	
	public void reportRequestFinished(Request request){
		List<String> requestsFinished = this.requestsFinishedPerUser.get(request.getUserID());
		if(requestsFinished == null){
			requestsFinished = new ArrayList<String>();
			this.requestsFinishedPerUser.put(request.getUserID(), requestsFinished);
		}
		
		requestsFinished.add(request.getRequestID());
	}
	
	public int getRequestsFinished(String userID){
		List<String> requestsFinished = this.requestsFinishedPerUser.get(userID);
		return (requestsFinished != null) ? requestsFinished.size() : 0;
	}
	
	public UtilityResult calculateUtility(long currentTimeInMillis){
		//FIXME! Compute data transferred!
		long totaInTransferred = 0;
		long totalOutTransferred = 0;
		
		UtilityResult result = new UtilityResult(calculateReceipt(), calculateCost(currentTimeInMillis), calculatePenalties(), totaInTransferred, totalOutTransferred);
		return result;
	}
	
	private double calculatePenalties() {
		//TODO: Code me!
		return 0d;
	}
	
	/**
	 * This method calculates the receipts incurred by SaaS providers periodically. (e.g, each month)  
	 * @return
	 */
	private double calculateReceipt() {
		double receipt = 0;
		for (User user : users) {
			receipt += user.calculateReceipt();
		}
		return receipt;
	}
	
	/**
	 * This method calculates the receipts incurred by SaaS providers in a unique period. (e.g, one time
	 * during a whole year)  
	 * @return
	 */
	public double calculateUniqueReceipt(){
		double unicReceipt = 0d;
		for(User user : users){
			unicReceipt += user.calculateUnicReceipt();
		}
		return unicReceipt;
	}
	
	/**
	 * This method calculates the costs incurred by IaaS providers periodically. (e.g, each month)  
	 * @return
	 */
	private double calculateCost(long currentTimeInMillis) {
		double cost = 0.0;
		for (Provider provider : providers) {
			cost += provider.calculateCost(currentTimeInMillis, this.maximumReservedResources);
		}
		return cost;
	}
	
	/**
	 * This method calculates the costs incurred by IaaS providers in a unique period. (e.g, one time
	 * during a whole year)  
	 * @return
	 */
	public double calculateUniqueCost(){
		double unicCost = 0d;
		for(Provider provider : providers){
			unicCost += provider.calculateUnicCost();
		}
		return unicCost;
	}

	/**
	 *  
	 * @param configurable
	 */
	public void setUpConfigurables(DynamicConfigurable configurable) {
		int[] initialServersPerTier = Configuration.getInstance().getApplicationInitialServersPerTier();

		for (int tier = 0; tier < initialServersPerTier.length; tier++) {
			for (int i = 0; i < initialServersPerTier[tier]; i++) {
				configurable.addServer(tier, buyMachine(), false);
			}
		}

		String[] workloads = Configuration.getInstance().getStringArray(SaaSUsersProperties.USER_WORKLOAD);

		configurable.setWorkloadParser(new TimeBasedWorkloadParser(new GEISTWorkloadParser(workloads), TimeBasedWorkloadParser.HOUR_IN_MILLIS));
	}
	
	public boolean canBuyMachine(){
		for (Provider provider : providers) {
			if(provider.canBuyMachine(true) || provider.canBuyMachine(false)){
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
			if(provider.canBuyMachine(false)){
				return provider.buyMachine(false);
			}
		}
		return null;
	}

	private MachineDescriptor requestReservedMachine() {
		for (Provider provider : providers) {
			if(provider.canBuyMachine(true)){
				return provider.buyMachine(true);
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
		
		requestsFinished.add(request.getRequestID());
	}

	public void reportMachineFinish(MachineDescriptor descriptor) {
		for (Provider provider : providers) {
			if(provider.shutdownMachine(descriptor)){
				return;
			}
		}
	}

	public void setMaximumNumberOfReservedMachinesUsed(int maximumReservedResources) {
		this.maximumReservedResources = maximumReservedResources;
	}
	
	public double[] getResourcesData(){
		double[] results = new double[4];
		for (Provider provider : providers) {
			double[] currentResult = provider.resourcesConsumption();
			for(int i = 0; i < currentResult.length; i++){
				results[i] += currentResult[i];
			}
		}
		
		return results;
	}
}
