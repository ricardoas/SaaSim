package saasim.ext.infrastructure;

import java.util.ArrayList;
import java.util.List;

import saasim.core.application.Request;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.Aggregator;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Monitor;
import saasim.core.infrastructure.Statistics;

import com.google.inject.Inject;

public class AverageMonitor implements Monitor {
	
	private long timeBetweenSamples;
	private EventScheduler scheduler;
	private List<Statistics> samples;
	private int finished;
	private int failed;
	private long responseTimeInMillis;

	@Inject
	public AverageMonitor(Configuration configuration, EventScheduler scheduler, Aggregator aggregator) {
		this.scheduler = scheduler;
		this.timeBetweenSamples = configuration.getLong("monitor.timebetweensamples");
		this.samples = new ArrayList<>();
		this.failed = 0;
		this.finished = 0;
		
		aggregator.registerMonitor(this);
		
		scheduler.queueEvent(new Event(scheduler.now()+timeBetweenSamples){
			@Override
			public void trigger() {
				buildSample();
			}
		});
	}

	private void buildSample() {
		
		samples.add(new Statistics() {
		});
		
		scheduler.queueEvent(new Event(scheduler.now()+timeBetweenSamples){
			@Override
			public void trigger() {
				buildSample();
			}
		});
	}

	@Override
	public List<Statistics> collectSamples() {
		List<Statistics> collected = samples;
		samples = new ArrayList<>();
		return collected;
	}

	
	@Override
	public void requestFinished(Request requestFinished) {
		finished++;
		responseTimeInMillis += requestFinished.getResponseTimeInMillis();
	}
	
	@Override
	public void requestFailed(Request request) {
		failed++;
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
