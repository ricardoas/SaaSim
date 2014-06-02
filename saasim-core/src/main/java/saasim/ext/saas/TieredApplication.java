package saasim.ext.saas;

import java.util.Map;
import java.util.TreeMap;

import saasim.core.config.Configuration;
import saasim.core.event.EventScheduler;
import saasim.core.iaas.Monitorable;
import saasim.core.infrastructure.AdmissionControl;
import saasim.core.saas.ASP;
import saasim.core.saas.Application;
import saasim.core.saas.Request;
import saasim.core.saas.Response;
import saasim.core.saas.ResponseListener;
import saasim.core.saas.Tier;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Tiered application. It queues incoming requests according to {@link AdmissionControl} policy.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TieredApplication implements Application, Monitorable, ResponseListener {
	
	private static int idSeed = 0;
	
	private final AdmissionControl control;
	private final Tier[] tiers;
	private final EventScheduler scheduler;
	private final int id;
	private final ASP asp;
	private long [] arrival_counter, rejection_counter, failure_counter, finish_counter, response_time;


	/**
	 * Default constructor.
	 * @param scheduler {@link EventScheduler}
	 * @param control {@link AdmissionControl}
	 * @param tiers instances of {@link Tier}.
	 */
	@Inject
	public TieredApplication(Configuration globalConf, EventScheduler scheduler, AdmissionControl control, Provider<Tier> tierFactory, ASP asp) {
		this.scheduler = scheduler;
		this.control = control;
		this.asp = asp;
		this.tiers = assembleTiers(globalConf, tierFactory);
		
		resetStatistics();
		
		this.id = idSeed++;
	}

	private void resetStatistics() {
		this.arrival_counter = new long[this.tiers.length];
		this.rejection_counter = new long[this.tiers.length];
		this.failure_counter = new long[this.tiers.length];
		this.finish_counter = new long[this.tiers.length];
		this.response_time = new long[this.tiers.length];
	}

	private Tier[] assembleTiers(Configuration globalConf, Provider<Tier> tierFactory) {
		Tier[] tiers = new Tier[globalConf.getInt(APPLICATION_TIER_NUMBER)];
		for (int i = 0; i < tiers.length; i++) {
			tiers[i] = tierFactory.get();
			tiers[i].setID(i);
		}
		return tiers;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.saas.Application#queue(saasim.core.saas.Request)
	 */
	@Override
	public void queue(Request request) {
		
		if(request.getCurrentTier() == tiers.length){ // reached last tier
			request.getResponseListener().processDone(request, new Response() {});
		}else{
			arrival_counter[request.getCurrentTier()]++;
			request.pushArrival(scheduler.now());
			
			if(control.canAccept(request)){
				request.setResponseListener(this);
				this.tiers[request.getCurrentTier()].queue(request);
			}else{
				rejection_counter[request.getCurrentTier()]++;
				asp.failed(request);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.saas.Application#configure(saasim.core.config.Configuration)
	 */
	@Override
	public void configure(Configuration configuration) {
		if(Configuration.ACTION_ADMISSION_CONTROL.equals(configuration.getProperty(Configuration.ACTION))){
			control.updatePolicy(configuration);
		}else {
			tiers[configuration.getInt(Configuration.TIER_ID)].configure(configuration);
		}
		
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.saas.ResponseListener#processDone(saasim.core.saas.Request, saasim.core.saas.Response)
	 */
	@Override
	public void processDone(Request request, Response response) {
		if(response == null){
			failure_counter[request.getCurrentTier()]++;
			if(request.getCurrentTier() == 0){
				asp.failed(request);
			}else{
				request.getResponseListener().processDone(request, response);
			}
		}else{
			finish_counter[request.getCurrentTier()]++;
			response_time[request.getCurrentTier()] += (scheduler.now() - request.popArrival());
			
			if(request.getCurrentTier() == 0){
				request.setFinishTime(scheduler.now());
				asp.finished(request);
			}else{
				request.getResponseListener().processDone(request, response);
			}
		}
	}
	
	@Override
	public Map<String, Double> collect(long now, long elapsedTime) {
		Map<String, Double> info = new TreeMap<>();
		
		for (int i = 0; i < arrival_counter.length; i++) {
			info.put("arrival_" + i, 1.0*arrival_counter[i]);
			info.put("rejection_" + i, 1.0*rejection_counter[i]);
			info.put("failure_" + i, 1.0*failure_counter[i]);
			info.put("finish_" + i, 1.0*finish_counter[i]);
			info.put("rt_" + i, finish_counter[i] == 0?0:1.0*response_time[i]/finish_counter[i]);
		}
		
		resetStatistics();
		return info;
	}

	@Override
	public int getID() {
		return id;
	}
}
