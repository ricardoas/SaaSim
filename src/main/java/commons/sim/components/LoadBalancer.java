package commons.sim.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

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
	
	private final Queue<Request> requestsToBeProcessed;
	private final int maxServersAllowed;
	private transient Monitor monitor;

	private Map<MachineDescriptor, Machine> startingUp;
	private Map<MachineDescriptor, Machine> warmingDown;

	
	/**
	 * Default constructor.
	 * @param scheduler Event scheduler.
	 * @param heuristic {@link SchedulingHeuristic}
	 * @param maxServersAllowed Max number of servers to manage in this layer.
	 * @param machines An initial collection of {@link Machine}s.
	 */
	public LoadBalancer(JEEventScheduler scheduler, SchedulingHeuristic heuristic, int maxServersAllowed, int tier) {
		super(scheduler);
		this.heuristic = heuristic;
		this.maxServersAllowed = maxServersAllowed;
		this.tier = tier;
		this.requestsToBeProcessed = new LinkedList<Request>();
		startingUp = new HashMap<MachineDescriptor, Machine>();
		warmingDown = new HashMap<MachineDescriptor, Machine>();
		
	}

	/**
	 * 
	 * @param useStartUpDelay 
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
	 * @param machineDescriptor
	 * @return
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
					
					for (Request queuedRequest : requestsToBeProcessed) {
						send(new JEEvent(JEEventType.NEWREQUEST, this, getScheduler().now(), queuedRequest));
					}
				}
				break;
			case MACHINE_TURNED_OFF:
				Machine machineToTurnOff = warmingDown.remove(event.getValue()[0]);
				if(machineToTurnOff != null){
					monitor.machineTurnedOff((MachineDescriptor)event.getValue()[0]);
				}
				
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
	 * @param eventTime
	 */
	public void estimateServers(long eventTime) {
		MachineStatistics statistics = new MachineStatistics(0, 0, 0, heuristic.getNumberOfMachines());
		monitor.sendStatistics(eventTime, statistics, tier);
	}

	/**
	 * This method is used to collect statistics of current running servers. Such statistics include: machine utilisation, number of
	 * requests that arrived, number of finished requests and current number of servers. 
	 * @param now
	 * @param timeInterval TODO
	 * @param numberOfRequests Total number of requests submitted to the system (A<sub>0</sub>)
	 */
	public void collectStatistics(long now, long timeInterval, int numberOfRequests) {
		MachineStatistics statistics = heuristic.getStatistics(now);
		statistics.numberOfRequestsArrivalInLastInterval = numberOfRequests;
		statistics.observationPeriod = timeInterval;
		monitor.sendStatistics(now, statistics, tier);
	}

	/**
	 * Copy of the servers list.
	 * @return the servers
	 */
	public List<Machine> getServers() {
		return new ArrayList<Machine>(heuristic.getMachines());
	}

	public void reportRequestQueued(Request requestQueued){
		monitor.requestQueued(getScheduler().now(), requestQueued, tier);
	}
	
	public void reportRequestFinished(Request requestFinished) {
		
		heuristic.reportFinishedRequest(requestFinished);
		monitor.requestFinished(requestFinished);
	}

	public void removeMachine(boolean force) {
		Machine machine = null;
		
		if(startingUp.isEmpty()){
			machine = heuristic.removeMachine();
			warmingDown.put(machine.getDescriptor(), machine);
			if(force){
				//FIXME what can we do with running requests?
//				send(new JEEvent(JEEventType.MACHINE_TURNED_OFF, this, getScheduler().now(), machine));
				throw new RuntimeException("Not implemented");
			}else{
				machine.shutdownOnFinish();
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

	public int getTier() {
		return tier;
	}

	/**
	 * @param monitor the monitor to set
	 */
	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
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
	 */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public void cancelMachineRemoval(int numberOfMachines) {
		Iterator<Entry<MachineDescriptor, Machine>> iterator = warmingDown.entrySet().iterator();
		for (int i = 0; i < numberOfMachines; i++) {
			Entry<MachineDescriptor, Machine> entry = iterator.next();
			iterator.remove();
			entry.getValue().cancelShutdown();
			heuristic.addMachine(entry.getValue());
		}
	}
}
