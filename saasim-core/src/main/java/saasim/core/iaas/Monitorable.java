package saasim.core.iaas;

import java.util.Map;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;


/**
 * Application monitor. Interface for reporting information.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Monitorable{
	
	Map<String, Double> collect(long now, long elapsedTime);

	Map<String, SummaryStatistics> new_collect(long now, long elapsedTime);
}
