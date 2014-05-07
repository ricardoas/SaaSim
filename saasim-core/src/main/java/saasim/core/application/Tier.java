package saasim.core.application;

import saasim.core.provisioning.TierConfiguration;

/**
 * Configurable application tier.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Tier {
	
	/**
	 * @param request {@link Request} to process.
	 */
	void queue(Request request);

	/**
	 * @param tierConfiguration new {@link TierConfiguration}.
	 */
	void config(TierConfiguration tierConfiguration);
	
}