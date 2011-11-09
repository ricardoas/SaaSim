package commons.sim.components;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

	private long MINIMUM_NUMBER_OF_MACHINES = 1;
	
	private final int tier;
	private final List<Machine> servers;
	private final SchedulingHeuristic heuristic;
	private final Queue<Request> requestsToBeProcessed;
	private final int maxServersAllowed;
	private transient Monitor monitor;

	
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
		this.servers = new ArrayList<Machine>();
		this.requestsToBeProcessed = new LinkedList<Request>();
		
		//Checking if there are reserved resources that should be up!
//		long[][] totalReserved = Configuration.getInstance().getLong2DArray(IaaSPlanProperties.IAAS_PLAN_PROVIDER_RESERVATION);
//		if(totalReserved.length > 0){
//		MINIMUM_NUMBER_OF_MACHINES = Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_INITIAL_SERVER_PER_TIER);
//		}
	}

	/**
	 * @param useStartUpDelay 
	 * 
	 */
	public void addServer(MachineDescriptor descriptor, boolean useStartUpDelay){
		Machine server = buildMachine(descriptor);
		long serverUpTime = getScheduler().now();
		if(useStartUpDelay){
			serverUpTime = serverUpTime + (Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SETUP_TIME));
		}
		send(new JEEvent(JEEventType.ADD_SERVER, this, serverUpTime, server));
	}
	
	/**
	 * @param machineDescriptor
	 * @return
	 */
	private Machine buildMachine(MachineDescriptor machineDescriptor) {
		return new TimeSharedMachine(getScheduler(), machineDescriptor, this);
	}
	
	/**
	 * 
	 */
	public void removeServer(MachineDescriptor descriptor, boolean force){
		for (int i = 0; i < servers.size(); i++) {
			Machine server = servers.get(i);
			if(server.getDescriptor().equals(descriptor)){
				if(force){
					migrateRequests(server);
					send(new JEEvent(JEEventType.MACHINE_TURNED_OFF, this, getScheduler().now(), server));
				}
				servers.remove(server);
				server.shutdownOnFinish();
				heuristic.finishServer(server, i, servers);
				break;// not a concurrent modification because of "break" statement.
			}
		}
	}

	/**
	 * @param server
	 */
	private void migrateRequests(Machine server) {
		long now = getScheduler().now();
		for (Request request : server.getProcessorQueue()) {
			request.reset();
			send(new JEEvent(JEEventType.NEWREQUEST, this, now, request));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
			case NEWREQUEST:
				Request request = (Request) event.getValue()[0];
				Machine nextServer = heuristic.getNextServer(request, getServers());
				if(nextServer != null){//Reusing an existent machine
					nextServer.sendRequest(request);
				}else{
//					System.out.println("Unavailable server!");
					monitor.requestQueued(getScheduler().now(), request, tier);
				}
				break;
			case ADD_SERVER:
				Machine machine = (Machine) event.getValue()[0];
				machine.getDescriptor().setStartTimeInMillis(getScheduler().now());
				servers.add(machine);
				
				this.heuristic.updateServers(servers);
				
				for (Request queuedRequest : requestsToBeProcessed) {
					send(new JEEvent(JEEventType.NEWREQUEST, this, getScheduler().now(), queuedRequest));
				}
				break;
			case MACHINE_TURNED_OFF:
				monitor.machineTurnedOff((MachineDescriptor)event.getValue()[0]);
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
		MachineStatistics statistics = new MachineStatistics(0, 0, 0, servers.size());
		monitor.sendStatistics(eventTime, statistics, tier);
	}

	/**
	 * This method is used to collect statistics of current running servers. Such statistics include: machine utilisation, number of
	 * requests that arrived, number of finished requests and current number of servers. 
	 * @param eventTime
	 */
	public void collectStatistics(long eventTime) {
		double averageUtilisation = 0d;
		for(Machine machine : servers){
			averageUtilisation += machine.computeUtilisation(eventTime);
		}
		
		if(!servers.isEmpty()){
			averageUtilisation /= servers.size();
		}
		
		long requestsArrivalCounter = this.heuristic.getRequestsArrivalCounter();
		long finishedRequestsCounter = this.heuristic.getFinishedRequestsCounter();
		this.heuristic.resetCounters();
		
		MachineStatistics statistics = new MachineStatistics(averageUtilisation, requestsArrivalCounter, finishedRequestsCounter, servers.size());
		monitor.sendStatistics(eventTime, statistics, tier);
	}

	/**
	 * Copy of the servers list.
	 * @return the servers
	 */
	public List<Machine> getServers() {
		return new ArrayList<Machine>(servers);
	}

	public void reportRequestQueued(Request requestQueued){
		monitor.requestQueued(getScheduler().now(), requestQueued, tier);
	}
	
	public void reportRequestFinished(Request requestFinished) {
		
//		if(getScheduler().now() - requestFinished.getArrivalTimeInMillis() > 
//				Configuration.getInstance().getLong(SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME)){
////			System.out.println("SLA!");
//			monitor.requestQueued(getScheduler().now(), requestFinished, tier);
//		}else{
		heuristic.reportRequestFinished();
		monitor.requestFinished(requestFinished);
//		}
		
	}

	public void removeServer(boolean force) {
		if(servers.size() <= MINIMUM_NUMBER_OF_MACHINES){
			return;
		}
//		if(servers.size() == 1){
//			return;
//		}
		
		for (int i = servers.size()-1; i >= 0; i--) {
			MachineDescriptor descriptor = servers.get(i).getDescriptor();
			if(!descriptor.isReserved()){
				removeServer(descriptor, force);
				return;
			}
		}
		removeServer(servers.get(servers.size()-1).getDescriptor(), force);
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

	public void cancelServerRemoval(int numberOfMachines) {
		//FIXME code me, please!!!
	}
}
