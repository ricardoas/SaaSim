package commons.sim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import commons.cloud.Machine;
import commons.cloud.Request;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class LoadBalancer extends JEEventHandler{
	
	protected List<Machine> reservedMachinesPool;//reserved resources
	protected List<Machine> onDemandMachinesPool;
	protected List<Machine> servers;//resources being used
	
	private int reservedResourcesAmount;
	private int onDemandResourcesLimit;
	
	private SchedulingHeuristic heuristic;
	
	/**
	 * 
	 */
	public LoadBalancer(SchedulingHeuristic heuristic) {
		this.servers = new ArrayList<Machine>();
		this.heuristic = heuristic;
		this.reservedResourcesAmount = Integer.MAX_VALUE;
		this.onDemandResourcesLimit = Integer.MAX_VALUE;
		this.onDemandMachinesPool = new ArrayList<Machine>();
		this.reservedMachinesPool = new ArrayList<Machine>();
	}
	
	/**
	 * 
	 */
	public void addMachine(){
		if(this.onDemandMachinesPool.size() > 0){
			servers.add(this.onDemandMachinesPool.remove(0));
		}else{
			servers.add(new Machine(new Random().nextLong()));
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

	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
		case NEWREQUEST:
			Request request = (Request) event.getValue()[0];
			Machine nextServer = heuristic.getNextServer(request, servers);
			if(nextServer != null){//Reusing an existent machine
				nextServer.sendRequest(request);
			}else{//Creating a new one
				this.manageMachines(1);
			}
			break;
		case EVALUATEUTILIZATION://RANJAN Scheduler
			Long eventTime = (Long) event.getValue()[0];
			int numberOfMachinesToAdd = heuristic.evaluateUtilization(servers, eventTime);
			this.manageMachines(numberOfMachinesToAdd);
		default:
			break;
		}
	}

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
			this.reservedMachinesPool.add(new Machine(new Random().nextLong(), true));
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
