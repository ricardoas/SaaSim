package saasim.planning.heuristic;

import java.util.Map;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.User;


/**
 * Interface to represent a generic planning heuristic and it commons features.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public interface PlanningHeuristic {

	/**
	 * Finds the plan to use in the application.
	 * @param cloudProviders the {@link Provider}s in the application
	 * @param cloudUsers the {@link User}s in the application
	 */
	public void findPlan(Provider[] cloudProviders, User[] cloudUsers);
	
	/**
	 * Gets the estimated profit of the {@link PlanningHeuristic}.
	 * @param period the profit period
	 * @return the profit
	 */
	public double getEstimatedProfit(int period);
	
	/**
	 * Gets the plan from the users.
	 * @param cloudUsers the {@link User}s of application
	 * @return a representation of the plan
	 */
	public Map<MachineType, Integer> getPlan(User[] cloudUsers);
}
