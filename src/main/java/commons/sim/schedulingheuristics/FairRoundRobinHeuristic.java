/**
 * 
 */
package commons.sim.schedulingheuristics;

import java.util.ArrayList;
import java.util.List;

import commons.cloud.Request;
import commons.sim.components.Machine;

/**
 * Simple {@link SchedulingHeuristic} to choose servers in a Round Robin fashion.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class FairRoundRobinHeuristic extends AbstractSchedulingHeuristic {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5170719922662295303L;
	private int nextToUse;
	private List<Integer> allocationsPerServer;
	private int counter;
	
	/**
	 * Default constructor
	 */
	public FairRoundRobinHeuristic() {
		super();
		this.nextToUse = 0;
		this.allocationsPerServer = new ArrayList<Integer>();
		counter = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public Machine removeMachine() {
		Machine machine = super.removeMachine();
//		finishServer(server, index, servers)
		return machine;
	}

	/**
	 * As a server is finished, allocation index may be fixed!
	 */
	private void finishServer(Machine server, int index, List<Machine> servers) {
		
		index = machines.size();
		if(nextToUse == index){
			
		}
		
		if(nextToUse > index){
			nextToUse = nextToUse - 1;
		}else if(nextToUse == index && nextToUse == allocationsPerServer.size() -1){
			nextToUse = (nextToUse + 1) % allocationsPerServer.size();
		}
		this.allocationsPerServer.remove(index);
	}
	
	@Override
	public void addMachine(Machine machine) {
		super.addMachine(machine);
		int difference = machines.size() - this.allocationsPerServer.size();
		if(difference > 0){
			for(int i = this.allocationsPerServer.size(); i < machines.size(); i++){
				this.allocationsPerServer.add(i, 0);
			}
		}
	}

	// This works allocating for each machine before jumping to the next
//	@Override
//	protected Machine getNextAvailableMachine() {
//		
//		int index = nextToUse++;
//		Machine next = machines.get(index % machines.size());
//		
//		
//		if(counter++ > next.getDescriptor().getType().getRelativePower()){
//			counter = 1;
//			index = ++nextToUse;
//			next = machines.get(index % machines.size());
//		}
//		
//		return machines.get(index % machines.size());
//	}
//
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Machine getNextAvailableMachine() {
		
		int index = nextToUse++;
		
		for (int i = index % machines.size(); i < machines.size(); i = nextToUse++) {
			Machine machine = machines.get(i);
			int relativePower = machine.getDescriptor().getType().getRelativePower();
			if(relativePower < counter){
				if(i == machines.size() - 1 && relativePower + 1 < counter){
					counter++;
				}
				return machine;
			}
		}
		
		counter = 0;
		nextToUse = 0;
		return getNextAvailableMachine();
	}
}
