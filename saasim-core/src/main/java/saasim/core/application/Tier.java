package saasim.core.application;

import saasim.core.config.Configuration;


/**
 * Configurable application tier.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Tier extends ResponseListener{
	
	/**
	 * @param request {@link Request} to process.
	 */
	void queue(Request request);

	/**
	 * @param configuration 
	 */
	void configure(Configuration configuration);
	
}