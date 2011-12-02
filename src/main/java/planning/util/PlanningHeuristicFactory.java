package planning.util;

import planning.heuristic.AGHeuristic;
import planning.heuristic.HistoryBasedHeuristic;
import planning.heuristic.OptimalHeuristic;
import planning.heuristic.OverProvisionHeuristic;
import planning.heuristic.PlanningHeuristic;
import provisioning.Monitor;

import commons.config.Configuration;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEEventScheduler;

/**
 * Factory to encapsulate {@link PlanningHeuristic} creation. A planning heuristic can be {@link AGHeuristic},
 * {@link HistoryBasedHeuristic}, {@link OptimalHeuristic} or {@link OverProvisionHeuristic}.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public class PlanningHeuristicFactory {
	
	/**
	 * Creates a specific {@link PlanningHeuristic}.
	 * @param scheduler {@link JEEventScheduler} an event scheduler.
	 * @param monitor {@link Monitor} to reporting information
	 * @param loadBalancers an array of {@link LoadBalancer} to be used in the application
	 * @return A builded {@link PlanningHeuristic} recovered from a configuration instance.
	 */
	public static PlanningHeuristic createHeuristic(JEEventScheduler scheduler, Monitor monitor, LoadBalancer[] loadBalancers){
		Class<?> clazz = Configuration.getInstance().getPlanningHeuristicClass();
		
		try {
			return (PlanningHeuristic) clazz.getDeclaredConstructors()[0].newInstance(scheduler, monitor, loadBalancers);
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ clazz.getCanonicalName(), e);
		}
	}
}
