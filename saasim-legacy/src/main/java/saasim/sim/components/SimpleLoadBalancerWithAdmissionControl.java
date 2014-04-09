 package saasim.sim.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saasim.cloud.Request;
import saasim.config.Configuration;
import saasim.provisioning.Monitor;
import saasim.sim.ServiceEntry;
import saasim.sim.core.AbstractEventHandler;
import saasim.sim.core.Event;
import saasim.sim.core.EventScheduler;
import saasim.sim.core.EventType;
import saasim.sim.core.HandlingPoint;
import saasim.sim.schedulingheuristics.AbstractSchedulingHeuristic;
import saasim.sim.schedulingheuristics.Statistics;
import saasim.sim.schedulingheuristics.SchedulingHeuristic;
import saasim.sim.util.SaaSAppProperties;
import saasim.util.TimeUnit;


/**
 * Tier load balancer.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.0
 */
public class SimpleLoadBalancerWithAdmissionControl extends AbstractEventHandler implements LoadBalancer{
	
	/**
	 * Version 1.0
	 */
	private static final long serialVersionUID = -8572489707494357108L;

	private final int tier;

	private final SchedulingHeuristic heuristic;
	
	private final int maxServersAllowed;
	
	private Monitor monitor;

	private Map<MachineDescriptor, Machine> startingUp;

	private ServiceEntry sentry;

	private double threshold;

	/**
	 * Default constructor.
	 * @param scheduler Event scheduler.
	 * @param monitor 
	 * @param heuristic {@link SchedulingHeuristic}
	 * @param maxServersAllowed Max number of servers to manage in this layer.
	 * @param tier the tier of this {@link SimpleLoadBalancerWithAdmissionControl} represents
	 */
	public SimpleLoadBalancerWithAdmissionControl(EventScheduler scheduler, Monitor monitor, SchedulingHeuristic heuristic, int maxServersAllowed, int tier) {
		super(scheduler);
		this.monitor = monitor;
		this.heuristic = heuristic;
		this.maxServersAllowed = maxServersAllowed;
		this.tier = tier;
		this.startingUp = new HashMap<MachineDescriptor, Machine>();
		this.threshold = Integer.MAX_VALUE;
	}

	public SimpleLoadBalancerWithAdmissionControl(EventScheduler scheduler, ServiceEntry entry, Monitor monitor, SchedulingHeuristic heuristic, int maxServersAllowed, int tier) {
		super(scheduler);
		this.sentry = entry;
		this.monitor = monitor;
		this.heuristic = heuristic;
		this.maxServersAllowed = maxServersAllowed;
		this.tier = tier;
		this.startingUp = new HashMap<MachineDescriptor, Machine>();
		this.threshold = Integer.MAX_VALUE;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.sim.components.LoadBalancer#addMachine(saasim.sim.components.MachineDescriptor, boolean)
	 */
	public void addMachine(MachineDescriptor descriptor, boolean useStartUpDelay){
		Machine machine = buildMachine(descriptor);
		long machineUpTime = now();
		if(useStartUpDelay){
			machineUpTime = machineUpTime + (Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SETUP_TIME));
		}
		
		startingUp.put(descriptor, machine);
		send(new Event(EventType.ADD_SERVER, this, machineUpTime, descriptor));
	}
	
	/**
	 * Builds a {@link MachineDescriptor} creating new {@link TimeSharedMachine}. 
	 * @param machineDescriptor the machine to be build
	 * @return a {@link Machine}
	 */
	private Machine buildMachine(MachineDescriptor machineDescriptor) {
		return new TimeSharedMachine(getScheduler(), machineDescriptor, this);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.sim.components.LoadBalancer#handleNewRequest(saasim.cloud.Request)
	 */
	@HandlingPoint(EventType.NEWREQUEST)
	public void handleNewRequest(Request request){
		Machine nextServer = heuristic.next(request);
		if(nextServer != null){//Reusing an existent machine
			nextServer.sendRequest(request);
		}else{
			monitor.requestQueued(now(), request, tier);
		}
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.sim.components.LoadBalancer#registerDrop(saasim.cloud.Request)
	 */
	public void registerDrop(Request request){
		((AbstractSchedulingHeuristic)heuristic).tierStatistics.updateInterarrivalTime(request.getArrivalTimeInMillis());
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.sim.components.LoadBalancer#serverIsUp(saasim.sim.components.MachineDescriptor)
	 */
	@HandlingPoint(EventType.ADD_SERVER)
	public void serverIsUp(MachineDescriptor descriptor){
		Machine machine = startingUp.remove(descriptor);
		if(machine != null){
			descriptor.setStartTimeInMillis(now());
			heuristic.addMachine(machine);
			config(threshold);
		}
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.sim.components.LoadBalancer#serverIsDown(saasim.sim.components.MachineDescriptor)
	 */
	@HandlingPoint(EventType.MACHINE_TURNED_OFF)
	public void serverIsDown(MachineDescriptor descriptor){
		monitor.machineTurnedOff(descriptor);
		config(threshold);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.sim.components.LoadBalancer#requestWasQueued(saasim.cloud.Request)
	 */
	@HandlingPoint(EventType.REQUESTQUEUED)
	public void requestWasQueued(Request request) {
		monitor.requestQueued(now(), request, tier);
	}
	
	/**
	 * This method is called when the optimal provisioning system is used. It is used to collect current amount of servers being used
	 * by each load balancer.
	 * @param eventTime the time of event
	 */
	public void estimateServers(long eventTime) {
		Statistics statistics = new Statistics(0, 0, 0, heuristic.getNumberOfMachines());
		monitor.sendStatistics(eventTime, statistics, tier);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.sim.components.LoadBalancer#collectStatistics(long, long, int, int)
	 */
	public void collectStatistics(long now, long timeInterval, int numberOfRequests, int peakArrivalRate) {
		Statistics statistics = heuristic.getStatistics(now);
		statistics.requestArrivals += numberOfRequests;
		statistics.observationPeriod = timeInterval / TimeUnit.SECOND.getMillis();
		statistics.peakArrivalRate = peakArrivalRate;
		statistics.startingUpServers = startingUp.size();
		monitor.sendStatistics(now, statistics, tier);
	}

	/**
	 * Copy of the servers list.
	 * @return the servers
	 */
	public List<Machine> getServers() {
		return new ArrayList<Machine>(heuristic.getMachines());
	}
	
	/**
	 * Gets the equivalent tier of this {@link SimpleLoadBalancerWithAdmissionControl}.
	 * @return the tier
	 */
	public int getTier() {
		return tier;
	}

	/**
	 * Sets the monitor of the application
	 * @param monitor the monitor to set
	 * @param sentry TODO
	 * @param sentry 
	 */
	@Deprecated
	public void setMonitor(Monitor monitor, ServiceEntry sentry) {
		this.monitor = monitor;
		this.sentry = sentry;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.sim.components.LoadBalancer#reportRequestQueued(saasim.cloud.Request)
	 */
	public void reportRequestQueued(Request requestQueued){
		monitor.requestQueued(now(), requestQueued, tier);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.sim.components.LoadBalancer#reportRequestFinished(saasim.cloud.Request)
	 */
	public void reportRequestFinished(Request requestFinished) {
		heuristic.reportFinishedRequest(requestFinished);
		monitor.requestFinished(requestFinished);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.sim.components.LoadBalancer#removeMachine(saasim.sim.components.MachineDescriptor, boolean)
	 */
	public void removeMachine(MachineDescriptor descriptor, boolean force) {
		
		assert descriptor != null: "Can't remove null descriptor.";
		
		Machine machine = heuristic.removeMachine(descriptor);
		if(machine != null){
			if(force){
				machine.shutdownNow();
			}else{
				machine.shutdownOnFinish();
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.sim.components.LoadBalancer#config(double)
	 */
	public void config(double threshold){
		if(threshold > 0){
			this.threshold = threshold;
			sentry.config((int)Math.floor(threshold * heuristic.getNumberOfMachines()));
		}
	}
}
