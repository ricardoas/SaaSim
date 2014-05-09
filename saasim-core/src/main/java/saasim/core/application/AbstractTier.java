package saasim.core.application;

import saasim.core.infrastructure.LoadBalancer;

/**
 * Abstract implementation of {@link Tier}. It only defines {@link Request} processing methods.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class AbstractTier implements Tier{

	private final LoadBalancer loadBalancer;
	
	/**
	 * Default constructor
	 * @param loadBalancer {@link LoadBalancer}
	 */
	public AbstractTier(LoadBalancer loadBalancer) {
		this.loadBalancer = loadBalancer;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.application.Tier#queue(saasim.core.application.Request)
	 */
	@Override
	public void queue(Request request) {
		request.setResponseListener(this);
		this.loadBalancer.queue(request);
	}

	@Override
	public LoadBalancer getLoadBalancer() {
		return loadBalancer;
	}
	
	@Override
	public void processDone(Request request, Response response) {
		request.getResponseListener().processDone(request, response);
	}
}