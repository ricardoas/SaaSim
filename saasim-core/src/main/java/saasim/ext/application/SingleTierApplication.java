package saasim.ext.application;

import saasim.core.application.Application;
import saasim.core.application.Tier;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.AdmissionControl;
import saasim.core.infrastructure.MonitoringService;

import com.google.inject.Inject;

/**
 * Single tier {@link Application}.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SingleTierApplication extends TieredApplication {

	/**
	 * Default constructor.
	 * @param scheduler {@link EventScheduler} instance
	 * @param control {@link AdmissionControl}
	 * @param monitor {@link MonitoringService}
	 * @param tier {@link Tier}
	 */
	@Inject
	public SingleTierApplication(EventScheduler scheduler, AdmissionControl control, MonitoringService monitor,
			Tier tier) {
		super(scheduler, control, monitor, tier);
	}

}
