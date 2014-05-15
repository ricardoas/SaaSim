package saasim.ext.application;

import saasim.core.application.Application;
import saasim.core.application.Request;
import saasim.core.application.Response;
import saasim.core.application.Tier;
import saasim.core.config.Configuration;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.AdmissionControl;
import saasim.core.infrastructure.Monitor;

/**
 * Tiered application. It queues incoming requests according to {@link AdmissionControl} policy.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class TieredApplication implements Application {
	
	private AdmissionControl control;
	private Tier[] tiers;
	private Monitor monitor;
	private EventScheduler scheduler;

	/**
	 * Default constructor.
	 * 
	 * @param scheduler {@link EventScheduler}
	 * @param control {@link AdmissionControl}
	 * @param monitor {@link Monitor}
	 * @param tiers instances of {@link Tier}.
	 */
	public TieredApplication(EventScheduler scheduler, AdmissionControl control, Monitor monitor, Tier... tiers) {
		this.scheduler = scheduler;
		this.control = control;
		this.monitor = monitor;
		this.tiers = tiers;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.application.Application#queue(saasim.core.application.Request)
	 */
	@Override
	public void queue(Request request) {
		monitor.requestArrived(request);
		if(control.canAccept(request)){
			request.setResponseListener(this);
			this.tiers[0].queue(request);
		}else{
			monitor.requestRejected(request);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.application.Application#configure(saasim.core.config.Configuration)
	 */
	@Override
	public void configure(Configuration configuration) {
		if(Configuration.ACTION_ADMISSION_CONTROL.equals(configuration.getProperty(Configuration.ACTION))){
			control.updatePolicy(configuration);
		}
		tiers[configuration.getInt(Configuration.TIER_ID)].configure(configuration);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.application.ResponseListener#processDone(saasim.core.application.Request, saasim.core.application.Response)
	 */
	@Override
	public void processDone(Request request, Response response) {
		request.setFinishTime(scheduler.now());
		monitor.requestFinished(request);
	}
}
