package saasim.core.application;

/**
 * {@link Request} sender. It waits until someone process the {@link Request} and sends a {@link Response} back.
 *  
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface ResponseListener {
	
	/**
	 * {@link Request} processing is done.
	 * @param request a {@link Request}
	 * @param response the {@link Request}'s {@link Response}
	 */
	void processDone(Request request, Response response);

}
