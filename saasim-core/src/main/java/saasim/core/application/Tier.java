package saasim.core.application;


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
	 * 
	 */
	void configure();
	
}