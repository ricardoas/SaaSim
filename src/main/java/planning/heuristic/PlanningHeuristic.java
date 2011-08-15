package planning.heuristic;

import java.util.List;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.io.HistoryBasedWorkloadParser;

public interface PlanningHeuristic {

	public void findPlan(HistoryBasedWorkloadParser workloadParser,
			List<Provider> cloudProviders, List<User> cloudUsers);
	
	public double getEstimatedProfit(int period);
	
	public List getPlan(List<User> cloudUsers);
}
