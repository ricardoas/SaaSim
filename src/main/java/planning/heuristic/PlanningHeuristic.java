package planning.heuristic;

import java.util.Map;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.User;

public interface PlanningHeuristic {

	public void findPlan(Provider[] cloudProviders, User[] cloudUsers);
	
	public double getEstimatedProfit(int period);
	
	public Map<MachineType, Integer> getPlan(User[] cloudUsers);
}
