package saasim.quid.provisioning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.log4j.Logger;

import saasim.core.cloud.utility.UtilityFunction;
import saasim.core.config.Configuration;
import saasim.core.deprovisioning.DeprovisioningSystem;
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
import com.google.inject.Singleton;


/**
 * Simple implementation of QuID-online algorithm as depicted in: 
 * <a href='http://dx.doi.org/10.1109/IWQoS.2002.1006569'>http://dx.doi.org/10.1109/IWQoS.2002.1006569<a>
 * <br>
 * This implementation is not ready to handle pool of heterogeneous instances.
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
@Singleton
public class QuIDOnlineProvisioningSystem implements ProvisioningSystem {
	
	private class RanjanReconfigurablePool {
		private Application application;
		private List<InstanceDescriptor> vmPool;
		private int tierID;
		private MonitoringService poolMonitor;
		private Map<String, SummaryStatistics> statistics;
		
		private SummaryStatistics arrived;
		private SummaryStatistics finished;
		private SummaryStatistics util;
		private SummaryStatistics time;
		private int number_of_active_servers;

		public RanjanReconfigurablePool(Application application, int tierID, MonitoringService poolMonitor, List<InstanceDescriptor> vmPool) {
			this.poolMonitor = poolMonitor;
			this.application = application;
			this.tierID = tierID;
			this.vmPool = vmPool;
			
			this.arrived = new SummaryStatistics();
			this.finished = new SummaryStatistics();
			this.util = new SummaryStatistics();
			this.time = new SummaryStatistics();
			this.number_of_active_servers = this.vmPool.size();
		}

		public void reconfigure() {
			
			statistics = poolMonitor.getStatistics();
		
			long now = (long) statistics.get("TIME").getMean();
			
			time.addValue( now );
			arrived.addValue( statistics.get("arrival_" + tierID).getMean() );
			finished.addValue( statistics.get("finish_" + tierID).getMean() );
			util.addValue( statistics.get("util").getMean() );
			number_of_active_servers = (int) statistics.get("util").getN();

			if(now % tick == 0){
				int delta = evaluateNumberOfServersForNextInterval();

				if(delta < 0){
					for (int i = 0; i < -delta; i++) {
						InstanceDescriptor machineToTurnOff = deprovisioningSystem.chooseMachineToTurnOff(vmPool);
						if(machineToTurnOff != null){
							vmPool.remove(machineToTurnOff);
							releaseInstance(application, tierID, poolMonitor, machineToTurnOff);
						}
					}
				}else{
					for (int i = 0; i < delta; i++) {
						if(provider.canAcquire(vmTypePerTier[tierID])){
							acquireInstance(application, tierID, poolMonitor, vmPool);
						}
					}
				}

				arrived = new SummaryStatistics();
				finished = new SummaryStatistics();
				util = new SummaryStatistics();
				time = new SummaryStatistics();;
			}

			printStatistics();
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
			
			double finished_requests = finished.getSum();
			double arrived_requests = arrived.getSum();
//			int number_of_active_servers = vmPool.size();
			double u = util.getMean();
			
			double d = u / finished_requests;
			double u_dash = Math.max(arrived_requests, finished_requests) * d;
			
			int n_dash = (int) Math.ceil( u_dash * number_of_active_servers / targetUtilisation[tierID] );
			
			return Math.max(1, n_dash) - number_of_active_servers;
		}
		
		public void printStatistics(){
			StringBuilder sb = new StringBuilder();
			sb.append(application.getID());
			sb.append(", ");
			sb.append(tierID);
			sb.append(", ");
			sb.append((long)statistics.get("TIME").getMax());
			sb.append(", ");
			sb.append((long)statistics.get("arrival_" + tierID).getSum());
			sb.append(", ");
			sb.append((long)statistics.get("rejection_" + tierID).getSum());
			sb.append(", ");
			sb.append((long)statistics.get("failure_" + tierID).getSum());
			sb.append(", ");
			sb.append((long)statistics.get("finish_" + tierID).getSum());
			sb.append(", ");
			sb.append(statistics.get("rt_" + tierID).getSum());
			sb.append(", ");
			sb.append(statistics.get("util").getMean());
			sb.append(", ");
			sb.append((long)statistics.get("util").getN());
			
			LOGGER.info(sb.toString());
		}
	}


	private static final String RANJAN_ENABLE = "provisioning.ranjan.enable";
	
	private static final String RANJAN_TICK = "provisioning.ranjan.tick";
	
	private static final String RANJAN_WARMUP = "provisioning.ranjan.warmup";
	
	private static String RANJAN_TARGET_UTILISATION = "provisioning.ranjan.target";  

	private static final Logger LOGGER = Logger.getLogger(ProvisioningSystem.class);

	private Provider provider;

	private List<RanjanReconfigurablePool> reconfigurableSets;

	private EventScheduler scheduler;

	private long tick;

	private long warmup;

	private int[] startNumberOfReplicas;

	private String[] vmTypePerTier;

	private double[] targetUtilisation;

	private com.google.inject.Provider<MonitoringService> monitoringServiceProvider;

	private boolean[] enable;

	private MachineFactory machineFactory;

	private DeprovisioningSystem deprovisioningSystem;

	private long monitoringtick;

	@Inject
	public QuIDOnlineProvisioningSystem(EventScheduler scheduler, Configuration globalConf, Provider provider, MachineFactory machineFactory, com.google.inject.Provider<MonitoringService> monitoringService, DeprovisioningSystem deprovisioningSystem) {
		this.scheduler = scheduler;
		this.provider = provider;
		this.machineFactory = machineFactory;
		this.monitoringServiceProvider = monitoringService;
		this.deprovisioningSystem = deprovisioningSystem;
		this.reconfigurableSets = new ArrayList<RanjanReconfigurablePool>();
		
		this.startNumberOfReplicas = globalConf.getIntegerArray(Application.APPLICATION_TIER_INIT);
		this.vmTypePerTier = globalConf.getStringArray(Application.APPLICATION_TIER_VMTYPE);
		this.targetUtilisation = globalConf.getDoubleArray(RANJAN_TARGET_UTILISATION);
		this.enable = globalConf.getBooleanArray(RANJAN_ENABLE);
		this.tick = globalConf.getLong(RANJAN_TICK);
		this.warmup = globalConf.getLong(RANJAN_WARMUP);
		this.monitoringtick = globalConf.getLong(MonitoringService.MONITORING_SERVICE_TIMEBETWEENREPORTS);
				
				
		scheduler.queueEvent(new Event(monitoringtick + warmup, EventPriority.LOW){
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
		scheduler.queueEvent(new Event(scheduler.now() + monitoringtick, EventPriority.LOW){
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
			applicationMonitor.register((Monitorable) application);
			
			for (int tierID = 0; tierID < startNumberOfReplicas.length; tierID++) {
				List<InstanceDescriptor> vmPool = new ArrayList<>();
				MonitoringService poolMonitor = monitoringServiceProvider.get();
				poolMonitor.addChildMonitoringService(applicationMonitor);
				for (int j = 0; j < Integer.valueOf(startNumberOfReplicas[tierID]); j++) {
					acquireInstance(application, tierID, poolMonitor, vmPool);
				}
				if(enable[tierID]){
					reconfigurableSets.add(new RanjanReconfigurablePool(application, tierID, poolMonitor, vmPool));
				}
			}
		}
	}

	private InstanceDescriptor acquireInstance(final Application application, final int tierID, final MonitoringService monitoringService, final List<InstanceDescriptor> vmPool) {
		final InstanceDescriptor instance = provider.acquire(vmTypePerTier[tierID]);
		instance.setTier(tierID);
		System.err.println(tierID + " " + instance);
		final Machine machine = machineFactory.create(instance);
		instance.setApplication(application);


		scheduler.queueEvent(new Event(scheduler.now() + machine.getStartUpDelay()) {
			@Override
			public void trigger() {
				monitoringService.register((Monitorable) machine);
				
				Configuration config = new Configuration();
				config.setProperty(Configuration.TIER_ID, tierID);
				config.setProperty(Configuration.ACTION, Configuration.ACTION_INCREASE);
				config.setProperty(Configuration.MACHINE, machine);

				application.configure(config);
				vmPool.add(instance);
			}
		});

		
		return instance;
	}
	
	private void releaseInstance(Application application, int tierID, final MonitoringService monitoringService, final InstanceDescriptor instance) {
		
		final Machine machine = instance.getMachine();
		
		Configuration config = new Configuration();
		config.setProperty(Configuration.TIER_ID, tierID);
		config.setProperty(Configuration.ACTION, Configuration.ACTION_DECREASE);
		config.setProperty(Configuration.MACHINE, machine);
		application.configure(config);
		
		shutdownIfIdle(monitoringService, instance, machine);
	}

	private void shutdownIfIdle(final MonitoringService monitoringService,
			final InstanceDescriptor instance, final Machine machine) {
		scheduler.queueEvent(new Event(scheduler.now() + 1000) {
			@Override
			public void trigger() {
				if(machine.canShutdown()){
					provider.release(instance);
					monitoringService.unregister((Monitorable)  instance.getMachine());
				}else{
					shutdownIfIdle(monitoringService, instance, machine);
				}
			}
		});
	}

	@Override
	public UtilityFunction calculateUtility() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
