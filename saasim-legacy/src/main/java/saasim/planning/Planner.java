package saasim.planning;

import java.util.Map;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.User;
import saasim.planning.heuristic.PlanningHeuristic;
import saasim.planning.util.PlanningHeuristicFactory;
import saasim.provisioning.Monitor;
import saasim.sim.components.LoadBalancer;
import saasim.sim.components.SimpleLoadBalancerWithAdmissionControl;
import saasim.sim.core.EventScheduler;


/**
 * Represents a planner who works as a manager of capacity planning.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public class Planner {

	private Provider[] cloudProviders;
	private PlanningHeuristic planningHeuristic;
	private User[] cloudUsers;
	
	//private final String OUTUPUT_FILE = "planning.dat"; 
	
	/**
	 * Default constructor.
	 * @param scheduler {@link EventScheduler} represents a event scheduler
	 * @param monitor {@link Monitor} which works for reporting information
	 * @param loadBalancers an array containing a {@link SimpleLoadBalancerWithAdmissionControl}s for the application
	 * @param providers an array containing the providers of the application
	 * @param cloudUsers an array containing the users of the application
	 */
	public Planner(EventScheduler scheduler, Monitor monitor, LoadBalancer[] loadBalancers, 
			Provider[] providers, User[] cloudUsers) {
		this.cloudProviders = providers;
		this.planningHeuristic = PlanningHeuristicFactory.createHeuristic(scheduler, monitor, loadBalancers);
		
		this.cloudUsers = cloudUsers;
		this.verifyProperties();
	}
	
	/**
	 * Verify properties about the users and providers in the application.
	 */
	private void verifyProperties() {
		if(this.cloudUsers == null || this.cloudUsers.length == 0){
			throw new RuntimeException("Invalid users in Planner!");
		}
		if(this.cloudProviders == null || this.cloudProviders.length == 0){
			throw new RuntimeException("Invalid cloud providers in Planner!");
		}
	}

	/**
	 * Given the heuristic and the scenario data, this method is responsible for requesting the planning
	 * of the infrastructure.
	 * @return A {@link Map} representing a chosen plan. 
	 */
	public Map<MachineType, Integer> plan() {
		//Asking for a plan
		this.planningHeuristic.findPlan(cloudProviders, cloudUsers);
		
		//Persisting planning
		Map<MachineType, Integer> plan = this.planningHeuristic.getPlan(cloudUsers);
		return plan;
	}

	//private void persistPlanning(List<String> plan) {
	//	try {
	//		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OUTUPUT_FILE)));
	//		for(String data : plan){
	//			writer.write(data+"\n");
	//		}
	//		writer.close();
	//	} catch (IOException e) {
	//		e.printStackTrace();
	//	}
	//}
}
