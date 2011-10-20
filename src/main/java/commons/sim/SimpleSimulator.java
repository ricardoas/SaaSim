package commons.sim;

import java.io.IOException;
import java.util.List;

import provisioning.Monitor;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.io.TickSize;
import commons.io.WorkloadParser;
import commons.sim.components.LoadBalancer;
import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.util.SimulatorProperties;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimpleSimulator extends JEAbstractEventHandler implements Simulator{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8028580648054904982L;
	
	public static int[] daysInMonths = {31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365};
	
	protected int currentMonth;
	private LoadBalancer tiers [];
	
	protected transient WorkloadParser<List<Request>> workloadParser;
	protected transient Monitor monitor;

	/**
	 * Constructor
	 * @param list 
	 * @throws IOException 
	 */
	public SimpleSimulator(JEEventScheduler scheduler, LoadBalancer... tiers){
		super(scheduler);
		this.tiers = tiers;
		this.currentMonth = Configuration.getInstance().getSimulationInfo().getCurrentMonth();
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
		
		int simulatedDays = Configuration.getInstance().getSimulationInfo().getSimulatedDays();
		if(simulatedDays < Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD)){
			send(new JEEvent(JEEventType.READWORKLOAD, this, getScheduler().now()));
			send(new JEEvent(JEEventType.COLLECT_STATISTICS, this, getScheduler().now() + Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)));
			
			if(simulatedDays + 1 == daysInMonths[currentMonth]){
				send(new JEEvent(JEEventType.CHARGE_USERS, this, TickSize.DAY.getTickInMillis(), simulatedDays + 1 ));
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
					for (Request request : list) {
						send(parseEvent(request));
					}
					if(workloadParser.hasNext()){
						long newEventTime = getScheduler().now() + Configuration.getInstance().getParserPageSize().getTickInMillis();
						send(new JEEvent(JEEventType.READWORKLOAD, this, newEventTime, true));
					}else{
						workloadParser.close();

						//Persisting information in simulation info
						Configuration.getInstance().getSimulationInfo().addSimulatedDay();
					}
				}
				break;
			case CHARGE_USERS:
				int simulatedDays = (Integer) event.getValue()[0];
				this.monitor.chargeUsers(simulatedDays * TickSize.DAY.getTickInMillis());
				if( simulatedDays < Configuration.getInstance().getLong(SimulatorProperties.PLANNING_PERIOD) ){
					currentMonth++;
					currentMonth %= daysInMonths.length;
					
					//Persisting information in simulation info
					Configuration.getInstance().getSimulationInfo().setCurrentMonth(currentMonth);
				}
				break;
			case COLLECT_STATISTICS:
				long time = event.getScheduledTime();
				for (LoadBalancer loadBalancer : tiers) {
					loadBalancer.collectStatistics(time);
				}
				if(workloadParser.hasNext()){
					send(new JEEvent(JEEventType.COLLECT_STATISTICS, this, getScheduler().now() + Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL)));
				}
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
	public void addServer(int tier, MachineDescriptor machineDescriptor, boolean useStartUpDelay) {
		assert tiers.length > tier : "This tier not exists!";
		tiers[tier].addServer(machineDescriptor, useStartUpDelay);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeServer(int tier, MachineDescriptor machineDescriptor,
			boolean force) {
		assert tiers.length > tier : "This tier not exists!";
		tiers[tier].removeServer(machineDescriptor, force);
	}

	@Override
	public void removeServer(int tier, boolean force) {
		assert tiers.length >= tier : "This tier not exists!";
		tiers[tier].removeServer(force);
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
}