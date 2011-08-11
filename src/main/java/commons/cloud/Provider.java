package commons.cloud;

import java.util.HashMap;
import java.util.Map;

import commons.sim.components.MachineDescriptor;
import commons.util.Triple;

public class Provider {

	public final String name;
	public final double onDemandCpuCost;// in $/instance-hour
	public final int onDemandLimit;// in number of instances
	public final int reservationLimit;// in number of instances
	public final double reservedCpuCost;// in $/instance-hour
	public final double reservationOneYearFee;// in $
	public final double reservationThreeYearsFee;// in $
	public final double monitoringCost;// in $

	public final String transferInLimits;// This string defines the limits used
											// to establish different costs for
											// transference
	public final String transferInCosts;// Transference costs per range
	public final String transferOutLimits;// This string defines the limits used
											// to establish different costs for
											// transference
	public final String transferOutCosts;// Transference costs per range

	public Provider(String name, double cpuCost, int onDemandLimit,
			int reservationLimit, double reservedCpuCost, double reservationOneYearFee,
			double reservationThreeYearsFee, double monitoringCost,
			String transferInLimits, String transferInCosts,
			String transferOutLimits, String transferOutCosts) {
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
		
		this.verifyProperties();
	}

	@Deprecated
	private void verifyProperties() {
		if(this.onDemandCpuCost < 0){
			throw new RuntimeException("Invalid provider "+this.name+": onDemandCpuCost "+this.onDemandCpuCost);
		}
		if(this.onDemandLimit <= 0){
			throw new RuntimeException("Invalid provider "+this.name+": onDemandLimit "+this.onDemandLimit);
		}
		if(this.reservationLimit <= 0){
			throw new RuntimeException("Invalid provider "+this.name+": reservationLimit "+this.reservationLimit);
		}
		if(this.reservedCpuCost < 0){
			throw new RuntimeException("Invalid provider "+this.name+": reservedCpuCost "+this.reservedCpuCost);
		}
		if(this.reservationOneYearFee < 0){
			throw new RuntimeException("Invalid provider "+this.name+": reservationOneYearFee "+this.reservationOneYearFee);
		}
		if(this.reservationThreeYearsFee < 0){
			throw new RuntimeException("Invalid provider "+this.name+": reservationThreeYearsFee "+this.reservationThreeYearsFee);
		}
		if(this.monitoringCost < 0){
			throw new RuntimeException("Invalid provider "+this.name+": monitoringCost "+this.monitoringCost);
		}
	}
	
	public double calculateCost(MachineDescriptor descriptor){
		return 0.0;
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
}
