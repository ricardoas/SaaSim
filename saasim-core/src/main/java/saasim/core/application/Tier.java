package saasim.core.application;

import saasim.core.provisioning.TierConfiguration;

/**
 * Configurable application tier.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Tier{
	
	/**
	 * @param request {@link Request} to process.
	 * @param responseListener TODO
	 */
	void process(Request request, ResponseListener responseListener);

	void config(TierConfiguration tierConfiguration);
	
}