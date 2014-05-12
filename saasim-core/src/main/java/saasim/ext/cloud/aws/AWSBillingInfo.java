package saasim.ext.cloud.aws;

import saasim.core.cloud.IaaSBillingInfo;
import saasim.core.infrastructure.InstanceDescriptor;

public class AWSBillingInfo implements IaaSBillingInfo {
	

	private long ondemandHours;
	private long reservationHours;
	private double setUpFee;
	private double onDemandFee;
	private double reservationFee;
	
	public AWSBillingInfo() {
		reset();
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ondemandHours);
		sb.append(',');
		sb.append(reservationHours);
		sb.append(',');
		sb.append(setUpFee);
		sb.append(',');
		sb.append(onDemandFee);
		sb.append(',');
		sb.append(reservationFee);
		sb.append('\n');
		return sb.toString();
	}


	@Override
	public void reset() {
		ondemandHours = 0;
		reservationHours = 0;
		setUpFee = 0;
		onDemandFee = 0;
		reservationFee = 0;
	}


	@Override
	public void account(InstanceDescriptor descriptor) {
		
	}
}
