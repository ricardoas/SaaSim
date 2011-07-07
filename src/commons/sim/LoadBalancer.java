package commons.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import commons.cloud.Machine;
import commons.cloud.Request;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class LoadBalancer {
	
	private List<Machine> servers;
	
	private SchedulingHeuristic heuristic;
	
	/**
	 * 
	 */
	public LoadBalancer() {
		servers = new ArrayList<Machine>();
		heuristic = null;
	}
	
	/**
	 * 
	 */
	public void addMachine(){
		servers.add(new Machine(new Random().nextLong()));
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
}
