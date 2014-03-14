package saasim.planning.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import saasim.cloud.MachineType;


/**
 * Machine to encapsulate the usage data for machines in the application.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public class MachineUsageData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4018423239141288363L;
	private Map<MachineType, Map<Long, Double>> machineUsagePerType;
	
	/**
	 * Default constructor.
	 */
	public MachineUsageData() {
		this.machineUsagePerType = new HashMap<MachineType, Map<Long,Double>>();
	}

	/**
	 * Another constructor.
	 * @param machineUsagePerType 
	 */
	public MachineUsageData(Map<MachineType, Map<Long, Double>> machineUsagePerType) {
		this.machineUsagePerType = machineUsagePerType;
	}

	/**
	 * Gets the representation of machine usage data.
	 * @return The representation of machine usage data.
	 */
	public Map<MachineType, Map<Long, Double>> getMachineUsagePerType() {
		return machineUsagePerType;
	}

	/**
	 * Sets the representation of machine usage data.
	 * @param machineUsagePerType The new representation of machine usage data.
	 */
	public void setMachineUsagePerType(Map<MachineType, Map<Long, Double>> machineUsagePerType) {
		this.machineUsagePerType = machineUsagePerType;
	}
	
	/**
	 * Add a new usage for this {@link MachineUsageData}.
	 * @param type the {@link MachineType}
	 * @param machineID the id of machine
	 * @param timeInMillis the actual time in milliseconds
	 */
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
