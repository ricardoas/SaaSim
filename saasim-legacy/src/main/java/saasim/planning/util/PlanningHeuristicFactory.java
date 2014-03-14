package saasim.planning.util;

import saasim.config.Configuration;
import saasim.planning.heuristic.AGHeuristic;
import saasim.planning.heuristic.HistoryBasedHeuristic;
import saasim.planning.heuristic.OptimalHeuristic;
import saasim.planning.heuristic.OverProvisionHeuristic;
import saasim.planning.heuristic.PlanningHeuristic;
import saasim.provisioning.Monitor;
import saasim.sim.components.LoadBalancer;
import saasim.sim.core.EventScheduler;


/**
 * Factory to encapsulate {@link PlanningHeuristic} creation. A planning heuristic can be {@link AGHeuristic},
 * {@link HistoryBasedHeuristic}, {@link OptimalHeuristic} or {@link OverProvisionHeuristic}.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public class PlanningHeuristicFactory {
	
	/**
	 * Creates a specific {@link PlanningHeuristic}.
	 * @param scheduler {@link EventScheduler} an event scheduler.
	 * @param monitor {@link Monitor} to reporting information
	 * @param loadBalancers an array of {@link LoadBalancer} to be used in the application
	 * @return A builded {@link PlanningHeuristic} recovered from a configuration instance.
	 */
	public static PlanningHeuristic createHeuristic(EventScheduler scheduler, Monitor monitor, LoadBalancer[] loadBalancers){
		Class<?> clazz = Configuration.getInstance().getPlanningHeuristicClass();
		
		try {
			return (PlanningHeuristic) clazz.getDeclaredConstructors()[0].newInstance(scheduler, monitor, loadBalancers);
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ clazz.getCanonicalName(), e);
		}
	}
}
