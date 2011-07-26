package provisioning;

import java.util.List;

import commons.cloud.Request;
import commons.sim.jeevent.JEEventHandler;
import commons.util.Triple;

/**
 * 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface Monitor extends JEEventHandler{
	
	void reportRequestFinished(Request requestFinished);

	/**
	 * Configurable system.
	 * 
	 * @param configurable
	 */
	void setConfigurable(DynamicallyConfigurable configurable);

	void reportUtilization(double utilization, List<Triple> arrivalsAndCompletions);
}
