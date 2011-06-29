package planning;

import java.util.List;
import java.util.Map;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;

public class Planner {

	private Map<String, Provider> cloudProvider;
	private String heuristic;
	private Map<User, Contract> cloudUsers;
	private Map<String, Map<User, List<Request>>> workload;
	
	public Planner(Map<String, Provider> providers, String heuristic, Map<User, Contract> cloudUsers, Map<String, Map<User, List<Request>>> workload2) {
		this.cloudProvider = providers;
		this.heuristic = heuristic;
		this.cloudUsers = cloudUsers;
		this.workload = workload2;
	}

	public void plan() {
		// TODO Auto-generated method stub
	}

}
