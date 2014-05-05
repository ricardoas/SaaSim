package saasim.core.sim;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import saasim.core.application.Application;
import saasim.core.application.Tier;
import saasim.core.cloud.IaaSProvider;
import saasim.core.cloud.utility.UtilityFunction;
import saasim.core.config.Configuration;
import saasim.core.event.EventCheckpointer;
import saasim.core.event.EventScheduler;
import saasim.core.provisioning.DPS;
import saasim.core.util.TimeUnit;

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
//	private RS rs;
	private UtilityFunction utilityFunction;
	
	
	private IaaSProvider iaasProvider;
	private Application application;
	
	/**
	 * @param configuration 
	 * @throws ConfigurationException 
	 */
	public SaaSim(Configuration config) throws ConfigurationException {
		
		this.config = config;
		
		if(EventCheckpointer.hasCheckpoint()){
			Iterator<Serializable> iterator = Arrays.asList(EventCheckpointer.load()).iterator();
			
			scheduler = (EventScheduler) iterator.next();
			iaasProvider = (IaaSProvider) iterator.next();
			dps = (DPS) iterator.next();
			application = (Application) iterator.next();

		}else{
			
			scheduler = new EventScheduler(config.getLong("random.seed", 0));
			iaasProvider = config.getInjector().getInstance(IaaSProvider.class);
			
//			iaasProvider = config.getIaaSProvidersFactory().build(config);
			dps = config.getDPSFactory().build(config, iaasProvider);
			application = config.getApplicationFactory().build(scheduler, dps);
			
			readWorkload();
		}
		
		String[] events = config.getStringArray("simulation.events");
		String[] handlers = config.getStringArray("simulation.handlers");
		this.scheduler.setup(events, handlers);
	}

	private void readWorkload() {
//			scheduler.queueEvent(application, null, scheduler.now());
	}

	@Override
	public void start() {
		
		long simulationEnd = config.getLong("saasim.end") * 
				TimeUnit.valueOf(config.getString("saasim.end.unit", TimeUnit.DAY.toString())).getMillis();
		
		long checkpointAt = simulationEnd;
		if(config.getBoolean("saasim.checkpoint", false)){
			long checkpointInterval = TimeUnit.valueOf(config.getString("saasim.checkpoint.unit", TimeUnit.DAY.toString())).getMillis();
			checkpointAt = scheduler.now() + simulationEnd/checkpointInterval;
		}
		
		scheduler.start(checkpointAt);
		
		if(scheduler.now() < simulationEnd){
			EventCheckpointer.save(scheduler);
		}else{
			Logger logger = Logger.getLogger(SaaSim.class);
			logger.info(utilityFunction);
//			logger.debug(config.getScheduler().dumpPostMortemEvents());
		}
	}
}
