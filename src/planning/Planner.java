package planning;

import java.util.List;
import java.util.Map;

import cloud.CloudProvider;
import cloud.Contract;
import cloud.Request;
import cloud.User;

public class Planner {

	private Map<String, CloudProvider> cloudProvider;
	private String heuristic;
	private Map<User, Contract> cloudUsers;
	private Map<String, Map<User, List<Request>>> workload;
	
	public Planner(Map<String, CloudProvider> providers, String heuristic, Map<User, Contract> cloudUsers, Map<String, Map<User, List<Request>>> workload2) {
		this.cloudProvider = providers;
		this.heuristic = heuristic;
		this.cloudUsers = cloudUsers;
		this.workload = workload2;
	}

	public void plan() {
		// TODO Auto-generated method stub
	}

}
