package commons.sim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.cloud.Request;
import commons.util.Triple;

public class AccountingSystem {
	
	private Map<String, List<String>> requestsFinishedPerUser;
	private Map<Long, Triple> machineUtilization;
	
	private List<Long> reservedMachinesIDs;
	private List<Long> onDemandMachinesIDs;
	
	private final int resourcesReservationLimit;
	private final int onDemandLimit;
	
	public AccountingSystem(int resourcesReservationLimit, int onDemandLimit){
		this.resourcesReservationLimit = resourcesReservationLimit;
		this.onDemandLimit = onDemandLimit;
		
		this.requestsFinishedPerUser = new HashMap<String, List<String>>();
		this.machineUtilization = new HashMap<Long, Triple>();
		
		this.reservedMachinesIDs = new ArrayList<Long>();
		this.onDemandMachinesIDs = new ArrayList<Long>();
	}
	
	public void reportRequestFinished(Request request){
		List<String> requestsFinished = this.requestsFinishedPerUser.get(request.getUserID());
		if(requestsFinished == null){
			requestsFinished = new ArrayList<String>();
			this.requestsFinishedPerUser.put(request.getUserID(), requestsFinished);
		}
		
		requestsFinished.add(request.getRequestID());
	}
	
	public void reportMachineFinish(long machineID, double machineEndTime){
		Triple machineData = this.machineUtilization.get(machineID);
		if(machineData == null){
			throw new RuntimeException("Could not report utilization for inexistent machine: "+machineID);
		}
		machineData.secondValue = machineEndTime;
		this.machineUtilization.put(machineID, machineData);
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
}
