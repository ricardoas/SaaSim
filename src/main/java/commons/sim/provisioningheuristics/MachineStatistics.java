package commons.sim.provisioningheuristics;

public class MachineStatistics {
	
	public double averageUtilisation;
	public long numberOfRequestsArrivalInLastInterval;
	public long numberOfRequestsCompletionsInLastInterval;
	public long totalNumberOfServers;

	/**
	 * @param averageUtilization
	 * @param totalRequestsArrivals
	 * @param totalRequestsCompletions
	 * @param totalNumberOfServers
	 */
	public MachineStatistics(double averageUtilization, long totalRequestsArrivals, long totalRequestsCompletions, long totalNumberOfServers) {
		this.averageUtilisation = averageUtilization;
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
		temp = Double.doubleToLongBits(averageUtilisation);
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
		MachineStatistics other = (MachineStatistics) obj;
		if (numberOfRequestsArrivalInLastInterval != other.numberOfRequestsArrivalInLastInterval)
			return false;
		if (numberOfRequestsCompletionsInLastInterval != other.numberOfRequestsCompletionsInLastInterval)
			return false;
		if (totalNumberOfServers != other.totalNumberOfServers)
			return false;
		if (Double.doubleToLongBits(averageUtilisation) != Double
				.doubleToLongBits(other.averageUtilisation))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "RanjanStatistics [averageUtilisation=" + averageUtilisation
				+ ", numberOfRequestsArrivalInLastInterval="
				+ numberOfRequestsArrivalInLastInterval
				+ ", numberOfRequestsCompletionsInLastInterval="
				+ numberOfRequestsCompletionsInLastInterval
				+ ", totalNumberOfServers=" + totalNumberOfServers + "]";
	}
}
