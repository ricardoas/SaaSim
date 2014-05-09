package saasim.ext.infrastructure;

import saasim.core.application.Request;
import saasim.core.config.Configuration;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Monitor;
import saasim.core.infrastructure.Statistics;

import com.google.inject.Inject;

public class AverageMonitor implements Monitor {
	
	private int arrived;
	private int rejected;
	private int finished;
	private int failed;
	
	private double responseTimeInMillis;
	private int[] rejectedAtLoadBalancer;

	@Inject
	public AverageMonitor(Configuration configuration) {
		System.out.println("AverageMonitor.AverageMonitor()" + this);
		reset(configuration.getInt("application.tier"));
	}

	@Override
	public Statistics collect() {
		AverageStatistics statistics = new AverageStatistics(arrived, rejected, finished, failed, responseTimeInMillis/finished, rejectedAtLoadBalancer);
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
	
	@Override
	public void requestRejectedAtLoadBalancer(Request request, int tier) {
		rejectedAtLoadBalancer[tier]++;
	}

	
	
	
	
	
	@Override
	public void requestAcceptedAtLoadBalancer(Request request, int tier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void machineTurnedOff(InstanceDescriptor machineDescriptor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void chargeUsers(long currentTimeInMillis) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isOptimal() {
		// TODO Auto-generated method stub
		return false;
	}

}
