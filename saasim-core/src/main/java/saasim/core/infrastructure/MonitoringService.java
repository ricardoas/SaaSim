package saasim.core.infrastructure;


/**
 * Application monitor. Interface for reporting information.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface MonitoringService{
	
	void report();

	void subscribe(MonitoringServiceConsumer consumer);

}
