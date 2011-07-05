package commons.cloud;

import java.util.ArrayList;
import java.util.List;

public class Provider {

	public List<Resource> reservedResources;
	public List<Resource> onDemandResources;
	
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
	public final String name;

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
		
		this.onDemandResources = new ArrayList<Resource>();
		this.reservedResources = new ArrayList<Resource>();
	}

	public void contractResource(long startTime, long endTime) {
		// TODO
	}

	public double calculateCost(double consumedCpu, double consumedTransference) {
		return this.calculateReservationCosts() + this.calculateOnDemandCosts() + this.calculateTransferenceCosts(consumedTransference);
	}
	
	//TODO
	private double calculateTransferenceCosts(double consumedTransference) {
		return 0;
	}

	private double calculateOnDemandCosts() {
		double totalConsumed = 0;
		for(Resource resource : this.onDemandResources){
			totalConsumed += Math.ceil(resource.calcExecutionTime());
		}
		
		return totalConsumed * this.onDemandCpuCost + totalConsumed * monitoringCost;
	}

	private double calculateReservationCosts() {
		double totalConsumed = 0;
		for(Resource resource : this.reservedResources){
			totalConsumed += Math.ceil(resource.calcExecutionTime());
		}
		
		return this.reservedResources.size() * this.reservationOneYearFee + 
		totalConsumed * this.reservedCpuCost + totalConsumed * monitoringCost;
	}
}
