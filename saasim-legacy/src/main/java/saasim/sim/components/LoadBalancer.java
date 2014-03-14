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
public class LoadBalancer extends AbstractEventHandler{
	
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
	 * @param tier the tier of this {@link LoadBalancer} represents
	 */
	public LoadBalancer(EventScheduler scheduler, Monitor monitor, SchedulingHeuristic heuristic, int maxServersAllowed, int tier) {
		super(scheduler);
		this.monitor = monitor;
		this.heuristic = heuristic;
		this.maxServersAllowed = maxServersAllowed;
		this.tier = tier;
		this.startingUp = new HashMap<MachineDescriptor, Machine>();
		this.threshold = Integer.MAX_VALUE;
	}

	public LoadBalancer(EventScheduler scheduler, ServiceEntry entry, Monitor monitor, SchedulingHeuristic heuristic, int maxServersAllowed, int tier) {
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
	 * Add a new {@link MachineDescriptor} to this tier.
	 * @param useStartUpDelay <code>true</code> if use start up delay
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
	
	@HandlingPoint(EventType.NEWREQUEST)
	public void handleNewRequest(Request request){
		Machine nextServer = heuristic.next(request);
		if(nextServer != null){//Reusing an existent machine
			nextServer.sendRequest(request);
		}else{
			monitor.requestQueued(now(), request, tier);
		}
	}
	
	public void registerDrop(Request request){
		((AbstractSchedulingHeuristic)heuristic).tierStatistics.updateInterarrivalTime(request.getArrivalTimeInMillis());
	}
	
	@HandlingPoint(EventType.ADD_SERVER)
	public void serverIsUp(MachineDescriptor descriptor){
		Machine machine = startingUp.remove(descriptor);
		if(machine != null){
			descriptor.setStartTimeInMillis(now());
			heuristic.addMachine(machine);
			config(threshold);
		}
	}
	
	@HandlingPoint(EventType.MACHINE_TURNED_OFF)
	public void serverIsDown(MachineDescriptor descriptor){
		monitor.machineTurnedOff(descriptor);
		config(threshold);
	}
	
	/**
	 * {@inheritDoc}
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
	 * This method is used to collect statistics of current running servers. Such statistics include: machine utilisation, number of
	 * requests that arrived, number of finished requests and current number of servers. 
	 * @param now the actual time
	 * @param timeInterval TODO the interval to collect statistics
	 * @param numberOfRequests total number of requests submitted to the system (A<sub>0</sub>)
	 * @param peakArrivalRate Peak arrival rate during last session
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
	 * Gets the equivalent tier of this {@link LoadBalancer}.
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
	 * Report when a specific {@link Request} goes to queue.
	 * @param requestQueued {@link Request} queued
	 */
	public void reportRequestQueued(Request requestQueued){
		monitor.requestQueued(now(), requestQueued, tier);
	}

	/**
	 * Report when a specific {@link Request} has been finished.
	 * @param requestFinished the {@link Request} has been finished
	 */
	public void reportRequestFinished(Request requestFinished) {
		heuristic.reportFinishedRequest(requestFinished);
		monitor.requestFinished(requestFinished);
	}

	/**
	 * Remove a specific {@link Machine} to this tier.
	 * @param force <code>true</code> if use force
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
	
	public void config(double threshold){
		if(threshold > 0){
			this.threshold = threshold;
			sentry.config((int)Math.floor(threshold * heuristic.getNumberOfMachines()));
		}
	}
}
