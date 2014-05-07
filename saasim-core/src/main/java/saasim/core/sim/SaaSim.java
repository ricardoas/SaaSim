package saasim.core.sim;

import org.apache.commons.configuration.ConfigurationException;

import saasim.core.application.Application;
import saasim.core.application.Tier;
import saasim.core.cloud.IaaSProvider;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.provisioning.DPS;

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

	private Configuration config;
	private EventScheduler scheduler;
	private IaaSProvider iaasProvider;
	private DPS dps;
	private Application application;
	private WorkloadTrafficGenerator workloadGenerator;

	/**
	 * @param configuration
	 * @throws ConfigurationException
	 */
	@Inject
	public SaaSim(Configuration config, EventScheduler scheduler,
			IaaSProvider iaasProvider, DPS dps, Application application, WorkloadTrafficGenerator workloadGenerator) throws ConfigurationException {

		this.config = config;
		this.scheduler = scheduler;
		this.iaasProvider = iaasProvider;
		this.application = application;
		this.dps = dps;
		this.workloadGenerator = workloadGenerator;
		
		this.dps.registerConfigurable(application);
	}

	public void start() {
		
		scheduler.queueEvent(new Event(scheduler.now()){
			@Override
			public void trigger() {
				workloadGenerator.start();
			}
		});
		scheduler.start(config.getLong("simulation.time"));
	}
}
