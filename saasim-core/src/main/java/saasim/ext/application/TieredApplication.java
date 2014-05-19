package saasim.ext.application;

import java.util.Map;
import java.util.TreeMap;

import saasim.core.application.Application;
import saasim.core.application.Request;
import saasim.core.application.Response;
import saasim.core.application.Tier;
import saasim.core.config.Configuration;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.AdmissionControl;
import saasim.core.infrastructure.Monitorable;
import saasim.core.infrastructure.MonitoringService;

/**
 * Tiered application. It queues incoming requests according to {@link AdmissionControl} policy.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class TieredApplication implements Application, Monitorable {
	
	private AdmissionControl control;
	private Tier[] tiers;
	private EventScheduler scheduler;
	
	private int arrived;
	private int rejected;
	private int finished;
	private int failed;

	/**
	 * Default constructor.
	 * 
	 * @param scheduler {@link EventScheduler}
	 * @param control {@link AdmissionControl}
	 * @param monitor {@link MonitoringService}
	 * @param tiers instances of {@link Tier}.
	 */
	public TieredApplication(EventScheduler scheduler, AdmissionControl control, MonitoringService monitor, Tier... tiers) {
		this.scheduler = scheduler;
		this.control = control;
		this.tiers = tiers;
		
		monitor.setMonitorable(this);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.application.Application#queue(saasim.core.application.Request)
	 */
	@Override
	public void queue(Request request) {
		arrived++;
		if(control.canAccept(request)){
			request.setResponseListener(this);
			this.tiers[0].queue(request);
		}else{
			rejected++;
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
		if(response == null){
			failed++;
		}else{
			request.setFinishTime(scheduler.now());
			finished++;
		}
	}
	
	@Override
	public Map<String, Double> collect(long now, long elapsedTime) {
		Map<String, Double> info = new TreeMap<>();
		
		info.put("arrivalrate", (double) arrived);
		info.put("failurerate", (double) failed);
		info.put("droprate", (double) rejected);
		info.put("finishrate", (double) finished);
		
		arrived = 0;
		failed = 0;
		rejected = 0;
		finished = 0;
		
		return info;
	}
}
