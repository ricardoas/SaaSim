package planning.heuristic;

import java.util.List;
import java.util.Map;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;

public class AGHeuristic implements PlanningHeuristic{

	@Override
	public void findPlan(Map<User, List<Request>> currentWorkload,
			Map<String, Provider> cloudProvider, Map<User, Contract> cloudUsers) {
		//TODO Create config and genes
		//evaluated population
		//store best config
	}

	@Override
	public double getEstimatedProfit() {
		//TODO return profit achieved by best config
		return 0;
	}

}
