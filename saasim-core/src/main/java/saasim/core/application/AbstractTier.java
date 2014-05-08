package saasim.core.application;

import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.AdmissionControl;

/**
 * Abstract implementation of {@link Tier}. It only defines {@link Request} processing methods.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class AbstractTier implements Tier{

	protected AdmissionControl admissionControl;
	
	/**
	 * Default constructor
	 * @param scheduler {@link EventScheduler}
	 * @param admissionControl {@link AdmissionControl}.
	 * @param loadBalancer
	 */
	public AbstractTier(AdmissionControl admissionControl) {
		this.admissionControl = admissionControl;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.application.Tier#queue(saasim.core.application.Request)
	 */
	@Override
	public void queue(Request request) {
		this.admissionControl.queue(request);
	}
}