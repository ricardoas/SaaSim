package saasim.ext.iaas.aws;

import saasim.core.iaas.BillingInfo;

public class AWSBillingInfo implements BillingInfo {
	

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
	public void account(long time, String market, String type, String id, long uptime,
			double fee) {
		sb.append(time);
		sb.append(',');
		sb.append(market);
		sb.append(',');
		sb.append(type);		
		sb.append(',');
		sb.append(id);		
		sb.append(',');
		sb.append(uptime);		
		sb.append(',');
		sb.append(fee);		
		sb.append('\n');
		
		totalFee += fee;
	}
}
