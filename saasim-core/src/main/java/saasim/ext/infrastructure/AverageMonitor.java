package saasim.ext.infrastructure;

import saasim.core.application.Request;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Monitor;
import saasim.core.infrastructure.Statistics;

public class AverageMonitor implements Monitor {

	@Override
	public void requestFinished(Request requestFinished) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestQueued(long timeMilliSeconds, Request request, int tier) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendStatistics(long timeMilliSeconds, Statistics statistics,
			int tier) {
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
