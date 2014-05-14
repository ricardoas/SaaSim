package saasim.ext.infrastructure;

import saasim.core.application.Application;
import saasim.core.application.Request;
import saasim.core.config.Configuration;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Monitor;
import saasim.core.infrastructure.Statistics;

import com.google.inject.Inject;

public class BasicMonitor implements Monitor {
	
	private int arrived;
	private int rejected;
	private int finished;
	private int failed;
	
	private double responseTimeInMillis;
	private int[] rejectedAtLoadBalancer;

	@Inject
	public BasicMonitor(Configuration globalConf) {
		reset(globalConf.getInt(Application.APPLICATION_TIER));
	}

	@Override
	public Statistics collect(long now, long elapsedTime) {
		Statistics statistics = new BasicStatistics(now, elapsedTime, arrived, rejected, finished, failed, finished == 0? 0.0:responseTimeInMillis/finished, rejectedAtLoadBalancer);
		reset(rejectedAtLoadBalancer.length);
		return statistics;
	}

	
	
	@Override
	public void reset(int numberOfTiers){
		this.arrived = 0;
		this.rejected = 0;
		this.finished = 0;
		this.failed = 0;
		this.responseTimeInMillis = 0.0;
		this.rejectedAtLoadBalancer = new int[numberOfTiers];
	}

	@Override
	public void requestArrived(Request request) {
		arrived++;
	}

	@Override
	public void requestRejected(Request request) {
		rejected++;
	}
	@Override
	public void requestFinished(Request requestFinished) {
		finished++;
		responseTimeInMillis += requestFinished.getResponseTimeInMillis();
	}

	@Override
	public void requestFailedAtMachine(Request request,
			InstanceDescriptor descriptor) {
		failed++;
	}
}
