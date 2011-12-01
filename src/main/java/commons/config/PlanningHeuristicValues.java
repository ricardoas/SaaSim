package commons.config;

import planning.heuristic.AGHeuristic;
import planning.heuristic.HistoryBasedHeuristic;
import planning.heuristic.OptimalHeuristic;
import planning.heuristic.OverProvisionHeuristic;
import planning.heuristic.PlanningHeuristic;

/**
 * Planning heuristic options. 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public enum PlanningHeuristicValues {
	
	/**
	 * AGHeuristic's implementation, its based on genetic algorithm way. 
	 */
	EVOLUTIONARY(AGHeuristic.class), 
	/**
	 * OverProvisionings's algorithm implementation. 
	 */
	OVERPROVISIONING(OverProvisionHeuristic.class), 
	/**
	 * Optimal's algorithm implementation.
	 */
	OPTIMAL(OptimalHeuristic.class), 
	/**
	 * History's algorithm implementation.
	 */
	HISTORY(HistoryBasedHeuristic.class);
	
	private final Class<? extends PlanningHeuristic> clazz;

	/**
	 * Default private constructor.
	 * @param className the name of heuristic class
	 */
	private PlanningHeuristicValues(Class<? extends PlanningHeuristic> clazz){
		this.clazz = clazz;
	}

	/**
	 * Gets the heuristic class to load.
	 * @return The heuristic class to load.
	 */
	public Class<?> getClazz() {
		return clazz;
	}
}
