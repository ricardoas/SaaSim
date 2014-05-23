package saasim.ext.iaas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.iaas.Monitorable;
import saasim.core.iaas.MonitoringService;

import com.google.inject.Inject;

public class PeriodicMonitoringService implements MonitoringService {
	
	private long timeBetweenReports;
	private EventScheduler scheduler;
	private List<Monitorable> monitorableObjects;
	
	protected Map<String, SummaryStatistics> metrics;

	@Inject
	public PeriodicMonitoringService(Configuration globalConf, EventScheduler scheduler) {
		this.scheduler = scheduler;
		this.monitorableObjects = new ArrayList<>();
		this.timeBetweenReports = globalConf.getLong(MONITORING_SERVICE_TIMEBETWEENREPORTS);
		this.metrics = new TreeMap<>();
		
		scheduler.queueEvent(new Event(scheduler.now()+timeBetweenReports){
			@Override
			public void trigger() {
				collect();
			}
		});
	}
	
	protected void collect() {
		
		if(!metrics.containsKey("TIME")){
			metrics.put("TIME", new SummaryStatistics());
		}
		metrics.get("TIME").addValue(scheduler.now());

		for (Monitorable monitorable : monitorableObjects) {
			Map<String, Double> collect = monitorable.collect(scheduler.now(), timeBetweenReports);
			for (Entry<String, Double> sample : collect.entrySet()) {
				if(!metrics.containsKey(sample.getKey())){
					metrics.put(sample.getKey(), new SummaryStatistics());
				}
				metrics.get(sample.getKey()).addValue(sample.getValue());
			}
		}
		
		scheduler.queueEvent(new Event(scheduler.now()+timeBetweenReports){
			@Override
			public void trigger() {
				collect();
			}
		});
	}

	@Override
	public Map<String, SummaryStatistics> getStatistics() {
		Map<String, SummaryStatistics> result = metrics;
		metrics = new TreeMap<>();
		return result;
	}

	@Override
	public void setMonitorable(Monitorable monitorable) {
		monitorableObjects.add(monitorable);
	}

}
