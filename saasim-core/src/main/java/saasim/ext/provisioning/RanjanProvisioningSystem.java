package saasim.ext.provisioning;

import java.util.ArrayList;
import java.util.List;

import saasim.core.application.Application;
import saasim.core.cloud.utility.UtilityFunction;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.iaas.Provider;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Statistics;
import saasim.core.provisioning.ApplicationConfiguration;
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
		private int tierID;
		private String vmType;
		private List<InstanceDescriptor> vmPool;

		public RanjanReconfigurablePool(Provider provider, Application application, int tierID,
				String vmType, List<InstanceDescriptor> vmPool,
				double targetUtilisation) {
					this.application = application;
					this.tierID = tierID;
					this.vmType = vmType;
					this.vmPool = vmPool;
		}

		public void reconfigure() {
			
			int delta = evaluateNumberOfServersForNextInterval();
			
			if(delta < 0){
				for (int i = 0; i < -delta; i++) {
					InstanceDescriptor instance = vmPool.remove(0);
					instance.turnOff(scheduler.now());
					Configuration config = new Configuration();
					config.setProperty(ApplicationConfiguration.TIER_ID, tierID);
					config.setProperty(ApplicationConfiguration.ACTION, ApplicationConfiguration.ACTION_DECREASE);
					config.setProperty(ApplicationConfiguration.INSTANCE_DESCRIPTOR, instance);
					config.setProperty(ApplicationConfiguration.FORCE, true);
					application.configure(config);
				}
			}else{
				for (int i = 0; i < delta; i++) {
					if(provider.canAcquire(vmType)){
						InstanceDescriptor instance = provider.acquire(vmType);
						Configuration config = new Configuration();
						config.setProperty(ApplicationConfiguration.TIER_ID, tierID);
						config.setProperty(ApplicationConfiguration.ACTION, ApplicationConfiguration.ACTION_INCREASE);
						config.setProperty(ApplicationConfiguration.INSTANCE_DESCRIPTOR, instance);
						config.setProperty(ApplicationConfiguration.FORCE, true);
						application.configure(config);
						vmPool.add(instance);
					}
				}
			}
		}
		
		/**
		 * Decides how many machines are needed to buy (release) according to collected statistics.
		 * 
		 * @param statistics {@link Statistics}
		 * @return The number of machines to buy, if positive, or to release, otherwise. 
		 */
		private int evaluateNumberOfServersForNextInterval() {
			
			
			
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

	@Inject
	public RanjanProvisioningSystem(EventScheduler scheduler, Configuration globalConf, Provider provider) {
		this.scheduler = scheduler;
		this.configuration = globalConf;
		this.provider = provider;
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
		for (RanjanReconfigurablePool element : pool) {
			element.reconfigure();
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
			for (int i = 0; i < startNumberOfReplicas.length; i++) {
				List<InstanceDescriptor> vmPool = new ArrayList<>();
				for (int j = 0; j < Integer.valueOf(startNumberOfReplicas[i]); j++) {
					InstanceDescriptor instance = provider.acquire(vmTypePerTier[i]);
					Configuration config = new Configuration();
					config.setProperty(ApplicationConfiguration.TIER_ID, i);
					config.setProperty(ApplicationConfiguration.ACTION, ApplicationConfiguration.ACTION_INCREASE);
					config.setProperty(ApplicationConfiguration.INSTANCE_DESCRIPTOR, instance);
					config.setProperty(ApplicationConfiguration.FORCE, true);
					application.configure(config);
					vmPool.add(instance);
				}
				pool.add(new RanjanReconfigurablePool(provider, application, i, vmTypePerTier[i], vmPool, targetUtilisation));
			}
		}
	}

	@Override
	public UtilityFunction calculateUtility() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
