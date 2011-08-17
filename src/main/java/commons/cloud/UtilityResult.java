package commons.cloud;

public class UtilityResult {
	
	public static class UtilityResultEntry{
		public double receipt;
		public double cost;
		public double penalty;
		public long totalInTransferred;
		public long totalOutTransferred;
	}

	private String result;
	private UtilityResultEntry entry;
	
	public UtilityResult(double receipt, double cost, double penalties, long totalInTransferred,
			long totalOutTransferred) {
		entry = new UtilityResultEntry();
		entry.receipt = receipt;
		entry.cost = cost;
		entry.penalty = penalties;
		entry.totalInTransferred = totalInTransferred;
		entry.totalOutTransferred = totalOutTransferred;
	}

	public String getResult() {
		return result;
	}

	
	public double getUtility() {
		return (entry.receipt - entry.cost - entry.penalty);
	}
	
	public double getCost(){
		return entry.cost;
	}
	
	public double getPenalty(){
		return entry.penalty;
	}
	
	public double getReceipt(){
		return entry.receipt;
	}
	
	public long getTotalInTransferred(){
		return entry.totalInTransferred;
	}
	
	public long getTotalOutTransferred(){
		return entry.totalOutTransferred;
	}

	
	public void addCost(double uniqueCost) {
		entry.cost += uniqueCost;
	}

	
	public void addReceipt(double uniqueReceipt) {
		entry.receipt += uniqueReceipt;
	}
}
