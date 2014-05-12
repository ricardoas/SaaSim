package saasim.ext.infrastructure;

import java.util.ArrayList;
import java.util.List;

import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.Monitor;
import saasim.core.infrastructure.MonitoringService;
import saasim.core.infrastructure.MonitoringServiceConsumer;
import saasim.core.infrastructure.Statistics;

import com.google.inject.Inject;

public class PeriodicMonitoringService implements MonitoringService {
	
	private Monitor monitor;
	private List<MonitoringServiceConsumer> listeners;
	private long timeBetweenReports;
	private EventScheduler scheduler;

	@Inject
	public PeriodicMonitoringService(Configuration configuration, EventScheduler scheduler, Monitor monitor) {
		this.scheduler = scheduler;
		this.monitor = monitor;
		listeners = new ArrayList<>();
		
		timeBetweenReports = configuration.getLong(MONITORING_SERVICE_TIMEBETWEENREPORTS);
		
		scheduler.queueEvent(new Event(scheduler.now()+timeBetweenReports){
			@Override
			public void trigger() {
				report();
			}
		});
	}
	
	@Override
	public void subscribe(MonitoringServiceConsumer listener) {
		listeners.add(listener);
	}
	
	@Override
	public void report() {
		Statistics statistics = monitor.collect(scheduler.now(), timeBetweenReports);
		for (MonitoringServiceConsumer listener : listeners) {
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
