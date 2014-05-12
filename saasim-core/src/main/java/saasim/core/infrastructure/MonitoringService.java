package saasim.core.infrastructure;


/**
 * Application monitor. Interface for reporting information.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface MonitoringService{
	
	public static final String MONITORING_SERVICE_TIMEBETWEENREPORTS = "monitoring.service.timebetweenreports";

	void report();

	void subscribe(MonitoringServiceConsumer consumer);

}
