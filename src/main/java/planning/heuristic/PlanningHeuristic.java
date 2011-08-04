package planning.heuristic;

import java.util.List;
import java.util.Map;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.User;

import config.GEISTMonthlyWorkloadParser;

public interface PlanningHeuristic {

	public void findPlan(GEISTMonthlyWorkloadParser workloadParser,
			Map<String, Provider> cloudProvider, Map<User, Contract> cloudUsers);
	
	public double getEstimatedProfit(int period);
	
	public List getPlan();
}
