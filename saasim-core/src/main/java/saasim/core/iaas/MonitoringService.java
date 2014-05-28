package saasim.core.iaas;

import java.util.Map;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;



/**
 * Application monitor. Interface for reporting information.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface MonitoringService{
	
	public static final String MONITORING_SERVICE_TIMEBETWEENREPORTS = "monitoring.service.timebetweenreports";

	Map<String, SummaryStatistics> getStatistics();
	
	void register(Monitorable monitorable);

	void unregister(Monitorable machine);
	
	void addChildMonitoringService(MonitoringService child);

}
