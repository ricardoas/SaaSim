package commons.cloud;

public class UtilityResult {
	
	public static class UtilityResultEntry{
		public double receipt;
		public double cost;
		public double penalty;
		public double utility;
	}

	private String result;
	private UtilityResultEntry entry;
	
	public UtilityResult(double receipt, double cost, double penalties) {
		entry = new UtilityResultEntry();
		entry.receipt = receipt;
		entry.cost = cost;
		entry.penalty = penalties;
		entry.utility = (receipt - cost - penalties);
	}

	public String getResult() {
		return result;
	}

}
