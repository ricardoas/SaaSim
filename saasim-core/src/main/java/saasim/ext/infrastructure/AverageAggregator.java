package saasim.ext.infrastructure;

import java.util.ArrayList;
import java.util.List;

import saasim.core.application.Request;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.Aggregator;
import saasim.core.infrastructure.AggregatorListener;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Monitor;
import saasim.core.infrastructure.Statistics;

import com.google.inject.Inject;

public class AverageAggregator implements Aggregator {
	
	private List<Monitor> monitors;
	private long timeBetweenReports;
	private List<AggregatorListener> listeners;

	@Inject
	public AverageAggregator(Configuration configuration, EventScheduler scheduler) {
		monitors = new ArrayList<>();
		listeners = new ArrayList<>();
		
		timeBetweenReports = configuration.getLong("monitor.monitor.timebetweenreports");
		
		scheduler.queueEvent(new Event(scheduler.now()+timeBetweenReports){
			@Override
			public void trigger() {
				report();
			}
		});
	}
	
	@Override
	public void registerMonitor(Monitor monitor) {
		monitors.add(monitor);
	}
	
	@Override
	public void registerInterestedInStatistics(AggregatorListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void report() {
		List<Statistics> statistics = new ArrayList<>();
		for (Monitor monitor : monitors) {
			List<Statistics> samples = monitor.collectSamples();
			statistics.add(aggregate(samples));
		}
		
		for (AggregatorListener listener : listeners) {
			listener.report(aggregate(statistics));
		}
	}

	private Statistics aggregate(List<Statistics> samples) {
		return null;
	}

	@Override
	public void requestFinished(Request requestFinished) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendStatistics(Statistics statistics) {
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

	@Override
	public void requestFailed(Request request) {
		// TODO Auto-generated method stub
		
	}




}
