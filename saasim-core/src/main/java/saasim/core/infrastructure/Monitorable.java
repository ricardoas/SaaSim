package saasim.core.infrastructure;

import java.util.Map;


/**
 * Application monitor. Interface for reporting information.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Monitorable{
	
	Map<String, Double> collect(long now, long elapsedTime);
}
