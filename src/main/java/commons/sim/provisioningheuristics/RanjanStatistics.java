package commons.sim.provisioningheuristics;

public class RanjanStatistics {
	
	public double totalUtilizationInLastInterval;
	public long numberOfRequestsArrivalInLastInterval;
	public long numberOfRequestsCompletionsInLastInterval;
	public long totalNumberOfServers;

	/**
	 * @param totalUtilization
	 * @param totalRequestsArrivals
	 * @param totalRequestsCompletions
	 * @param totalNumberOfServers
	 */
	public RanjanStatistics(double totalUtilization, long totalRequestsArrivals, long totalRequestsCompletions, long totalNumberOfServers) {
		this.totalUtilizationInLastInterval = totalUtilization;
		this.numberOfRequestsArrivalInLastInterval = totalRequestsArrivals;
		this.numberOfRequestsCompletionsInLastInterval = totalRequestsCompletions;
		this.totalNumberOfServers = totalNumberOfServers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ (int) (numberOfRequestsArrivalInLastInterval ^ (numberOfRequestsArrivalInLastInterval >>> 32));
		result = prime
				* result
				+ (int) (numberOfRequestsCompletionsInLastInterval ^ (numberOfRequestsCompletionsInLastInterval >>> 32));
		result = prime * result
				+ (int) (totalNumberOfServers ^ (totalNumberOfServers >>> 32));
		long temp;
		temp = Double.doubleToLongBits(totalUtilizationInLastInterval);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RanjanStatistics other = (RanjanStatistics) obj;
		if (numberOfRequestsArrivalInLastInterval != other.numberOfRequestsArrivalInLastInterval)
			return false;
		if (numberOfRequestsCompletionsInLastInterval != other.numberOfRequestsCompletionsInLastInterval)
			return false;
		if (totalNumberOfServers != other.totalNumberOfServers)
			return false;
		if (Double.doubleToLongBits(totalUtilizationInLastInterval) != Double
				.doubleToLongBits(other.totalUtilizationInLastInterval))
			return false;
		return true;
	}
}
