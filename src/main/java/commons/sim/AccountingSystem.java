package commons.sim;

import java.util.HashMap;
import java.util.Map;

import commons.cloud.Request;
import commons.util.Triple;

public class AccountingSystem {
	
	private Map<String, Integer> requestsFinished;
	private Map<Long, Triple> machineUtilization;
	
	public AccountingSystem(){
		this.requestsFinished = new HashMap<String, Integer>();
		this.machineUtilization = new HashMap<Long, Triple>();
	}
	
	public void reportRequestFinished(Request request){
		Integer totalFinished = this.requestsFinished.get(request.getUserID());
		if(totalFinished == null){
			totalFinished = 0;
		}
		totalFinished++;
		this.requestsFinished.put(request.getUserID(), totalFinished);
	}
	
	public void reportMachineUtilization(long machineID, double machineEnd){
		Triple machineData = this.machineUtilization.get(machineID);
		if(machineData == null){
			throw new RuntimeException("Could not rerpor utilization for inexistent machine: "+machineID);
		}
		machineData.secondValue = machineEnd;
		this.machineUtilization.put(machineID, machineData);
	}
	
	public int getRequestsFinished(String userID){
		Integer totalFinished = this.requestsFinished.get(userID);
		return (totalFinished != null) ? totalFinished : 0;
	}
	
	public double getMachineUtilization(long machineID){
		Triple machineData = this.machineUtilization.get(machineID);
		if(machineData != null){
			return ((Double)machineData.secondValue - (Double)machineData.firstValue);
		}else{
			return 0d;
		}
	}
}
