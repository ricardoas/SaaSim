package planning;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import planning.heuristic.AGHeuristic;
import planning.heuristic.PlanningHeuristic;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.io.HistoryBasedWorkloadParser;

public class Planner {

	private List<Provider> cloudProviders;
	private PlanningHeuristic planningHeuristic;
	private List<User> cloudUsers;
	private HistoryBasedWorkloadParser workloadParser;
	
	private final String OUTUPUT_FILE = "planning.dat"; 
	
	public Planner(List<Provider> providers, String planningHeuristic, List<User> cloudUsers, HistoryBasedWorkloadParser workloadParser) {
		this.cloudProviders = providers;
		this.planningHeuristic = new AGHeuristic();
		
		this.cloudUsers = cloudUsers;
		this.workloadParser = workloadParser;
		this.verifyProperties();
	}
	
	private void verifyProperties() {
		if(this.cloudUsers == null || this.cloudUsers.size() == 0){
			throw new RuntimeException("Invalid users in Planner!");
		}
		if(this.cloudProviders == null || this.cloudProviders.size() == 0){
			throw new RuntimeException("Invalid cloud providers in Planner!");
		}
		if(this.workloadParser == null){
			throw new RuntimeException("Invalid workload parser in Planner!");
		}
	}

	/**
	 * Given the heuristic and the scenario data, this method is responsible for requesting the planning
	 * of the infrastructure
	 */
	public List<String> plan() {
		
		//For each month ...
		while(this.workloadParser.hasNext()){
			this.planningHeuristic.findPlan(this.workloadParser, cloudProviders, cloudUsers);
		}
		
		//Persisting planning
		List<String> plan = this.planningHeuristic.getPlan();
		persistPlanning(plan);
		
		return plan;
			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return null;
	}

	private void persistPlanning(List<String> plan) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(OUTUPUT_FILE)));
			for(String data : plan){
				writer.write(data+"\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
