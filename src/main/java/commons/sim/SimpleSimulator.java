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
	
	private static final long MONITOR_INTERVAL = Configuration.getInstance().getLong(SimulatorProperties.DPS_MONITOR_INTERVAL);
	private static final long PARSER_PAGE_SIZE = Configuration.getInstance().getParserPageSize().getTickInMillis();
	private static int[] daysInMonths = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	
	private int currentMonth = 0;

	private WorkloadParser<List<Request>> workloadParser;
	
	private LoadBalancer tiers [];
	
	private final Monitor monitor;

	/**
	 * Constructor
	 * @param scheduler TODO
	 * @param scheduler 
	 * @param list 
	 * @throws IOException 
	 */
	public SimpleSimulator(JEEventScheduler scheduler, Monitor monitor, LoadBalancer... tiers){
		super(scheduler);
		this.monitor = monitor;
		this.tiers = tiers;
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
		send(new JEEvent(JEEventType.CHARGE_USERS, this, getScheduler().now() + (TickSize.DAY.getTickInMillis() * daysInMonths[currentMonth++])));
		send(new JEEvent(JEEventType.COLLECT_STATISTICS, this, getScheduler().now() + MONITOR_INTERVAL));
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
						send(new JEEvent(JEEventType.READWORKLOAD, this, newEventTime));
					}	
				}
				break;
			case CHARGE_USERS:
				this.monitor.chargeUsers(getScheduler().now());
				if(workloadParser.hasNext()){
					long newEventTime = getScheduler().now() + (TickSize.DAY.getTickInMillis() * daysInMonths[currentMonth]);
					send(new JEEvent(JEEventType.CHARGE_USERS, this, newEventTime));
					currentMonth %= daysInMonths.length;
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
}