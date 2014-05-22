package saasim.core.application;

import saasim.core.config.Configuration;


/**
 * Configurable application tier.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Tier{
	
	/**
	 * It queues a request at this {@link Tier}'s processing queue.
	 * 
	 * @param request {@link Request} to process.
	 */
	void queue(Request request);

	/**
	 * It sends a reconfiguration scheme to this {@link Tier}.
	 * @param configuration new {@link Configuration}
	 */
	void configure(Configuration configuration);
	
	int getID();

	void setID(int id);
	
}