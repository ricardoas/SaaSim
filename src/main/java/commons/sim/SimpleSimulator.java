package commons.sim;

import java.io.IOException;
import java.util.List;

import provisioning.Monitor;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.io.TickSize;
import commons.io.WorkloadParser;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
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
	
	protected static final long MONITOR_INTERVAL = Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL);
	protected static final long PARSER_PAGE_SIZE = Configuration.getInstance().getParserPageSize().getTickInMillis();
	public static int[] daysInMonths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	
	protected int currentMonth;

	protected WorkloadParser<List<Request>> workloadParser;
	
	private LoadBalancer tiers [];
	
	protected final Monitor monitor;

	/**
	 * Constructor
	 * @param list 
	 * @throws IOException 
	 */
	public SimpleSimulator(JEEventScheduler scheduler, Monitor monitor, LoadBalancer... tiers){
		super(scheduler);
		this.monitor = monitor;
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
			send(new JEEvent(JEEventType.COLLECT_STATISTICS, this, getScheduler().now() + MONITOR_INTERVAL));
			
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
						long newEventTime = getScheduler().now() + PARSER_PAGE_SIZE;
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
					send(new JEEvent(JEEventType.COLLECT_STATISTICS, this, getScheduler().now() + MONITOR_INTERVAL));
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
	
	@Override
	public void addServer(int tier, Machine machine) {
		this.tiers[tier].addServer(machine);
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
		assert tiers.length > tier : "This tier not exists!";
		tiers[tier].removeServer(force);
	}

	/**
	 * @return
	 */
	public LoadBalancer[] getTiers() {
		return this.tiers;
	}

	public WorkloadParser<List<Request>> getParser() {
		return this.workloadParser;
	}
}