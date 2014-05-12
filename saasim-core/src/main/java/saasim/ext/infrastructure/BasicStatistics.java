package saasim.ext.infrastructure;

import saasim.core.infrastructure.Statistics;


/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class BasicStatistics implements Statistics{
	
	
	private int arrived;
	private int rejected;
	private int finished;
	private int failed;
	private double averageResponseTime;
	private int[] rejectedAtLoadBalancer;
	private long now;
	private long elapsedTimeInSeconds;

	public BasicStatistics(long now, long elapsedTime, int arrived, int rejected, int finished,
			int failed, double averageResponseTime, int[] rejectedAtLoadBalancer) {
				this.now = now;
				this.elapsedTimeInSeconds = elapsedTime / 1000;
				this.arrived = arrived;
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
		sb.append(arrived);
		sb.append(',');
		sb.append(1.0*arrived/elapsedTimeInSeconds);
		sb.append(',');
		sb.append(rejected);
		sb.append(',');
		sb.append(1.0*rejected/elapsedTimeInSeconds);
		sb.append(',');
		sb.append(finished);
		sb.append(',');
		sb.append(1.0*finished/elapsedTimeInSeconds);
		sb.append(',');
		sb.append(failed);
		sb.append(',');
		sb.append(1.0*failed/elapsedTimeInSeconds);
		sb.append(',');
		sb.append(averageResponseTime);
		for (int element : rejectedAtLoadBalancer) {
			sb.append(',');
			sb.append(element);
			sb.append(',');
			sb.append(1.0*element/elapsedTimeInSeconds);
		}
		return sb.toString();
	}
}