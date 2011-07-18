package commons.sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import provisioning.Monitor;

import commons.cloud.Machine;
import commons.cloud.Request;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class LoadBalancer extends JEAbstractEventHandler implements JEEventHandler{
	
	protected List<Machine> reservedMachinesPool;//reserved resources
	protected List<Machine> onDemandMachinesPool;
	protected List<Machine> servers;//resources being used
	
	private int reservedResourcesAmount;
	private int onDemandResourcesLimit;
	
	private SchedulingHeuristic heuristic;
	private Queue<Request> requestsToBeProcessed;
	private Monitor monitor;
	
	/**
	 * @param scheduler TODO
	 * @param machine 
	 * 
	 */
	public LoadBalancer(JEEventScheduler scheduler, Monitor monitor, SchedulingHeuristic heuristic, Machine... machines) {
		super(scheduler);
		this.monitor = monitor;
		this.heuristic = heuristic;
		this.servers = new ArrayList<Machine>();
		this.servers.addAll(Arrays.asList(machines));
		
		this.reservedResourcesAmount = Integer.MAX_VALUE;
		this.onDemandResourcesLimit = Integer.MAX_VALUE;
		this.onDemandMachinesPool = new ArrayList<Machine>();
		this.reservedMachinesPool = new ArrayList<Machine>();
		this.requestsToBeProcessed = new LinkedList<Request>();
	}
	
	/**
	 * 
	 */
	public void addMachine(){
		if(this.onDemandMachinesPool.size() > 0){
			servers.add(this.onDemandMachinesPool.remove(0));
		}else{
			servers.add(new Machine(getScheduler(), new Random().nextLong()));
		}
	}
	
	/**
	 * 
	 */
	public void removeMachine(){
		servers.remove(servers.size()-1);
	}

	/**
	 * 
	 * @param request
	 */
	public void run(Request... request) {
//		heuristic.nextServer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
		case NEWREQUEST:
			Request request = (Request) event.getValue()[0];
			Machine nextServer = heuristic.getNextServer(request, servers);
			if(nextServer != null){//Reusing an existent machine
				nextServer.sendRequest(request);
			}else{//Creating a new one
				getScheduler().queueEvent(new JEEvent(JEEventType.REQUESTQUEUED, monitor, getScheduler().now(), request));
//				this.manageMachines(1);
//				Machine machine = this.servers.get(this.servers.size()-1);//Retrieving new machine added
//				machine.sendRequest(request);
			}
			break;
		case EVALUATEUTILIZATION://RANJAN Scheduler
			Long eventTime = (Long) event.getValue()[0];
			int numberOfMachinesToAdd = heuristic.evaluateUtilization(servers, eventTime);
			this.manageMachines(numberOfMachinesToAdd);
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
				int onDemandResourcesAlreadyAdded = this.servers.size() - this.reservedResourcesAmount;
				numberOfMachinesToAdd = Math.min(numberOfMachinesToAdd, onDemandResourcesLimit - onDemandResourcesAlreadyAdded);
				for(int i = 0; i < numberOfMachinesToAdd; i++){
					this.addMachine();
				}
			}
		}else if(numberOfMachinesToAdd < 0){//Removing unused machines
			Iterator<Machine> it = servers.iterator();
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

	public void initOneMachine() {
		if(this.reservedMachinesPool.size() > 0){
			int numberOfMachinesToAdd = 1;
			addReservedMachines(numberOfMachinesToAdd);
		}else{
			this.addMachine();
		}
	}

	private int addReservedMachines(int numberOfMachinesToAdd) {
		Iterator<Machine> it = this.reservedMachinesPool.iterator();
		while(it.hasNext() && numberOfMachinesToAdd > 0){
			Machine machine = it.next();
			this.servers.add(machine);
			it.remove();
			numberOfMachinesToAdd--;
		}
		return numberOfMachinesToAdd;
	}
}
