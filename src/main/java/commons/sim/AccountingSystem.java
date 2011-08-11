package commons.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import provisioning.DynamicConfigurable;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityFunction;
import commons.config.SimulatorConfiguration;
import commons.io.GEISTWorkloadParser;
import commons.io.TimeBasedWorkloadParser;
import commons.sim.components.MachineDescriptor;
import commons.sim.util.UsersProperties;
import commons.util.Triple;

public class AccountingSystem {
	
	protected Map<String, List<String>> requestsFinishedPerUser;
	
	private final int resourcesReservationLimit;
	private final int onDemandLimit;
	protected double totalTransferred;
	
	private UtilityFunction utilityFunction;
	
	private int machineIDGenerator;
	private List<User> users;
	private Map<Long, MachineDescriptor> reservedMachines;
	private Map<Long, MachineDescriptor> onDemandMachines;
	
	private List<MachineDescriptor> finishedMachines;
	
	public AccountingSystem(int resourcesReservationLimit, int onDemandLimit){
		if(resourcesReservationLimit < 0 || onDemandLimit < 0){
			throw new RuntimeException("Invalid accounting construction: "+resourcesReservationLimit+" "+onDemandLimit);
		}
		
		this.resourcesReservationLimit = resourcesReservationLimit;
		this.onDemandLimit = onDemandLimit;
		
		this.totalTransferred = 0d;
		
		this.requestsFinishedPerUser = new HashMap<String, List<String>>();
		
		this.utilityFunction = new UtilityFunction();
		
		this.users = new ArrayList<User>(SimulatorConfiguration.getInstance().getContractsPerUser().keySet());
		Collections.sort(users);
		
		this.reservedMachines = new HashMap<Long, MachineDescriptor>();
		this.onDemandMachines = new HashMap<Long, MachineDescriptor>();
		this.machineIDGenerator = 0;
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
	
	public double getMachineUtilization(long machineID){
		Triple<Long, Long, Double> machineData = this.machineUtilization.get(machineID);
		if(machineData != null){
			return ((Long)machineData.secondValue - (Long)machineData.firstValue);
		}else{
			return 0d;
		}
	}

	public double calculateUtility(){
		return calculateReceipt() - calculateCost();
	}
	
	private double calculateReceipt() {
		double receipt = 0;
		for (User user : users) {
			receipt += utilityFunction.calculateReceipt(user);
		}
		return receipt;
	}

	private int calculateCost() {
		double cost = previousCostT
		return 0;
	}

	public double calculateCost(Provider provider) {
		return this.utilityFunction.calculateCost(this.totalTransferred, provider);
	}
	
	public double calculateTotalReceipt(Contract contract, User user) {
		return this.utilityFunction.calculateTotalReceipt(contract, user);
	}
	
	public double calcExtraReceipt(Contract contract, User user) {
		return this.utilityFunction.calcExtraReceipt(contract, user);
	}

	public Map<Long, Triple<Long, Long, Double>> getReservedMachinesData(){
		Map<Long, Triple<Long, Long, Double>> result = new HashMap<Long, Triple<Long, Long,Double>>();
		for(Long machineID : this.reservedMachinesIDs){
			Triple<Long, Long, Double> data = this.machineUtilization.get(machineID);
			result.put(machineID, data);
		}
		return result; 
	}
	
	public Map<Long, Triple<Long, Long, Double>> getOnDemandMachinesData(){
		Map<Long, Triple<Long, Long, Double>> result = new HashMap<Long, Triple<Long, Long,Double>>();
		for(Long machineID : this.onDemandMachinesIDs){
			Triple<Long, Long, Double> data = this.machineUtilization.get(machineID);
			result.put(machineID, data);
		}
		return result;
	}

	/**
	 *  
	 * @param configurable
	 */
	public void setUpConfigurables(DynamicConfigurable configurable) {
		int[] initialServersPerTier = SimulatorConfiguration.getInstance().getApplicationInitialServersPerTier();

		for (int tier = 0; tier < initialServersPerTier.length; tier++) {
			for (int i = 0; i < initialServersPerTier[tier]; i++) {
				configurable.addServer(tier, buyMachine(), false);
			}
		}

		String[] workloads = SimulatorConfiguration.getInstance().getStringArray(UsersProperties.USER_WORKLOAD);

		configurable.setWorkloadParser(new TimeBasedWorkloadParser(new GEISTWorkloadParser(workloads), TimeBasedWorkloadParser.HOUR_IN_MILLIS));
	}
	
	public boolean canBuyMachine(){
		return onDemandMachines.size() < onDemandLimit || reservedMachines.size() < resourcesReservationLimit;
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
		
		if (onDemandMachines.size() < onDemandLimit){
			MachineDescriptor descriptor = new MachineDescriptor(machineIDGenerator++, false);
			onDemandMachines.put(descriptor.getMachineID(), descriptor);
			return descriptor;
		}
		return null;
	}

	private MachineDescriptor requestReservedMachine() {
		if (reservedMachines.size() < resourcesReservationLimit){
			MachineDescriptor descriptor = new MachineDescriptor(machineIDGenerator++, true);
			reservedMachines.put(descriptor.getMachineID(), descriptor);
			return descriptor;
		}
		return null;
	}

	public void reportLostRequest(Request request) {
		// FIXME Code me!
	}

	public void reportMachineFinish(MachineDescriptor machineDescriptor) {
		Triple<Long, Long, Double> machineData = this.machineUtilization.get(machineID);
		if(machineData == null){
			throw new RuntimeException("Could not report utilization for inexistent machine: "+machineID);
		}
		if(machineEndTimeInMillis <= (long) machineData.firstValue){
			throw new RuntimeException("Machine can not finish before start: "+machineData.firstValue+" to "+machineEndTimeInMillis);
		}
		machineData.secondValue = machineEndTimeInMillis;
		this.machineUtilization.put(machineID, machineData);
		
		//Indicating that a new machine can be created
		MachineDescriptor removed = onDemandMachines.remove(machineDescriptor.getMachineID());
		if(removed == null){
			removed = reservedMachines.remove(machineDescriptor.getMachineID());
		}
		
		finishedMachines.add(removed);
	}
}
