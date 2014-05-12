package saasim.ext.application;

import saasim.core.application.Application;
import saasim.core.application.Request;
import saasim.core.application.Response;
import saasim.core.application.Tier;
import saasim.core.config.Configuration;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.AdmissionControl;
import saasim.core.infrastructure.Monitor;
import saasim.core.provisioning.TierConfiguration;

import com.google.inject.Inject;

/**
 * Multi tier application.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SingleTierApplication implements Application {
	
	private AdmissionControl control;
	private Tier[] tiers;
	private Monitor monitor;
	private EventScheduler scheduler;

	@Inject
	public SingleTierApplication(Configuration configuration, EventScheduler scheduler, AdmissionControl control, Monitor monitor, Tier tier) {
		this.scheduler = scheduler;
		this.control = control;
		this.monitor = monitor;
		this.tiers = new Tier[]{tier};
	}

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

	@Override
	public void configure(TierConfiguration configuration) {
		tiers[configuration.getTierID()].config(configuration);
	}

	@Override
	public int getNumberOfTiers() {
		return tiers.length;
	}

	@Override
	public void processDone(Request request, Response response) {
		request.setFinishTime(scheduler.now());
		monitor.requestFinished(request);
	}
}
