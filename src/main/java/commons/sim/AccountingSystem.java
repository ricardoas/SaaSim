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
import commons.config.Configuration;
import commons.io.GEISTWorkloadParser;
import commons.io.TimeBasedWorkloadParser;
import commons.sim.components.MachineDescriptor;
import commons.sim.util.UsersProperties;

public class AccountingSystem {
	
	protected Map<String, List<String>> requestsFinishedPerUser;
	
	protected double totalTransferred;
	
	
	private List<User> users;

	private List<Provider> providers;
	
	public AccountingSystem(){
		this.totalTransferred = 0d;
		
		this.requestsFinishedPerUser = new HashMap<String, List<String>>();
		
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
		
		requestsFinished.add(request.getRequestID());
		this.totalTransferred += request.getSizeInBytes();
	}
	
	public int getRequestsFinished(String userID){
		List<String> requestsFinished = this.requestsFinishedPerUser.get(userID);
		return (requestsFinished != null) ? requestsFinished.size() : 0;
	}
	
	public double calculateUtility(){
		return calculateReceipt() - calculateCost();
	}
	
	private double calculateReceipt() {
		double receipt = 0;
		for (User user : users) {
			receipt += user.calculateReceipt();
		}
		return receipt;
	}

	private double calculateCost() {
		double cost = 0.0;
		for (Provider provider : providers) {
			cost += provider.calculateCost();
		}
		return cost;
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

		String[] workloads = Configuration.getInstance().getStringArray(UsersProperties.USER_WORKLOAD);

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

	public void reportLostRequest(Request request) {
		// FIXME Code me!
	}

	public void reportMachineFinish(MachineDescriptor descriptor) {
		for (Provider provider : providers) {
			if(provider.shutdownMachine(descriptor)){
				return;
			}
		}
	}
}
