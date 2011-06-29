package cloud;

import java.util.List;

public class CloudProvider {
	
	public List<CloudResource> resources;
	public final double cpuCost;//in instance-hour
	public final double onDemandLimit;//in number of instances
	public final double reservationLimit;//in number of instances
	public final double reservationOneYearFee;//in $
	public final double reservationThreeYearsFee;//in $
	public final double monitoringCost;//in $
	
	public final String transferInLimits;// This string defines the limits used to establish different costs for transference
	public final String transferInCosts;//Transference costs per range
	public final String transferOutLimits;// This string defines the limits used to establish different costs for transference
	public final String transferOutCosts;//Transference costs per range
	public final String name;
	
	public CloudProvider(String name, double cpuCost, double onDemandLimit, double reservationLimit,
			double reservationOneYearFee, double reservationThreeYearsFee, double monitoringCost, String transferInLimits,
			String transferInCosts, String transferOutLimits, String transferOutCosts) {
			this.name = name;
			this.cpuCost = cpuCost;
			this.onDemandLimit = onDemandLimit;
			this.reservationLimit = reservationLimit;
			this.reservationOneYearFee = reservationOneYearFee;
			this.reservationThreeYearsFee = reservationThreeYearsFee;
			this.monitoringCost = monitoringCost;
			this.transferInLimits = transferInLimits;
			this.transferInCosts = transferInCosts;
			this.transferOutLimits = transferOutLimits;
			this.transferOutCosts = transferOutCosts;
	}

	public void contractResource(long startTime, long endTime){
		//TODO
	}
	
	public void calculateCost(){
		//TODO
	}

}
