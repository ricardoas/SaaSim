package saasim.ext.sim;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;

import saasim.core.application.Tier;
import saasim.core.cloud.IaaSProvider;
import saasim.core.cloud.util.IaaSProviderFactory;
import saasim.core.cloud.utility.UtilityFunction;
import saasim.core.config.Configuration;
import saasim.core.event.EventCheckpointer;
import saasim.core.event.EventScheduler;
import saasim.core.provisioning.DPS;
import saasim.core.provisioning.DPSFactory;
import saasim.core.sim.Simulator;
import saasim.core.util.TimeUnit;
import saasim.ext.application.ApplicationFactory;

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
public class SimpleSimulator implements Simulator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2617169446354857178L;
	private EventScheduler scheduler;
	private Configuration config;
	private Tier[] applications;
	private DPS dps;
//	private RS rs;
	private UtilityFunction utilityFunction;
	
	
	private IaaSProvider[] iaasProviders;
	

	/**
	 * 
	 */
	public SimpleSimulator() {
		
		config = Configuration.getInstance();
		
		if(EventCheckpointer.hasCheckpoint()){
			Iterator<Serializable> iterator = Arrays.asList(EventCheckpointer.load()).iterator();
			scheduler = (EventScheduler) iterator.next();
			applications = (Tier[]) iterator.next();
			dps = (DPS) iterator.next();
			
		}else{
			scheduler = new EventScheduler(config.getLong("random.seed", 31));
			
			iaasProviders = IaaSProviderFactory.getInstance().buildIaaSProviders();

			dps = DPSFactory.createDPS(iaasProviders);
			applications = ApplicationFactory.buildApplication(scheduler, dps);
			readWorkload();
		}
		
		
		this.scheduler.clearAndRegisterAnnotations(null);
		this.scheduler.clearAndRegisterHandlerClasses(null);
	}

	private void readWorkload() {
		for (Tier application : applications) {
			scheduler.queueEvent(application, null, scheduler.now());
		}
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
			Logger logger = Logger.getLogger(SimpleSimulator.class);
			logger.info(utilityFunction);
			logger.debug(config.getScheduler().dumpPostMortemEvents());
		}
	}
}
