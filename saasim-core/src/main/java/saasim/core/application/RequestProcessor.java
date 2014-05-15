package saasim.core.application;

/**
 * Able to process a request.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface RequestProcessor extends ResponseListener{
	
	/**
	 * @return the {@link ResponseListener} which this processor should this processor respond to.
	 */
	ResponseListener getResponseListener();

}
