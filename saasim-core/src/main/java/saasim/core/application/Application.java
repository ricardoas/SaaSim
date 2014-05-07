package saasim.core.application;

import saasim.core.provisioning.TierConfiguration;

/**
 * Application abstraction.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Application{
	
	public static String READWORKLOADEVENT = ""; 
	
	void config(TierConfiguration... tierConfiguration);

	void queue(Request request);
	
	int getNumberOfTiers();
}
