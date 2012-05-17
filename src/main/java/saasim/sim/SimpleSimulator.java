package saasim.sim;

import java.util.List;

import org.apache.log4j.Logger;

import saasim.cloud.Request;
import saasim.config.Configuration;
import saasim.io.WorkloadParser;
import saasim.io.WorkloadParserFactory;
import saasim.provisioning.DPS;
import saasim.sim.components.LoadBalancer;
import saasim.sim.core.AbstractEventHandler;
import saasim.sim.core.Event;
import saasim.sim.core.EventCheckpointer;
import saasim.sim.core.EventScheduler;
import saasim.sim.core.EventType;
import saasim.sim.util.SimulatorProperties;
import saasim.util.SimulationInfo;


/**
 * This class represents a simulator of SaaSim, it features and basic operations.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimpleSimulator extends AbstractEventHandler implements Simulator{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8028580648054904982L;
	
	private DynamicConfigurable[] applications;

	private WorkloadParser<List<Request>>[] parsers;
	
	private DPS dps;
	
	private SimulationInfo info;


	/**
	 * Default constructor.
	 * @param scheduler A {@link EventScheduler} to represent a scheduler of {@link SimpleSimulator}.
	 * @param dps TODO
	 * @param applications An array containing the tiers of application, see {@link LoadBalancer}.
	 */
	public SimpleSimulator(EventScheduler scheduler, DPS dps, DynamicConfigurable... applications){
		super(scheduler);
		this.info = new SimulationInfo();
		this.applications = applications;
		this.dps = dps;
		
		this.dps.registerConfigurable(this.applications);
		
		this.parsers = new WorkloadParser[applications.length];
		
		for (int i = 0; i < applications.length; i++) {
			parsers[i] = WorkloadParserFactory.getWorkloadParser();
			applications[i].setWorkloadParser(parsers[i]);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		prepareBeforeStart();
		getScheduler().start();
		
		if(info.isFinishDay()){
			Logger logger = Logger.getLogger(SimpleSimulator.class);
			logger.info(dps.calculateUtility());
			logger.debug(Configuration.getInstance().getScheduler().dumpPostMortemEvents());
		}else{//Persisting dump
			Configuration.getInstance().save();
		}

	}

	/**
	 * Prepare the simulator before it start up, starting events like 
	 * {@link JEEventType#READWORKLOAD#CHARGE_USERS#ESTIMATE_SERVERS#COLLECT_STATISTICS}. 
	 */
	protected void prepareBeforeStart() {
		send(new Event(EventType.READWORKLOAD, applications[0], getScheduler().now()));
		
		if(info.isChargeDay()){
			send(new Event(EventType.CHARGE_USERS, applications[0], info.getCurrentDayInMillis() + EventCheckpointer.INTERVAL - 1));
		}
		
		
		if(info.isFirstDay()){
			if(this.dps.isOptimal()){ //TODO:"Change this!
				send(new Event(EventType.ESTIMATE_SERVERS, applications[0], getScheduler().now()));
			}else{
				long monitoringInterval = Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL);
				send(new Event(EventType.COLLECT_STATISTICS, applications[0], getScheduler().now() + monitoringInterval));
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DynamicConfigurable[] getApplications() {
		return applications;
	}

	@Override
	public void restore() {
		this.info.addDay();
		
		for (WorkloadParser<List<Request>> parser : parsers) {
			parser.clear();
		}
//		for (DynamicConfigurable configurable : applications) {
//			configurable.setWorkloadParser(WorkloadParserFactory.getWorkloadParser());
//		}
	}

	@Override
	public SimulationInfo getSimulationInfo() {
		return this.info;
	}
}
