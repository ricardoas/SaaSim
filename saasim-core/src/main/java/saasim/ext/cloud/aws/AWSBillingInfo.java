package saasim.ext.cloud.aws;

import saasim.core.cloud.IaaSBillingInfo;
import saasim.core.infrastructure.InstanceDescriptor;

public class AWSBillingInfo implements IaaSBillingInfo {
	

	private double totalFee;

	private StringBuilder sb = new StringBuilder();
	
	public AWSBillingInfo() {
		reset();
	}
	
	
	@Override
	public String toString() {
		
		sb.append('\n');
		sb.append("TOTAL=");
		sb.append(totalFee);
		sb.append('\n');
		
		return sb.toString();
	}


	@Override
	public void reset() {
		totalFee = 0;
		sb = new StringBuilder();
	}


	@Override
	public void account(InstanceDescriptor descriptor, long now) {
		totalFee += descriptor.reportUsage(sb, now);
	}
}
