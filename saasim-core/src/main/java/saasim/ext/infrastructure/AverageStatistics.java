package saasim.ext.infrastructure;

import java.util.Arrays;

import saasim.core.infrastructure.Statistics;


/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class AverageStatistics implements Statistics{
	
	
	private int accepted;
	private int rejected;
	private int finished;
	private int failed;
	private double averageResponseTime;
	private int[] rejectedAtLoadBalancer;

	public AverageStatistics(int accepted, int rejected, int finished,
			int failed, double averageResponseTime, int[] rejectedAtLoadBalancer) {
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
		sb.append("----- statistics start -----");
		sb.append('\n');
		sb.append("acc=" + accepted);
		sb.append('\n');
		sb.append("rej=" + rejected);
		sb.append('\n');
		sb.append("fin=" + finished);
		sb.append('\n');
		sb.append("fail=" + failed);
		sb.append('\n');
		sb.append("art=" + averageResponseTime);
		sb.append('\n');
		sb.append("rej_LB=" + Arrays.toString(rejectedAtLoadBalancer));
		sb.append('\n');
		sb.append("------ statistics end ------");
		return sb.toString();
	}
}
