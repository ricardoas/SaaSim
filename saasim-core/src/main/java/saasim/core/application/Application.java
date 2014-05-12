package saasim.core.application;

import saasim.core.provisioning.TierConfiguration;

/**
 * Application abstraction.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Application extends ResponseListener{
	
	/**
	 * TODO what about admission control configuration?
	 * @param tierConfiguration A configuration to be executed.
	 */
	void configure(TierConfiguration tierConfiguration);

	/**
	 * Queue {@link Request} at this {@link Application}.
	 * @param request new {@link Request}
	 */
	void queue(Request request);
	
	/**
	 * @return the number of {@link Tier}s.
	 */
	int getNumberOfTiers();
}
