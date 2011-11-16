package commons.sim;

import java.io.IOException;
import java.util.List;

import provisioning.Monitor;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.io.Checkpointer;
import commons.io.WorkloadParser;
import commons.sim.components.LoadBalancer;
import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.util.SimulatorProperties;
import commons.util.SimulationInfo;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimpleSimulator extends JEAbstractEventHandler implements Simulator{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8028580648054904982L;
	
	private LoadBalancer tiers [];
	
	protected transient WorkloadParser<List<Request>> workloadParser;
	protected transient Monitor monitor;

	private long monitoringInterval;

	private int numberOfRequests;

	/**
	 * Constructor
	 * @param list 
	 * @throws IOException 
	 */
	public SimpleSimulator(JEEventScheduler scheduler, LoadBalancer... tiers){
		super(scheduler);
		this.tiers = tiers;
		this.numberOfRequests = 0;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		prepareBeforeStart();
		getScheduler().start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWorkloadParser(WorkloadParser<List<Request>> workloadParser) {
		assert workloadParser != null : "WorkloadParser could not be null! Check your code.";
		this.workloadParser = workloadParser;
	}
	
	protected void prepareBeforeStart() {
		
		send(new JEEvent(JEEventType.READWORKLOAD, this, getScheduler().now()));
		
		SimulationInfo info = Checkpointer.loadSimulationInfo();
		if(info.isChargeDay()){
			send(new JEEvent(JEEventType.CHARGE_USERS, this, info.getCurrentDayInMillis() + Checkpointer.INTERVAL - 1));
		}
		
		monitoringInterval = Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL);
		
		if(info.isFirstDay()){
			if(this.monitor.isOptimal()){ //TODO:"Change this!
				send(new JEEvent(JEEventType.ESTIMATE_SERVERS, this, getScheduler().now()));
			}else{
				send(new JEEvent(JEEventType.COLLECT_STATISTICS, this, getScheduler().now() + monitoringInterval));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
			case READWORKLOAD:
				if(workloadParser.hasNext()) {
					List<Request> list = workloadParser.next();
					numberOfRequests += list.size(); 
					for (Request request : list) {
						send(parseEvent(request));
					}
					if(workloadParser.hasNext()){
						long newEventTime = getScheduler().now() + Configuration.getInstance().getParserPageSize().getMillis();
						send(new JEEvent(JEEventType.READWORKLOAD, this, newEventTime, true));
					}else{
						workloadParser.close();
					}
				}
				break;
			case CHARGE_USERS:
				this.monitor.chargeUsers(event.getScheduledTime());
				break;
			case COLLECT_STATISTICS:
				long time = event.getScheduledTime();
				for (LoadBalancer loadBalancer : tiers) {
					loadBalancer.collectStatistics(time, monitoringInterval, numberOfRequests);
				}
				numberOfRequests = 0;
				send(new JEEvent(JEEventType.COLLECT_STATISTICS, this, getScheduler().now() + Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)));
				break;
			case ESTIMATE_SERVERS:
				long currentTime = event.getScheduledTime();
				for (LoadBalancer loadBalancer : tiers) {
					loadBalancer.estimateServers(currentTime);
				}
				send(new JEEvent(JEEventType.ESTIMATE_SERVERS, this, getScheduler().now() + 1000 * 60 * 60));//One hour later
				break;
			default:
				break;
		}
	}

	/**
	 * @param request
	 * @return
	 */
	protected JEEvent parseEvent(Request request) {
		return new JEEvent(JEEventType.NEWREQUEST, tiers[0], request.getArrivalTimeInMillis(), request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMachine(int tier, MachineDescriptor machineDescriptor, boolean useStartUpDelay) {
		assert tiers.length > tier : "This tier not exists!";
		tiers[tier].addMachine(machineDescriptor, useStartUpDelay);
	}
	
	@Override
	public void removeMachine(int tier, boolean force) {
		assert tiers.length >= tier : "This tier not exists!";
		tiers[tier].removeMachine(force);
	}

	/**
	 * @return
	 */
	@Override
	public LoadBalancer[] getTiers() {
		return this.tiers;
	}

	public WorkloadParser<List<Request>> getParser() {
		return this.workloadParser;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
		for (LoadBalancer loadBalancers : tiers) {
			loadBalancers.setMonitor(monitor);
		}
	}

	@Override
	public void cancelMachineRemoval(int tier, int numberOfMachines) {
		tiers[tier].cancelMachineRemoval(numberOfMachines);
	}

}