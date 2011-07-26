package commons.sim.provisioningheuristics;

public class RanjanStatistics {
	
	public double totalUtilizationInLastInterval;
	public long numberOfRequestsArrivalInLastInterval;
	public long numberOfRequestsCompletionsInLastInterval;
	public long totalNumberOfServers;

	public RanjanStatistics(double totalUtilization, long totalRequestsArrivals, long totalRequestsCompletions, long totalNumberOfServers) {
		this.totalUtilizationInLastInterval = totalUtilization;
		this.numberOfRequestsArrivalInLastInterval = totalRequestsArrivals;
		this.numberOfRequestsCompletionsInLastInterval = totalRequestsCompletions;
		this.totalNumberOfServers = totalNumberOfServers;
	}
}
