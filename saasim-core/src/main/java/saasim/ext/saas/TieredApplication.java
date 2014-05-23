package saasim.ext.saas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import saasim.core.config.Configuration;
import saasim.core.event.EventScheduler;
import saasim.core.iaas.Monitorable;
import saasim.core.iaas.MonitoringService;
import saasim.core.infrastructure.AdmissionControl;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Machine;
import saasim.core.infrastructure.MachineFactory;
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
	
	
	private final AdmissionControl control;
	private final Tier[] tiers;
	private final EventScheduler scheduler;
	private final int id;
	
	private int arrived;
	private int rejected;
	private int finished;
	private int failed;
	
	private int [] arrival_counter, rejection_counter, failure_counter, finish_counter;

	private MachineFactory machineFactory;
	
	private static int idSeed = 0;

	/**
	 * Default constructor.
	 * 
	 * @param scheduler {@link EventScheduler}
	 * @param control {@link AdmissionControl}
	 * @param monitor {@link MonitoringService}
	 * @param tiers instances of {@link Tier}.
	 */
	@Inject
	public TieredApplication(Configuration globalConf, EventScheduler scheduler, AdmissionControl control, MonitoringService monitor, MachineFactory machineFactory, Provider<Tier> tierFactory) {
		this.scheduler = scheduler;
		this.control = control;
		this.machineFactory = machineFactory;
		this.tiers = assembleTiers(globalConf, tierFactory);
		
		resetStatistics();
		
		this.id = idSeed++;
		monitor.setMonitorable(this);
	}

	private void resetStatistics() {
		this.arrival_counter = new int[this.tiers.length];
		this.rejection_counter = new int[this.tiers.length];
		this.failure_counter = new int[this.tiers.length];
		this.finish_counter = new int[this.tiers.length];
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
			
			if(control.canAccept(request)){
				request.setResponseListener(this);
				this.tiers[request.getCurrentTier()].queue(request);
			}else{
				rejection_counter[request.getCurrentTier()]++;
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
			if(Configuration.ACTION_INCREASE.equals(configuration.getProperty(Configuration.ACTION))){
				Machine machine = machineFactory.create((InstanceDescriptor) configuration.getProperty(Configuration.INSTANCE_DESCRIPTOR));
				configuration.setProperty(Configuration.MACHINE, machine);
			}
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
		}else{
			finish_counter[request.getCurrentTier()]++;
		}
		
		if(request.getCurrentTier() != 0){
			request.setFinishTime(scheduler.now());
			request.getResponseListener().processDone(request, response);
		}
	}
	
	@Override
	public Map<String, Double> collect(long now, long elapsedTime) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < arrival_counter.length; i++) {
			list.add(arrival_counter[i]);
			list.add(failure_counter[i]);
			list.add(rejection_counter[i]);
			list.add(finish_counter[i]);
		}
		System.out.println(list.toString().substring(1, list.toString().length()-1));
		
		Map<String, Double> info = new TreeMap<>();
		
		info.put(getID() + "_arrivalrate", (double) arrived);
		info.put(getID() + "_failurerate", (double) failed);
		info.put(getID() + "_droprate", (double) rejected);
		info.put(getID() + "_finishrate", (double) finished);
		
		arrived = 0;
		failed = 0;
		rejected = 0;
		finished = 0;
		
		resetStatistics();
		
		return info;
	}

	@Override
	public int getID() {
		return id;
	}
}
