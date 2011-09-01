package planning.heuristic;

import java.util.List;
import java.util.Map;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.io.HistoryBasedWorkloadParser;

public interface PlanningHeuristic {

	public void findPlan(HistoryBasedWorkloadParser workloadParser,
			List<Provider> cloudProviders, List<User> cloudUsers);
	
	public double getEstimatedProfit(int period);
	
	public Map<MachineType, Integer> getPlan(List<User> cloudUsers);
}
