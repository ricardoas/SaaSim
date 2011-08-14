package planning.heuristic;

import java.util.List;
import java.util.Map;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.io.HistoryBasedWorkloadParser;

public interface PlanningHeuristic {

	public void findPlan(HistoryBasedWorkloadParser workloadParser,
			Map<String, Provider> cloudProvider, Map<User, Contract> cloudUsers);
	
	public double getEstimatedProfit(int period);
	
	public List getPlan();
}
