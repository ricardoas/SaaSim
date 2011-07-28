package commons.sim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityFunction;
import commons.util.Triple;

public class AccountingSystem {
	
	protected Map<String, List<String>> requestsFinishedPerUser;
	protected Map<Long, Triple> machineUtilization;
	
	protected List<Long> reservedMachinesIDs;
	protected List<Long> onDemandMachinesIDs;
	
	private final int resourcesReservationLimit;
	private final int onDemandLimit;
	protected double totalTransferred;
	
	private UtilityFunction utilityFunction;
	
	public AccountingSystem(int resourcesReservationLimit, int onDemandLimit){
		if(resourcesReservationLimit < 0 || onDemandLimit < 0){
			throw new RuntimeException("Invalid accounting construction: "+resourcesReservationLimit+" "+onDemandLimit);
		}
		
		this.resourcesReservationLimit = resourcesReservationLimit;
		this.onDemandLimit = onDemandLimit;
		
		this.totalTransferred = 0d;
		
		this.requestsFinishedPerUser = new HashMap<String, List<String>>();
		this.machineUtilization = new HashMap<Long, Triple>();
		
		this.reservedMachinesIDs = new ArrayList<Long>();
		this.onDemandMachinesIDs = new ArrayList<Long>();
		
		this.utilityFunction = new UtilityFunction();
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
	
	public void reportMachineFinish(long machineID, double machineEndTime){
		Triple machineData = this.machineUtilization.get(machineID);
		if(machineData == null){
			throw new RuntimeException("Could not report utilization for inexistent machine: "+machineID);
		}
		if(machineEndTime <= (Double) machineData.firstValue){
			throw new RuntimeException("Machine can not finish before start: "+machineData.firstValue+" to "+machineEndTime);
		}
		machineData.secondValue = machineEndTime;
		this.machineUtilization.put(machineID, machineData);
		
		//Indicating that a new machine can be created
		this.reservedMachinesIDs.remove(machineID);
		this.onDemandMachinesIDs.remove(machineID);
	}
	
	public int getRequestsFinished(String userID){
		List<String> requestsFinished = this.requestsFinishedPerUser.get(userID);
		return (requestsFinished != null) ? requestsFinished.size() : 0;
	}
	
	public double getMachineUtilization(long machineID){
		Triple machineData = this.machineUtilization.get(machineID);
		if(machineData != null){
			return ((Double)machineData.secondValue - (Double)machineData.firstValue);
		}else{
			return 0d;
		}
	}

	public void createMachine(long machineID, boolean isReserved, double machineStartTime) {
		if(isReserved){
			this.reservedMachinesIDs.add(machineID);
		}else{
			this.onDemandMachinesIDs.add(machineID);
		}
		
		Triple machineData = new Triple();
		machineData.firstValue = machineStartTime;
		this.machineUtilization.put(machineID, machineData);
	}

	public boolean canAddAReservedMachine() {
		return this.reservedMachinesIDs.size() < this.resourcesReservationLimit;
	}

	public boolean canAddAOnDemandMachine() {
		return this.onDemandMachinesIDs.size() < this.onDemandLimit;
	}

	public double calculateUtility(Contract contract, User user, Provider provider){
		return this.utilityFunction.calculateUtility(contract, user, provider);
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
}
