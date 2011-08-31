package planning.heuristic;

import java.util.List;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.io.HistoryBasedWorkloadParser;

public class HistoryBasedHeuristic implements PlanningHeuristic{

	@Override
	public void findPlan(HistoryBasedWorkloadParser workloadParser,
			List<Provider> cloudProviders, List<User> cloudUsers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getEstimatedProfit(int period) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> getPlan(List<User> cloudUsers) {
		// TODO Auto-generated method stub
		return null;
	}

}
