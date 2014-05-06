package saasim.core.sim;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import saasim.core.application.Application;
import saasim.core.application.Request;
import saasim.core.application.Tier;
import saasim.core.cloud.IaaSProvider;
import saasim.core.cloud.utility.UtilityFunction;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventCheckpointer;
import saasim.core.event.EventScheduler;
import saasim.core.provisioning.DPS;
import saasim.core.util.TimeUnit;
import saasim.sim.core.EventType;

import com.google.inject.Inject;

/**
 * Simple implementation of a {@link Simulator} composed by:<br>
 * <ul>
 * <li>a set of {@link Tier} applications;</li>
 * <li>one {@link EventScheduler}</li>
 * <li></li>
 * <li></li>
 * <li></li>
 * </ul>
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * 
 */
public class SaaSim{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2617169446354857178L;
	private Configuration config;
	private EventScheduler scheduler;
	private IaaSProvider iaasProvider;
	private DPS dps;
	private Application application;
	private WorkloadGenerator workloadGenerator;

	/**
	 * @param configuration
	 * @throws ConfigurationException
	 */
	@Inject
	public SaaSim(Configuration config, EventScheduler scheduler,
			IaaSProvider iaasProvider, DPS dps, Application application, WorkloadGenerator workloadGenerator) throws ConfigurationException {

		this.config = config;
		this.scheduler = scheduler;
		this.iaasProvider = iaasProvider;
		this.application = application;
		this.dps = dps;
		this.workloadGenerator = workloadGenerator;
		
		this.dps.registerConfigurable(application);
	}

	@Override
	public void start() {
		
		scheduler.queueEvent(new Event(scheduler.now()){
			@Override
			public void trigger() {
				workloadGenerator.start();
			}
		});

		scheduler.start(config.getLong("saasim.end"));
	}
}
