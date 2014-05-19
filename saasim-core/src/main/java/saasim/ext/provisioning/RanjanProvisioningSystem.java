package saasim.ext.provisioning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;

import saasim.core.application.Application;
import saasim.core.cloud.utility.UtilityFunction;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.iaas.Provider;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.MonitoringService;
import saasim.core.provisioning.ProvisioningSystem;

import com.google.inject.Inject;


/**
 * Simple implementation of QuID algorithm as depicted in: 
 * <a href='http://dx.doi.org/10.1109/IWQoS.2002.1006569'>http://dx.doi.org/10.1109/IWQoS.2002.1006569<a>
 * <br>
 * This implementation is not ready to handle the problem of heterogeneous machines.
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public class RanjanProvisioningSystem implements ProvisioningSystem {
	
	private class RanjanReconfigurablePool {

		private Application application;
		private List<InstanceDescriptor> vmPool;
		private int tierID;

		public RanjanReconfigurablePool( Application application, int tierID, List<InstanceDescriptor> vmPool) {
			this.application = application;
			this.tierID = tierID;
			this.vmPool = vmPool;
		}

		public void reconfigure(Map<String, SummaryStatistics> statistics) {
			
			int delta = evaluateNumberOfServersForNextInterval(statistics);
			
			if(delta < 0){
				for (int i = 0; i < -delta; i++) {
					InstanceDescriptor instance = vmPool.remove(0);
					instance.turnOff(scheduler.now());
					Configuration config = new Configuration();
					config.setProperty(Configuration.TIER_ID, tierID);
					config.setProperty(Configuration.ACTION, Configuration.ACTION_DECREASE);
					config.setProperty(Configuration.INSTANCE_DESCRIPTOR, instance);
					config.setProperty(Configuration.FORCE, true);
					application.configure(config);
				}
			}else{
				for (int i = 0; i < delta; i++) {
					if(provider.canAcquire(vmTypePerTier[tierID])){
						InstanceDescriptor instance = provider.acquire(vmTypePerTier[tierID]);
						Configuration config = new Configuration();
						config.setProperty(Configuration.TIER_ID, tierID);
						config.setProperty(Configuration.ACTION, Configuration.ACTION_INCREASE);
						config.setProperty(Configuration.INSTANCE_DESCRIPTOR, instance);
						config.setProperty(Configuration.FORCE, true);
						application.configure(config);
						vmPool.add(instance);
					}
				}
			}
		}
		
		/**
		 * Decides how many machines are needed to buy (release) according to collected statistics.
		 * @param statistics 
		 * @param statistics 
		 * 
		 * @param statistics {@link Statistics}
		 * @return The number of machines to buy, if positive, or to release, otherwise. 
		 */
		private int evaluateNumberOfServersForNextInterval(Map<String, SummaryStatistics> statistics) {
			
			
			return 0;
//			
//			double d = statistics.averageUtilisation / statistics.requestCompletions;
//			
//			double u_lign = Math.max(statistics.requestArrivals, statistics.requestCompletions) * d;
//			int newNumberOfServers = (int) Math.ceil( u_lign * statistics.totalNumberOfActiveServers / targetUtilisation );
//			
//			return Math.max(1, newNumberOfServers) - statistics.totalNumberOfActiveServers;
		}
	}


	private static String RANJAN_TARGET_UTILISATION = "dps.ranjan.target";  

	private Configuration configuration;
	private Provider provider;

	private List<RanjanReconfigurablePool> pool;

	private EventScheduler scheduler;

	private long tick;

	private String[] startNumberOfReplicas;

	private String[] vmTypePerTier;

	private double targetUtilisation;

	private MonitoringService monitoringService;

	@Inject
	public RanjanProvisioningSystem(EventScheduler scheduler, Configuration globalConf, Provider provider, MonitoringService monitoringService) {
		this.scheduler = scheduler;
		this.configuration = globalConf;
		this.provider = provider;
		this.monitoringService = monitoringService;
		this.pool = new ArrayList<RanjanReconfigurablePool>();
		
		this.startNumberOfReplicas = globalConf.getStringArray(Application.APPLICATION_TIER_REPLICAS);
		this.vmTypePerTier = globalConf.getStringArray(Application.APPLICATION_TIER_VMTYPE);
		this.targetUtilisation = this.configuration.getDouble(RANJAN_TARGET_UTILISATION);

		
		scheduler.queueEvent(new Event(tick){
			@Override
			public void trigger() {
				evaluate();
			}
		});
	}

	protected void evaluate() {
		Map<String, SummaryStatistics> statistics = monitoringService.getStatistics();
		for (RanjanReconfigurablePool element : pool) {
			element.reconfigure(statistics);
		}
		scheduler.queueEvent(new Event(scheduler.now() + tick){
			@Override
			public void trigger() {
				evaluate();
			}
		});
	}

	@Override
	public void registerConfigurable(Application... applications) {
		
		for (Application application : applications) {
			for (int tierID = 0; tierID < startNumberOfReplicas.length; tierID++) {
				List<InstanceDescriptor> vmPool = new ArrayList<>();
				for (int j = 0; j < Integer.valueOf(startNumberOfReplicas[tierID]); j++) {
					InstanceDescriptor instance = provider.acquire(vmTypePerTier[tierID]);
					Configuration config = new Configuration();
					config.setProperty(Configuration.TIER_ID, tierID);
					config.setProperty(Configuration.ACTION, Configuration.ACTION_INCREASE);
					config.setProperty(Configuration.INSTANCE_DESCRIPTOR, instance);
					config.setProperty(Configuration.FORCE, true);
					application.configure(config);
					vmPool.add(instance);
				}
				pool.add(new RanjanReconfigurablePool(application, tierID, vmPool));
			}
		}
	}

	@Override
	public UtilityFunction calculateUtility() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
