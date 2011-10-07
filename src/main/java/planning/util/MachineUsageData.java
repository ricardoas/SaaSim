package planning.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import commons.cloud.MachineType;

public class MachineUsageData implements Serializable{

	private Map<MachineType, Map<Long, Double>> machineUsagePerType;
	
	public MachineUsageData() {
		this.machineUsagePerType = new HashMap<MachineType, Map<Long,Double>>();
	}

	public MachineUsageData(Map<MachineType, Map<Long, Double>> machineUsagePerType) {
		this.machineUsagePerType = machineUsagePerType;
	}

	public Map<MachineType, Map<Long, Double>> getMachineUsagePerType() {
		return machineUsagePerType;
	}

	public void setMachineUsagePerType(Map<MachineType, Map<Long, Double>> machineUsagePerType) {
		this.machineUsagePerType = machineUsagePerType;
	}
	
	public void addUsage(MachineType type, long machineID, double timeInMillis){
		Map<Long, Double> machineTypeData = this.machineUsagePerType.get(type);
		if(machineTypeData == null){
			machineTypeData = new HashMap<Long, Double>();
			this.machineUsagePerType.put(type, machineTypeData);
		}
		
		Double timeUsed = machineTypeData.get(machineID);
		if(timeUsed == null){
			timeUsed = 0d;
		}
		timeUsed = timeInMillis;
		machineTypeData.put(machineID, timeUsed);
	}
}
