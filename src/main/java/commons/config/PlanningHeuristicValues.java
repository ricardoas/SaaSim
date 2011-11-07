package commons.config;

import planning.heuristic.AGHeuristic;
import planning.heuristic.HistoryBasedHeuristic;
import planning.heuristic.OptimalHeuristic;
import planning.heuristic.OverProvisionHeuristic;

/**
 * @author David Candeia
 */
public enum PlanningHeuristicValues {
	
	EVOLUTIONARY(AGHeuristic.class.getCanonicalName()), 
	OVERPROVISIONING(OverProvisionHeuristic.class.getCanonicalName()), 
	OPTIMAL(OptimalHeuristic.class.getCanonicalName()), 
	HISTORY(HistoryBasedHeuristic.class.getCanonicalName());
	
	private final String className;

	/**
	 * Default private constructor.
	 * @param className
	 */
	private PlanningHeuristicValues(String className){
		this.className = className;
	}

	/**
	 * @return Class name to load.
	 */
	public String getClassName() {
		return className;
	}
}
