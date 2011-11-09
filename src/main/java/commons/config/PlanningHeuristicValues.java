package commons.config;

import planning.heuristic.AGHeuristic;
import planning.heuristic.HistoryBasedHeuristic;
import planning.heuristic.OptimalHeuristic;
import planning.heuristic.OverProvisionHeuristic;
import planning.heuristic.PlanningHeuristic;

/**
 * @author David Candeia
 */
public enum PlanningHeuristicValues {
	
	EVOLUTIONARY(AGHeuristic.class), 
	OVERPROVISIONING(OverProvisionHeuristic.class), 
	OPTIMAL(OptimalHeuristic.class), 
	HISTORY(HistoryBasedHeuristic.class);
	
	private final Class<? extends PlanningHeuristic> clazz;

	/**
	 * Default private constructor.
	 * @param className
	 */
	private PlanningHeuristicValues(Class<? extends PlanningHeuristic> clazz){
		this.clazz = clazz;
	}

	public Class<?> getClazz() {
		return clazz;
	}
}
