package saasim.core.application;

/**
 * Configurable application tier.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Tier{
	
	/**
	 * @param request {@link Request} to process.
	 * @return {@link Response} produced by processing this request.
	 */
	Response process(Request request);
	
}