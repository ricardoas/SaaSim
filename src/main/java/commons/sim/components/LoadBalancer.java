package commons.sim.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
	
	private final List<Machine> servers;
	
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
		this.servers = new ArrayList<Machine>(Arrays.asList(machines));
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
		for (Machine server : servers) {
			if(server.getMachineID() == serverID){
				if(force){
					migrateRequests(server);
					send(new JEEvent(JEEventType.MACHINE_TURNED_OFF, this, getScheduler().now(), server));
				}
				servers.remove(server);
				if(server != null){
					server.shutdownOnFinish();
				}
			}
			break;// not a concurrent modification because of "break" statement.
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
			servers.add((Machine) event.getValue()[0]);
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
	 * Copy of the servers list.
	 * @return the servers
	 */
	public List<Machine> getServers() {
		return new ArrayList<Machine>(servers);
	}

	public void reportRequestFinished(Request requestFinished) {
		monitor.reportRequestFinished(requestFinished);
	}
}
