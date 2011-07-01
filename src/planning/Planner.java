package planning;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import planning.heuristic.AGHeuristic;
import planning.heuristic.PlanningHeuristic;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;
import commons.cloud.UtilityFunction;
import config.GEISTMonthlyWorkloadParser;

public class Planner {

	private Map<String, Provider> cloudProvider;
	private PlanningHeuristic planningHeuristic;
	private Map<User, Contract> cloudUsers;
	private GEISTMonthlyWorkloadParser workloadParser;
	
	private UtilityFunction utilityFunction;
	
	public Planner(Map<String, Provider> providers, String heuristic, Map<User, Contract> cloudUsers, GEISTMonthlyWorkloadParser workloadParser) {
		this.cloudProvider = providers;
		this.planningHeuristic = new AGHeuristic();
		
		this.cloudUsers = cloudUsers;
		this.workloadParser = workloadParser;
		
		this.utilityFunction = new UtilityFunction();
	}
	
	/**
	 * Given the heuristic and the scenario data, this method is responsible for requesting the planning
	 * of the infrastructure
	 */
	public void plan() {
		try {
			Map<User, List<Request>> currentWorkload = this.workloadParser.next();
			while(!currentWorkload.isEmpty()){
				this.planningHeuristic.findPlan(currentWorkload, cloudProvider, cloudUsers);
				//TODO
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
