package saasim.core.application;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface ResponseListener {
	
	/**
	 * @param request
	 * @param response
	 */
	void processDone(Request request, Response response);

}
