package saasim.core.application;

import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.AdmissionControl;
import saasim.core.infrastructure.LoadBalancer;

/**
 * Abstract implementation of {@link Tier}. It only defines {@link Request} processing methods.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class AbstractTier implements Tier{

	private AdmissionControl admissionControl;
	protected LoadBalancer loadBalancer;
	
	/**
	 * Default constructor
	 * @param scheduler {@link EventScheduler}
	 * @param admissionControl {@link AdmissionControl}.
	 * @param loadBalancer
	 */
	public AbstractTier(final EventScheduler scheduler, AdmissionControl admissionControl, final LoadBalancer loadBalancer) {
		this.admissionControl = admissionControl;
		this.loadBalancer = loadBalancer;
		
		scheduler.queueEvent(new Event(scheduler.now()) {
			@Override
			public void trigger() {
				AbstractTier.this.admissionControl.process(scheduler.now(), loadBalancer);
			}
		});
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