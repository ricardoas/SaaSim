package planning.heuristic;

import java.util.List;
import java.util.Map;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.Request;
import commons.cloud.User;

public interface PlanningHeuristic {

	public void findPlan(Map<User, List<Request>> currentWorkload,
			Map<String, Provider> cloudProvider, Map<User, Contract> cloudUsers, double sla);
	
	public double getEstimatedProfit(int period);
	
	public List getPlan();
}
