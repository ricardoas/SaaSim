package saasim.ext.provisioning;

import saasim.core.cloud.utility.UtilityFunction;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
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

public class StaticProvisioningSystem implements ProvisioningSystem {
	
	private Application[] applications;
	private Provider provider;
	private String[] startNumberOfReplicas;
	private String[] vmTypePerTier;
	private long tick;
	private MonitoringService monitoringService;
	private EventScheduler scheduler;
	private MachineFactory machineFactory;

	@Inject
	public StaticProvisioningSystem(EventScheduler scheduler, Configuration globalConf, Provider provider, MonitoringService monitoringService, MachineFactory machineFactory) {
		this.scheduler = scheduler;
		this.provider = provider;
		this.monitoringService = monitoringService;
		this.machineFactory = machineFactory;
		startNumberOfReplicas = globalConf.getStringArray(Application.APPLICATION_TIER_INIT);
		vmTypePerTier = globalConf.getStringArray(Application.APPLICATION_TIER_VMTYPE);
		tick = globalConf.getLong(MonitoringService.MONITORING_SERVICE_TIMEBETWEENREPORTS);
		
		scheduler.queueEvent(new Event(tick){
			@Override
			public void trigger() {
				evaluate();
			}
		});
	}

	protected void evaluate() {
//		Map<String, SummaryStatistics> statistics = monitoringService.getStatistics();
//		Logger.getLogger(ProvisioningSystem.class).info((long)statistics.get("TIME").getMax());

		scheduler.queueEvent(new Event(scheduler.now() + tick){
			@Override
			public void trigger() {
				evaluate();
			}
		});
	}

	@Override
	public void registerConfigurable(Application... applications) {
		this.applications = applications;
		setUp();
	}

	protected void setUp() {
		for (Application application : applications) {
			monitoringService.setMonitorable((Monitorable) application);
			for (int tierID = 0; tierID < startNumberOfReplicas.length; tierID++) {
				int numberOfReplicas = Integer.valueOf(startNumberOfReplicas[tierID]);
				while(numberOfReplicas-- > 0){
					if(provider.canAcquire(vmTypePerTier[tierID])){
						InstanceDescriptor descriptor = provider.acquire(vmTypePerTier[tierID]);
						descriptor.setApplication(application);
						Machine machine = machineFactory.create(descriptor);
						monitoringService.setMonitorable((Monitorable) machine);
						
						Configuration config = new Configuration();
						config.setProperty(Configuration.TIER_ID, tierID);
						config.setProperty(Configuration.ACTION, Configuration.ACTION_INCREASE);
						config.setProperty(Configuration.INSTANCE_DESCRIPTOR, descriptor);
						config.setProperty(Configuration.FORCE, true);
						config.setProperty(Configuration.MACHINE, machine);
						application.configure(config);
					}
				}
			}
		}
	}

	@Override
	public UtilityFunction calculateUtility() {
		return null;
	}
}
