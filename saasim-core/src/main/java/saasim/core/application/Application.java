package saasim.core.application;

import saasim.core.config.Configuration;


/**
 * Application abstraction.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Application extends ResponseListener{
	
	public static final String APPLICATION_TIER = "application.tier";
	public static final String APPLICATION_TIER_VMTYPE = "application.tier.vmtype";
	public static final String APPLICATION_TIER_REPLICAS = "application.tier.replicas";

	/**
	 * @param configuration A configuration to be executed.
	 */
	void configure(Configuration configuration);

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
