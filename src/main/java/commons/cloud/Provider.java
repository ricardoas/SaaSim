package commons.cloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import commons.sim.components.MachineDescriptor;
import commons.util.Triple;

public class Provider {
	
	private static int machineIDGenerator = 0;
	private int currentOnDemandMachines;
	private int currentReservedMachines;


	public final String name;
	public final double onDemandCpuCost;// in $/instance-hour
	public final int onDemandLimit;// in number of instances
	public final int reservationLimit;// in number of instances
	public final double reservedCpuCost;// in $/instance-hour
	public final double reservationOneYearFee;// in $
	public final double reservationThreeYearsFee;// in $
	public final double monitoringCost;// in $
	private final long[] transferInLimits;
	private final double[] transferInCosts;
	private final long[] transferOutLimits;
	private final double[] transferOutCosts;
	
	private Map<Long, MachineDescriptor> runningMachines;
	private List<MachineDescriptor> finishedMachines;

	public Provider(String name, double cpuCost, int onDemandLimit,
			int reservationLimit, double reservedCpuCost, double reservationOneYearFee,
			double reservationThreeYearsFee, double monitoringCost,
			long[] transferInLimits, double[] transferInCosts,
			long[] transferOutLimits, double[] transferOutCosts) {
		this.name = name;
		this.onDemandCpuCost = cpuCost;
		this.onDemandLimit = onDemandLimit;
		this.reservationLimit = reservationLimit;
		this.reservedCpuCost = reservedCpuCost;
		this.reservationOneYearFee = reservationOneYearFee;
		this.reservationThreeYearsFee = reservationThreeYearsFee;
		this.monitoringCost = monitoringCost;
		this.transferInLimits = transferInLimits;
		this.transferInCosts = transferInCosts;
		this.transferOutLimits = transferOutLimits;
		this.transferOutCosts = transferOutCosts;
		
		this.currentOnDemandMachines = 0;
		this.currentReservedMachines = 0;
		
		this.runningMachines = new HashMap<Long, MachineDescriptor>();
		this.finishedMachines = new ArrayList<MachineDescriptor>();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the onDemandCpuCost
	 */
	public double getOnDemandCpuCost() {
		return onDemandCpuCost;
	}

	/**
	 * @return the onDemandLimit
	 */
	public int getOnDemandLimit() {
		return onDemandLimit;
	}

	/**
	 * @return the reservationLimit
	 */
	public int getReservationLimit() {
		return reservationLimit;
	}

	/**
	 * @return the reservedCpuCost
	 */
	public double getReservedCpuCost() {
		return reservedCpuCost;
	}

	/**
	 * @return the reservationOneYearFee
	 */
	public double getReservationOneYearFee() {
		return reservationOneYearFee;
	}

	/**
	 * @return the reservationThreeYearsFee
	 */
	public double getReservationThreeYearsFee() {
		return reservationThreeYearsFee;
	}

	/**
	 * @return the monitoringCost
	 */
	public double getMonitoringCost() {
		return monitoringCost;
	}

	/**
	 * @return the transferInLimits
	 */
	public long[] getTransferInLimits() {
		return transferInLimits;
	}

	/**
	 * @return the transferInCosts
	 */
	public double[] getTransferInCosts() {
		return transferInCosts;
	}

	/**
	 * @return the transferOutLimits
	 */
	public long[] getTransferOutLimits() {
		return transferOutLimits;
	}

	/**
	 * @return the transferOutCosts
	 */
	public double[] getTransferOutCosts() {
		return transferOutCosts;
	}

	private double calculateCost(MachineDescriptor descriptor){
		if(descriptor.isReserved()){
			return (descriptor.getFinishTimeInMillis() - descriptor.getStartTimeInMillis()) * reservedCpuCost;
		}else{
			return (descriptor.getFinishTimeInMillis() - descriptor.getStartTimeInMillis()) * onDemandCpuCost;
		}
	}


	public double calculateCost(double consumedTransference) {
		return this.calculateReservationCosts() + this.calculateOnDemandCosts() + this.calculateTransferenceCosts(consumedTransference);
	}
	
	//TODO
	private double calculateTransferenceCosts(double consumedTransference) {
		return 0;
	}

	private double calculateOnDemandCosts() {
		double totalConsumed = this.onDemandConsumption();
		return totalConsumed * this.onDemandCpuCost + totalConsumed * monitoringCost;
	}

	private double calculateReservationCosts() {
		double totalConsumed = this.reservedConsumption();
		return this.reservedResources.size() * this.reservationOneYearFee + 
		totalConsumed * this.reservedCpuCost + totalConsumed * monitoringCost;
	}

	public double onDemandConsumption() {
		double totalConsumed = 0;
		for(Long machineID : this.onDemandResources.keySet()){
			Triple<Long, Long, Double> triple = this.onDemandResources.get(machineID);
			long executionTime = triple.secondValue - triple.firstValue;
			if(executionTime < 0){
				throw new RuntimeException("Invalid cpu usage in machine "+machineID.toString()+" : "+executionTime);
			}
			totalConsumed += Math.ceil(1.0 * executionTime / UtilityFunction.HOUR_IN_MILLIS);
		}
		return totalConsumed;
	}

	public double reservedConsumption() {
		double totalConsumed = 0;
		for(Long machineID : this.reservedResources.keySet()){
			Triple<Long, Long, Double> triple = this.reservedResources.get(machineID);
			
			long executionTime;
			if(triple.secondValue != null){
				executionTime = triple.secondValue - triple.firstValue;
			}else{
				executionTime = 0;
			}
			
			if(executionTime < 0){
				throw new RuntimeException("Invalid cpu usage in machine "+machineID.toString()+" : "+executionTime);
			}
			totalConsumed += Math.ceil(1.0 * executionTime / UtilityFunction.HOUR_IN_MILLIS);
		}
		return totalConsumed;
	}

	public boolean canBuyMachine(boolean reserved) {
		return reserved ? currentReservedMachines < reservationLimit: currentOnDemandMachines < onDemandLimit;
	}
	
	public MachineDescriptor buyMachine(boolean reserved) {
		if(reserved){
			currentReservedMachines++;
		}else{
			currentOnDemandMachines++;
		}
		return new MachineDescriptor(machineIDGenerator++, reserved);
	}

	public boolean shutdownMachine(MachineDescriptor machineDescriptor) {
		if(runningMachines.remove(machineDescriptor.getMachineID()) == null){
			return false;
		}
		finishedMachines.add(machineDescriptor);
		if(machineDescriptor.isReserved()){
			currentReservedMachines--;
		}else{
			currentOnDemandMachines--;
		}
		return true;
	}

	public double calculateCost() {
		double cost = 0;
		long inTransference = 0;
		long outTransference = 0;
		for (MachineDescriptor descriptor : finishedMachines) {
			cost += calculateCost(descriptor);
			inTransference += descriptor.getInTransference();
			outTransference += descriptor.getOutTransference();
		}
		cost += calcTransferenceCost(inTransference, transferInLimits, transferInCosts);
		cost += calcTransferenceCost(outTransference, transferOutLimits, transferOutCosts);
		return cost;
	}
	
	/**
	 * TODO Code me!
	 * @param totalTransfered
	 * @param limits
	 * @param costs
	 * @return
	 */
	private double calcTransferenceCost(long totalTransfered,
			long[] limits, double[] costs) {
		return 0;
	}

	public void resetCostCounters(){
		
	}
	
	
}
