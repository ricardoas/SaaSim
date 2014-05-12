package saasim.core.application;


/**
 * Abstract implementation of {@link Tier}. It only defines {@link Request} processing methods.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class AbstractTier implements Tier{

	@Override
	public void processDone(Request request, Response response) {
		request.getResponseListener().processDone(request, response);
	}
}