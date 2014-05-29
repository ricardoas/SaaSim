package saasim.core.iaas;


public interface BillingInfo {

	void reset();

	void account(long time, String market, String type, String id, long uptime, double fee);
}
