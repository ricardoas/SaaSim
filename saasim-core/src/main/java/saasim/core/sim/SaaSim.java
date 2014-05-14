package saasim.core.sim;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import saasim.core.application.Application;
import saasim.core.cloud.IaaSProvider;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.MonitoringService;
import saasim.core.infrastructure.MonitoringServiceConsumer;
import saasim.core.provisioning.ProvisioningSystem;

import com.google.inject.Inject;

/**
 * SaaSim main class. It builds and glues all other components.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SaaSim{

	public static final String SAASIM_SIMULATION_TIME = "simulation.time";
	private EventScheduler scheduler;
	private ProvisioningSystem dps;
	private Application application;
	private WorkloadTrafficGenerator workloadGenerator;
	private MonitoringService feed;
	private long simulationTime;

	/**
	 * Default constructor.
	 * 
	 * @param globalConf {@link Configuration} instance.
	 * @param scheduler {@link Event} queue manager.
	 * @param iaasProvider {@link IaaSProvider} instance.
	 * @param dps provisioner instance.
	 * @param application {@link Application} being simulated.
	 * @param workloadGenerator traffic generator.
	 * 
	 * @throws ConfigurationException
	 */
	@Inject
	public SaaSim(Configuration globalConf, EventScheduler scheduler, ProvisioningSystem dps, Application application, WorkloadTrafficGenerator workloadGenerator, MonitoringService feed, MonitoringServiceConsumer consumer) throws ConfigurationException {

		this.scheduler = scheduler;
		this.application = application;
		
		this.dps = dps;
		this.workloadGenerator = workloadGenerator;
		this.feed = feed;
		this.feed.subscribe(consumer);
		
		this.dps.registerConfigurable(this.application);
		this.simulationTime = globalConf.getLong(SAASIM_SIMULATION_TIME);
	}

	/**
	 * Start simulation
	 */
	public void start() {
		
		long start = System.currentTimeMillis();
		Logger.getLogger(SaaSim.class).debug("SIMULATION START " + start);
		
		scheduler.queueEvent(new Event(scheduler.now()){
			@Override
			public void trigger() {
				workloadGenerator.start();
			}
		});
		scheduler.start(simulationTime);

		Logger.getLogger(SaaSim.class).debug("SIMULATION END " + System.currentTimeMillis());
		Logger.getLogger(SaaSim.class).debug("SIMULATION DURATION " + (System.currentTimeMillis()-start));
	}
}
