package saasim.core.saas;

import saasim.core.config.Configuration;


/**
 * Application abstraction.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Application {
	
	public static final String APPLICATION_TIER_VMTYPE = "application.tier.vmtype";
	public static final String APPLICATION_TIER_INIT = "application.tier.init";
	public static final String APPLICATION_TIER_NUMBER = "application.tier.number";
	
	/**
	 * Reconfigure this {@link Application}
	 * @param configuration new {@link Configuration}
	 */
	void configure(Configuration configuration);

	/**
	 * Queue {@link Request} at this {@link Application}.
	 * 
	 * @param request new {@link Request}
	 */
	void queue(Request request);
	
	int getID();
}
