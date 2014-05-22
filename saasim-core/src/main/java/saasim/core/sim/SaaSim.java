package saasim.core.sim;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import saasim.core.application.Application;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.iaas.Provider;
import saasim.core.provisioning.ProvisioningSystem;
import saasim.core.saas.ASP;
import saasim.core.saas.Tenant;

import com.google.inject.Inject;

/**
 * SaaSim main class. It builds and glues all other components.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SaaSim{

	public static final String SAASIM_SIMULATION_TIME = "simulation.time";
	private EventScheduler scheduler;
	private long simulationTime;
	private ASP asp;

	/**
	 * Default constructor.
	 * 
	 * @param globalConf {@link Configuration} instance.
	 * @param scheduler {@link Event} queue manager.
	 * @param dps provisioner instance.
	 * @param iaasProvider {@link Provider} instance.
	 * @param application {@link Application} being simulated.
	 * @param workloadGenerator traffic generator.
	 * @throws ConfigurationException
	 */
	@Inject
	public SaaSim(Configuration globalConf, EventScheduler scheduler, ASP asp) throws ConfigurationException {

		this.scheduler = scheduler;
		
		this.asp = asp;
		
		this.simulationTime = globalConf.getLong(SAASIM_SIMULATION_TIME);
	}

	/**
	 * Start simulation
	 */
	public void start() {
		
		long start = System.currentTimeMillis();
		Logger.getLogger(SaaSim.class).debug("SIMULATION START " + start);
		
		asp.setUp();
		
		scheduler.start(simulationTime);

		Logger.getLogger(SaaSim.class).debug("SIMULATION END " + System.currentTimeMillis());
		Logger.getLogger(SaaSim.class).debug("SIMULATION DURATION " + (System.currentTimeMillis()-start));
	}
}
