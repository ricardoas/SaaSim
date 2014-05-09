package saasim.core.infrastructure;


/**
 * Application monitor. Interface for reporting information.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface MonitorPublisher{
	
	void report();

	void subscribe(AggregatorListener listener);

}
