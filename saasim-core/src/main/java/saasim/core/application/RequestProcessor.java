package saasim.core.application;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface RequestProcessor extends ResponseListener{
	
	/**
	 * @return the {@link ResponseListener} which this processor should send response
	 */
	ResponseListener getResponseListener();

}
