package provisioning;

import commons.cloud.Request;
import commons.sim.jeevent.JEEventHandler;

/**
 * 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface Monitor extends JEEventHandler{
	
	void report(Request requestFinished);

	/**
	 * Configurable system.
	 * 
	 * @param configurable
	 */
	void setConfigurable(DynamicallyConfigurable configurable);

}
