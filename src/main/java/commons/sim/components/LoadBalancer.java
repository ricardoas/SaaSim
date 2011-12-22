 package commons.sim.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import provisioning.Monitor;

import commons.cloud.Request;
import commons.config.Configuration;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.provisioningheuristics.MachineStatistics;
import commons.sim.schedulingheuristics.SchedulingHeuristic;
import commons.sim.util.SaaSAppProperties;
import commons.util.TimeUnit;

/**
 * Tier load balancer.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.0
 */
public class LoadBalancer extends JEAbstractEventHandler{
	
	/**
	 * Version 1.0
	 */
	private static final long serialVersionUID = -8572489707494357108L;

	private final int tier;

	private final SchedulingHeuristic heuristic;
	
	private final int maxServersAllowed;
	private transient Monitor monitor;

	private Map<MachineDescriptor, Machine> startingUp;
	private Map<MachineDescriptor, Machine> warmingDown;

	
	/**
	 * Default constructor.
	 * @param scheduler Event scheduler.
	 * @param heuristic {@link SchedulingHeuristic}
	 * @param maxServersAllowed Max number of servers to manage in this layer.
	 * @param tier the tier of this {@link LoadBalancer} represents
	 */
	public LoadBalancer(JEEventScheduler scheduler, SchedulingHeuristic heuristic, int maxServersAllowed, int tier) {
		super(scheduler);
		this.heuristic = heuristic;
		this.maxServersAllowed = maxServersAllowed;
		this.tier = tier;
		startingUp = new HashMap<MachineDescriptor, Machine>();
		warmingDown = new HashMap<MachineDescriptor, Machine>();
		
	}

	/**
	 * Add a new {@link MachineDescriptor} to this tier.
	 * @param useStartUpDelay <code>true</code> if use start up delay
	 */
	public void addMachine(MachineDescriptor descriptor, boolean useStartUpDelay){
		Machine machine = buildMachine(descriptor);
		long machineUpTime = getScheduler().now();
		if(useStartUpDelay){
			machineUpTime = machineUpTime + (Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SETUP_TIME));
		}
		
		startingUp.put(descriptor, machine);
		send(new JEEvent(JEEventType.ADD_SERVER, this, machineUpTime, descriptor));
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
	 */
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
			case NEWREQUEST:
				Request request = (Request) event.getValue()[0];
				Machine nextServer = heuristic.next(request);
				if(nextServer != null){//Reusing an existent machine
					nextServer.sendRequest(request);
				}else{
					monitor.requestQueued(getScheduler().now(), request, tier);
				}
				break;
			case ADD_SERVER:
				MachineDescriptor descriptor = (MachineDescriptor) event.getValue()[0];
				Machine machine = startingUp.remove(descriptor);
				if(machine != null){
					descriptor.setStartTimeInMillis(getScheduler().now());
					heuristic.addMachine(machine);
				}
				break;
			case MACHINE_TURNED_OFF:
//				Machine machineToTurnOff = warmingDown.remove(event.getValue()[0]);
//				if(machineToTurnOff != null){
					monitor.machineTurnedOff((MachineDescriptor)event.getValue()[0]);
//				}
				break;
			case REQUESTQUEUED:
				monitor.requestQueued(getScheduler().now(), (Request)event.getValue()[0], tier);
				break;
			default:
				break;
		}
	}
	
	/**
	 * This method is called when the optimal provisioning system is used. It is used to collect current amount of servers being used
	 * by each load balancer.
	 * @param eventTime the time of event
	 */
	public void estimateServers(long eventTime) {
		MachineStatistics statistics = new MachineStatistics(0, 0, 0, heuristic.getNumberOfMachines());
		monitor.sendStatistics(eventTime, statistics, tier);
	}

	/**
	 * This method is used to collect statistics of current running servers. Such statistics include: machine utilisation, number of
	 * requests that arrived, number of finished requests and current number of servers. 
	 * @param now the actual time
	 * @param timeInterval TODO the interval to collect statistics
	 * @param numberOfRequests total number of requests submitted to the system (A<sub>0</sub>)
	 */
	public void collectStatistics(long now, long timeInterval, int numberOfRequests) {
		MachineStatistics statistics = heuristic.getStatistics(now);
		statistics.requestArrivals += numberOfRequests;
		statistics.observationPeriod = timeInterval / TimeUnit.SECOND.getMillis();
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
	 */
	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}

	/**
	 * Report when a specific {@link Request} goes to queue.
	 * @param requestQueued {@link Request} queued
	 */
	public void reportRequestQueued(Request requestQueued){
		//heuristic.reportFinishedRequest(requestQueued);
		monitor.requestQueued(getScheduler().now(), requestQueued, tier);
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
	@Deprecated
	public void removeMachine(boolean force) {
		Machine machine = null;
		
		if(startingUp.isEmpty()){
			machine = heuristic.removeMachine();
			if(machine != null){
				warmingDown.put(machine.getDescriptor(), machine);
				if(force){
					//FIXME what can we do with running requests?
					//send(new JEEvent(JEEventType.MACHINE_TURNED_OFF, this, getScheduler().now(), machine));
					throw new RuntimeException("Not implemented");
				}else{
					machine.shutdownOnFinish();
				}
			}
		}else{
			Iterator<Entry<MachineDescriptor, Machine>> iterator = startingUp.entrySet().iterator();
			Entry<MachineDescriptor, Machine> entry = iterator.next();
			iterator.remove();
			machine = entry.getValue();
			
			entry.getKey().setStartTimeInMillis(getScheduler().now());
			warmingDown.put(machine.getDescriptor(), machine);
			machine.shutdownOnFinish();
		}
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
				throw new RuntimeException("Not implemented yet!");
			}
			machine.shutdownOnFinish();
		}
	}
	
	/**
	 * Cancels machine's removal.
	 * @param numberOfMachines number of machines to be recovered
	 */
	public void cancelMachineRemoval(int numberOfMachines) {
		Iterator<Entry<MachineDescriptor, Machine>> iterator = warmingDown.entrySet().iterator();
		for (int i = 0; i < numberOfMachines; i++) {
			Entry<MachineDescriptor, Machine> entry = iterator.next();
			iterator.remove();
			entry.getValue().cancelShutdown();
			heuristic.addMachine(entry.getValue());
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

}
