package saasim.ext.infrastructure;

import java.util.ArrayList;
import java.util.List;

import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.MonitorPublisher;
import saasim.core.infrastructure.AggregatorListener;
import saasim.core.infrastructure.Monitor;
import saasim.core.infrastructure.Statistics;

import com.google.inject.Inject;

public class TimedMonitorPublisher implements MonitorPublisher {
	
	private Monitor monitor;
	private List<AggregatorListener> listeners;
	private long timeBetweenReports;
	private EventScheduler scheduler;

	@Inject
	public TimedMonitorPublisher(Configuration configuration, EventScheduler scheduler, Monitor monitor) {
		this.scheduler = scheduler;
		this.monitor = monitor;
		listeners = new ArrayList<>();
		
		timeBetweenReports = configuration.getLong("aggregator.timebetweenreports");
		
		scheduler.queueEvent(new Event(scheduler.now()+timeBetweenReports){
			@Override
			public void trigger() {
				report();
			}
		});
	}
	
	@Override
	public void subscribe(AggregatorListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void report() {
		Statistics statistics = monitor.collect();
		for (AggregatorListener listener : listeners) {
			listener.report(statistics);
		}
		scheduler.queueEvent(new Event(scheduler.now()+timeBetweenReports){
			@Override
			public void trigger() {
				report();
			}
		});
	}

}
