package saasim.core.application;

import saasim.core.provisioning.TierConfiguration;

/**
 * Application abstraction.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Application{
	
	public static String READWORKLOADEVENT = ""; 
	
	void config(TierConfiguration... tierConfiguration);

	/**
	 * Queue {@link Request} at this {@link Application}.
	 * @param request new {@link Request}
	 */
	void queue(Request request);
	
	int getNumberOfTiers();
}
