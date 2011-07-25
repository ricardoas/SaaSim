package commons.sim.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import provisioning.Monitor;

import commons.cloud.Request;
import commons.config.SimulatorConfiguration;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import commons.sim.schedulingheuristics.SchedulingHeuristic;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class LoadBalancer extends JEAbstractEventHandler implements JEEventHandler{
	
	@Deprecated//FIXME not needed anymore
	public List<Machine> reservedMachinesPool;//reserved resources
	@Deprecated//FIXME not needed anymore
	public List<Machine> onDemandMachinesPool;
	
	private final List<Machine> servers;
	
	@Deprecated//FIXME move me to DPS
	private int reservedResourcesAmount;
	@Deprecated//FIXME move me to DPS
	private int onDemandResourcesLimit;
	
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
		this.servers = new ArrayList<Machine>();
		this.getServers().addAll(Arrays.asList(machines));
		
		this.reservedResourcesAmount = Integer.MAX_VALUE;
		this.onDemandResourcesLimit = Integer.MAX_VALUE;
		this.onDemandMachinesPool = new ArrayList<Machine>();
		this.reservedMachinesPool = new ArrayList<Machine>();
		this.requestsToBeProcessed = new LinkedList<Request>();
	}
	
	/**
	 * 
	 */
	public void addServer(Machine server){
		server.setLoadBalancer(this);
		send(new JEEvent(JEEventType.ADD_SERVER, this, getServerUpTime(), server));
	}
	
	private JETime getServerUpTime(){
		return getScheduler().now().plus(new JETime(SimulatorConfiguration.getInstance().getSetUpTime()));
	}
	
	/**
	 * 
	 */
	public void removeMachine(Machine server, boolean force){
		if(force){
			queueEvents(server);
		}else{
			servers.remove(server);
			server.shutdownOnFinish();
		}
	}

	private void queueEvents(Machine server) {
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
			int numberOfMachinesToAdd = heuristic.evaluateUtilization(getServers(), eventTime);
			this.manageMachines(numberOfMachinesToAdd);
			break;
		case ADD_SERVER:
			Machine newServer = (Machine) event.getValue()[0];
			getServers().add(newServer);
			for (Request queuedRequest : requestsToBeProcessed) {
				send(new JEEvent(JEEventType.NEWREQUEST, this, getScheduler().now(), queuedRequest));
			}
			break;
		case MACHINE_TURNED_OFF:
			send(new JEEvent(event, monitor));
			break;
		default:
			break;
		}
	}

	/**
	 * @param numberOfMachinesToAdd
	 */
	private void manageMachines(int numberOfMachinesToAdd) {
		if(numberOfMachinesToAdd > 0){//Adding machines
			numberOfMachinesToAdd = addReservedMachines(numberOfMachinesToAdd);
			
			//Machines are missing, add on demand resources
			if(numberOfMachinesToAdd > 0){
				int onDemandResourcesAlreadyAdded = this.getServers().size() - this.reservedResourcesAmount;
				numberOfMachinesToAdd = Math.min(numberOfMachinesToAdd, onDemandResourcesLimit - onDemandResourcesAlreadyAdded);
				for(int i = 0; i < numberOfMachinesToAdd; i++){
//					this.addMachine();
				}
			}
		}else if(numberOfMachinesToAdd < 0){//Removing unused machines
			Iterator<Machine> it = getServers().iterator();
			while(it.hasNext()){
				Machine machine = it.next();
				if(!machine.isBusy()){
					it.remove();
					if(machine.isReserved()){
						this.reservedMachinesPool.add(machine);	
					}else{
						this.onDemandMachinesPool.add(machine);
					}
				}
			}
		}
	}

	public void setOnDemandResourcesLimit(int limit) {
		this.onDemandResourcesLimit = limit;
	}

	public void addReservedResources(int amount) {
		if(amount < 0){
			throw new RuntimeException("Negative amount of resources reserved!");
		}
		this.reservedResourcesAmount = amount;
		for(int i = 0; i < amount; i++){
			this.reservedMachinesPool.add(new Machine(getScheduler(), new Random().nextLong(), true));
		}
	}

	private int addReservedMachines(int numberOfMachinesToAdd) {
		Iterator<Machine> it = this.reservedMachinesPool.iterator();
		while(it.hasNext() && numberOfMachinesToAdd > 0){
			Machine machine = it.next();
			this.getServers().add(machine);
			it.remove();
			numberOfMachinesToAdd--;
		}
		return numberOfMachinesToAdd;
	}

	/**
	 * @return the servers
	 */
	public List<Machine> getServers() {
		return servers;
	}

	public void report(Request requestFinished) {
		monitor.report(requestFinished);
	}
}
