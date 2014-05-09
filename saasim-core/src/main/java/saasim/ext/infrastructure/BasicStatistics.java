package saasim.ext.infrastructure;

import saasim.core.infrastructure.Statistics;


/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class BasicStatistics implements Statistics{
	
	
	private int accepted;
	private int rejected;
	private int finished;
	private int failed;
	private double averageResponseTime;
	private int[] rejectedAtLoadBalancer;
	private long now;
	private long elapsedTimeInSeconds;

	public BasicStatistics(long now, long elapsedTime, int accepted, int rejected, int finished,
			int failed, double averageResponseTime, int[] rejectedAtLoadBalancer) {
				this.now = now;
				this.elapsedTimeInSeconds = elapsedTime / 1000;
				this.accepted = accepted;
				this.rejected = rejected;
				this.finished = finished;
				this.failed = failed;
				this.averageResponseTime = averageResponseTime;
				this.rejectedAtLoadBalancer = rejectedAtLoadBalancer;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(now);
		sb.append(',');
		sb.append(accepted);
		sb.append(',');
		sb.append(accepted/elapsedTimeInSeconds);
		sb.append(',');
		sb.append(rejected);
		sb.append(',');
		sb.append(rejected/elapsedTimeInSeconds);
		sb.append(',');
		sb.append(finished);
		sb.append(',');
		sb.append(finished/elapsedTimeInSeconds);
		sb.append(',');
		sb.append(failed);
		sb.append(',');
		sb.append(failed/elapsedTimeInSeconds);
		sb.append(',');
		sb.append(averageResponseTime);
		for (int element : rejectedAtLoadBalancer) {
			sb.append(',');
			sb.append(element);
		}
		return sb.toString();
	}
}
