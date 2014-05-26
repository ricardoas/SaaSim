package saasim.ext.provisioning.ranjan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.log4j.Logger;

import saasim.core.cloud.utility.UtilityFunction;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventPriority;
import saasim.core.event.EventScheduler;
import saasim.core.iaas.Monitorable;
import saasim.core.iaas.MonitoringService;
import saasim.core.iaas.Provider;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Machine;
import saasim.core.infrastructure.MachineFactory;
import saasim.core.provisioning.ProvisioningSystem;
import saasim.core.saas.Application;

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
		private MonitoringService poolMonitor;

		public RanjanReconfigurablePool(Application application, int tierID, MonitoringService poolMonitor, List<InstanceDescriptor> vmPool) {
			this.poolMonitor = poolMonitor;
			this.application = application;
			this.tierID = tierID;
			this.vmPool = vmPool;
		}

		public void reconfigure() {
			
			int delta = evaluateNumberOfServersForNextInterval();
			
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
		private int evaluateNumberOfServersForNextInterval() {
			
			Map<String, SummaryStatistics> statistics = poolMonitor.getStatistics();
			
			List<Double> results = new ArrayList<>();
			results.add(statistics.get("TIME").getMax());
			results.add(statistics.get("arrival_" + tierID).getSum());
			results.add(statistics.get("failure_" + tierID).getSum());
			results.add(statistics.get("rejection_" + tierID).getSum());
			results.add(statistics.get("finish_" + tierID).getSum());
			results.add(statistics.get("rt_" + tierID).getSum());
			
			results.add(statistics.get("arrived").getSum());
			results.add(statistics.get("failed").getSum());
			results.add(statistics.get("util").getMean());
			results.add(statistics.get("pq").getMean());
			results.add(statistics.get("bq").getMean());
			results.add(statistics.get("ap").getMean());
			results.add(statistics.get("at").getMean());
			
			System.out.println(results);
			
			
//			Logger.getLogger(ProvisioningSystem.class).info((long)statistics.get("TIME").getMax()+","+(long)statistics.get(application.getID()+"_arrivalrate").getMean()+","+(long)statistics.get(application.getID()+"_arrivalrate").getMean());

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


	private static final String RANJAN_ENABLE = "dps.ranjan.enable";
	
	private static final String RANJAN_TICK = "dps.ranjan.tick";
	
	private static String RANJAN_TARGET_UTILISATION = "dps.ranjan.target";  

	private Provider provider;

	private List<RanjanReconfigurablePool> reconfigurableSets;

	private EventScheduler scheduler;

	private long tick;

	private int[] startNumberOfReplicas;

	private String[] vmTypePerTier;

	private double[] targetUtilisation;

	private com.google.inject.Provider<MonitoringService> monitoringServiceProvider;

	private boolean[] enable;

	private MachineFactory machineFactory;

	@Inject
	public RanjanProvisioningSystem(EventScheduler scheduler, Configuration globalConf, Provider provider, MachineFactory machineFactory, com.google.inject.Provider<MonitoringService> monitoringService) {
		this.scheduler = scheduler;
		this.provider = provider;
		this.machineFactory = machineFactory;
		this.monitoringServiceProvider = monitoringService;
		this.reconfigurableSets = new ArrayList<RanjanReconfigurablePool>();
		
		this.startNumberOfReplicas = globalConf.getIntegerArray(Application.APPLICATION_TIER_INIT);
		this.vmTypePerTier = globalConf.getStringArray(Application.APPLICATION_TIER_VMTYPE);
		this.targetUtilisation = globalConf.getDoubleArray(RANJAN_TARGET_UTILISATION);
		this.enable = globalConf.getBooleanArray(RANJAN_ENABLE);
		this.tick = globalConf.getLong(RANJAN_TICK);
				
				
		scheduler.queueEvent(new Event(tick, EventPriority.LOW){
			@Override
			public void trigger() {
				evaluate();
			}
		});
	}

	protected void evaluate() {
		for (RanjanReconfigurablePool reconfigurableSet : reconfigurableSets) {
			reconfigurableSet.reconfigure();
		}
		scheduler.queueEvent(new Event(scheduler.now() + tick, EventPriority.LOW){
			@Override
			public void trigger() {
				evaluate();
			}
		});
	}

	@Override
	public void registerConfigurable(Application... applications) {
		
		for (Application application : applications) {
			MonitoringService applicationMonitor = monitoringServiceProvider.get();
			applicationMonitor.setMonitorable((Monitorable) application);
			
			for (int tierID = 0; tierID < startNumberOfReplicas.length; tierID++) {
				List<InstanceDescriptor> vmPool = new ArrayList<>();
				MonitoringService poolMonitor = monitoringServiceProvider.get();
				poolMonitor.setMonitorable(applicationMonitor);
				for (int j = 0; j < Integer.valueOf(startNumberOfReplicas[tierID]); j++) {
					InstanceDescriptor instance = provider.acquire(vmTypePerTier[tierID]);
					instance.setApplication(application);
					Machine machine = machineFactory.create(instance);
					poolMonitor.setMonitorable((Monitorable) machine);
					
					Configuration config = new Configuration();
					config.setProperty(Configuration.TIER_ID, tierID);
					config.setProperty(Configuration.ACTION, Configuration.ACTION_INCREASE);
					config.setProperty(Configuration.INSTANCE_DESCRIPTOR, instance);
					config.setProperty(Configuration.FORCE, true);
					config.setProperty(Configuration.MACHINE, machine);
					application.configure(config);
					vmPool.add(instance);
				}
				if(enable[tierID]){
					reconfigurableSets.add(new RanjanReconfigurablePool(application, tierID, poolMonitor, vmPool));
				}
			}
		}
	}

	@Override
	public UtilityFunction calculateUtility() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
