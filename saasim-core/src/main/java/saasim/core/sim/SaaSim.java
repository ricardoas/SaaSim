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
public class SaaSim implements Simulator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2617169446354857178L;
	private EventScheduler scheduler;
	private Configuration config;
	private DPS dps;
	// private RS rs;
	private UtilityFunction utilityFunction;

	private IaaSProvider iaasProvider;
	private Application application;

	/**
	 * @param configuration
	 * @throws ConfigurationException
	 */
	@Inject
	public SaaSim(Configuration config, EventScheduler scheduler,
			IaaSProvider iaasProvider, DPS dps) throws ConfigurationException {

		System.out.println("SaaSim.SaaSim() config=" + config + " provider="
				+ iaasProvider + " dps=" + dps);
		this.config = config;
		this.scheduler = scheduler;
		this.iaasProvider = iaasProvider;
		this.dps = dps;
		readWorkload();

	}

	private void readWorkload() {
		final Request request = null;
		
		scheduler.queueEvent(new Event(scheduler.now()) {
			@Override
			public void trigger() {
				application.process(request, null);
			}
		});
	}

	@Override
	public void start() {

		long simulationEnd = config.getLong("saasim.end")
				* TimeUnit.valueOf(
						config.getString("saasim.end.unit",
								TimeUnit.DAY.toString())).getMillis();

		long checkpointAt = simulationEnd;
		if (config.getBoolean("saasim.checkpoint", false)) {
			long checkpointInterval = TimeUnit.valueOf(
					config.getString("saasim.checkpoint.unit",
							TimeUnit.DAY.toString())).getMillis();
			checkpointAt = scheduler.now() + simulationEnd / checkpointInterval;
		}

		scheduler.start(checkpointAt);

		if (scheduler.now() < simulationEnd) {
			EventCheckpointer.save(scheduler);
		} else {
			Logger logger = Logger.getLogger(SaaSim.class);
			logger.info(utilityFunction);
			// logger.debug(config.getScheduler().dumpPostMortemEvents());
		}
	}
}
