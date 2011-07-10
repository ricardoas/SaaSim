package commons.sim;

import java.util.ArrayList;
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
	
	private List<Machine> servers;
	
	private SchedulingHeuristic heuristic;
	
	/**
	 * 
	 */
	public LoadBalancer(SchedulingHeuristic heuristic) {
		this.servers = new ArrayList<Machine>();
		this.heuristic = heuristic;
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

	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
		case NEWREQUEST:
			Request request = (Request) event.getValue()[0];
			heuristic.getNextServer(request, servers).sendRequest(request);
			break;
		case EVALUATEUTILIZATION:
			Long eventTime = (Long) event.getValue()[0];
			int numberOfMachinesToAdd = heuristic.evaluateUtilization(eventTime);
		default:
			break;
		}
	}
}
