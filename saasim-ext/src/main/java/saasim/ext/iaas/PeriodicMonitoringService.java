package saasim.ext.iaas;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
	private Set<Monitorable> monitorableObjects;
	private Set<MonitoringService> children;
	
	protected Map<String, SummaryStatistics> metrics;


	@Inject
	public PeriodicMonitoringService(Configuration globalConf, EventScheduler scheduler) {
		this.scheduler = scheduler;
		this.timeBetweenReports = globalConf.getLong(MONITORING_SERVICE_TIMEBETWEENREPORTS);
		
		this.monitorableObjects = new HashSet<>();
		this.children = new HashSet<>();
		this.metrics = new TreeMap<>();
		
		scheduler.queueEvent(new Event(scheduler.now()+timeBetweenReports){
			@Override
			public void trigger() {
				gatherMetrics();
			}
		});
	}
	
	@Override
	public Map<String, SummaryStatistics> getStatistics() {
		return metrics;
	}
	
	@Override
	public void register(Monitorable monitorable) {
		monitorableObjects.add(monitorable);
	}

	@Override
	public void unregister(Monitorable monitorable) {
		monitorableObjects.remove(monitorable);
	}

	@Override
	public void addChildMonitoringService(MonitoringService service) {
		children.add(service);
	}

	protected void gatherMetrics() {
		
		metrics = new TreeMap<>();
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
		
		for (MonitoringService child : children) {
			metrics.putAll(child.getStatistics());
		}
		
		scheduler.queueEvent(new Event(scheduler.now()+timeBetweenReports){
			@Override
			public void trigger() {
				gatherMetrics();
			}
		});
	}

}
