package saasim.ext.infrastructure;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.Monitor;
import saasim.core.infrastructure.MonitoringService;
import saasim.core.infrastructure.Statistics;

import com.google.inject.Inject;

public class PeriodicMonitoringService implements MonitoringService {
	
	private List<Monitor> monitors;
	private long timeBetweenReports;
	private EventScheduler scheduler;
	private Statistics statistics;
	private Logger logger = Logger.getLogger(MonitoringService.class);

	@Inject
	public PeriodicMonitoringService(Configuration globalConf, EventScheduler scheduler, Monitor monitor) {
		this.scheduler = scheduler;
		this.monitors = new ArrayList<>();
		
		timeBetweenReports = globalConf.getLong(MONITORING_SERVICE_TIMEBETWEENREPORTS);
		
		scheduler.queueEvent(new Event(scheduler.now()+timeBetweenReports){
			@Override
			public void trigger() {
				collect();
			}
		});
	}
	
	protected void collect() {
		for (Monitor monitor : monitors) {
			statistics = monitor.collect(scheduler.now(), timeBetweenReports);//FIXME combine them!
			logger.info(statistics);
		}
		
		scheduler.queueEvent(new Event(scheduler.now()+timeBetweenReports){
			@Override
			public void trigger() {
				collect();
			}
		});
	}

	@Override
	public void register(Monitor monitor) {
		monitors.add(monitor);
	}

	@Override
	public Statistics getStatistics() {
		return statistics;
	}

}
