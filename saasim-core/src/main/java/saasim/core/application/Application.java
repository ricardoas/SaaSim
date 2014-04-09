package saasim.core.application;

import saasim.core.event.EventHandler;
import saasim.core.provisioning.TierConfiguration;

/**
 * Application abstraction.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Application extends EventHandler{
	
	void config(TierConfiguration... tierConfiguration);

	void process(Request request, ResponseListener callback);
	
	int getNumberOfTiers();
}
