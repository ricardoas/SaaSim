package commons.sim.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import provisioning.Monitor;

import commons.cloud.Request;
import commons.config.SimulatorConfiguration;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import commons.sim.provisioningheuristics.RanjanStatistics;
import commons.sim.schedulingheuristics.SchedulingHeuristic;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class LoadBalancer extends JEAbstractEventHandler implements JEEventHandler{
	
	private final Map<Long, Machine> servers;
	
	private SchedulingHeuristic heuristic;
	private Queue<Request> requestsToBeProcessed;
	private Monitor monitor;
	
	private final int maxServersAllowed;
	
	/**
	 * @param scheduler TODO
	 * @param machine 
	 * 
	 */
	public LoadBalancer(JEEventScheduler scheduler, Monitor monitor, SchedulingHeuristic heuristic, int maxServersAllowed, Machine... machines) {
		super(scheduler);
		this.monitor = monitor;
		this.heuristic = heuristic;
		this.maxServersAllowed = maxServersAllowed;
		this.servers = new HashMap<Long, Machine>();
		this.getServers().addAll(Arrays.asList(machines));
		
		this.requestsToBeProcessed = new LinkedList<Request>();
	}
	
	/**
	 * 
	 */
	public void addServer(Machine server){
		server.setLoadBalancer(this);
		JETime serverUpTime = getScheduler().now().plus(new JETime(SimulatorConfiguration.getInstance().getSetUpTime()));
		send(new JEEvent(JEEventType.ADD_SERVER, this, serverUpTime, server));
	}
	
	/**
	 * 
	 */
	public void removeServer(long serverID, boolean force){
		if(force){
			Machine server = servers.get(serverID);
			migrateRequests(server);
			send(new JEEvent(JEEventType.MACHINE_TURNED_OFF, this, getScheduler().now(), server));
		}
		Machine server = servers.remove(serverID);
		if(server != null){
			server.shutdownOnFinish();
		}
	}

	/**
	 * @param server
	 */
	private void migrateRequests(Machine server) {
		JETime now = getScheduler().now();
		for (Request request : server.getQueue()) {
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
				send(new JEEvent(JEEventType.REQUESTQUEUED, monitor, getScheduler().now(), request));
			}
			break;
		case EVALUATEUTILIZATION://RANJAN Scheduler
			Long eventTime = (Long) event.getValue()[0];
			
			RanjanStatistics statistics = this.collectStatistics(getServers(), eventTime);
			send(new JEEvent(JEEventType.EVALUATEUTILIZATION, this.monitor, getScheduler().now(), statistics));

			break;
		case ADD_SERVER:
			Machine newServer = (Machine) event.getValue()[0];
			getServers().add(newServer);
			for (Request queuedRequest : requestsToBeProcessed) {
				send(new JEEvent(JEEventType.NEWREQUEST, this, getScheduler().now(), queuedRequest));
			}
			break;
		case MACHINE_TURNED_OFF:
			forward(event, monitor);
			break;
		default:
			break;
		}
	}

	private RanjanStatistics collectStatistics(List<Machine> servers, long eventTime) {
		
		//Gathering total utilization
		double totalUtilization = 0d;
		for(Machine machine : servers){
			totalUtilization += machine.computeUtilization(eventTime);
		}
		
		long totalRequestsArrivals = 0;
		long totalRequestsCompletions = 0;
		
		for(Machine machine : servers){
			totalRequestsArrivals += machine.getNumberOfRequestsArrivalsInPreviousInterval();
			totalRequestsCompletions += machine.getNumberOfRequestsCompletionsInPreviousInterval();
			machine.resetCounters();
		}
		
		return new RanjanStatistics(totalUtilization, totalRequestsArrivals, totalRequestsCompletions, servers.size());
	}

	/**
	 * @param numberOfMachinesToAdd
	 */
//	private void manageMachines(int numberOfMachinesToAdd) {
//		if(numberOfMachinesToAdd > 0){//Adding machines
//			numberOfMachinesToAdd = addReservedMachines(numberOfMachinesToAdd);
//			
//			//Machines are missing, add on demand resources
//			if(numberOfMachinesToAdd > 0){
//				int onDemandResourcesAlreadyAdded = this.getServers().size() - this.reservedResourcesAmount;
//				numberOfMachinesToAdd = Math.min(numberOfMachinesToAdd, onDemandResourcesLimit - onDemandResourcesAlreadyAdded);
//				for(int i = 0; i < numberOfMachinesToAdd; i++){
////					this.addMachine();
//				}
//			}
//		}else if(numberOfMachinesToAdd < 0){//Removing unused machines
//			Iterator<Machine> it = getServers().iterator();
//			while(it.hasNext()){
//				Machine machine = it.next();
//				if(!machine.isBusy()){
//					it.remove();
//					if(machine.isReserved()){
//						this.reservedMachinesPool.add(machine);	
//					}else{
//						this.onDemandMachinesPool.add(machine);
//					}
//				}
//			}
//		}
//	}
//
//	public void addReservedResources(int amount) {
//		if(amount < 0){
//			throw new RuntimeException("Negative amount of resources reserved!");
//		}
//		this.reservedResourcesAmount = amount;
//		for(int i = 0; i < amount; i++){
//			this.reservedMachinesPool.add(new Machine(getScheduler(), new Random().nextLong(), true));
//		}
//	}
//
//	private int addReservedMachines(int numberOfMachinesToAdd) {
//		Iterator<Machine> it = this.reservedMachinesPool.iterator();
//		while(it.hasNext() && numberOfMachinesToAdd > 0){
//			Machine machine = it.next();
//			this.getServers().add(machine);
//			it.remove();
//			numberOfMachinesToAdd--;
//		}
//		return numberOfMachinesToAdd;
//	}

	/**
	 * @return the servers
	 */
	public List<Machine> getServers() {
		return new ArrayList<Machine>(servers.values());
	}

	public void reportRequestFinished(Request requestFinished) {
		monitor.reportRequestFinished(requestFinished);
	}
}
