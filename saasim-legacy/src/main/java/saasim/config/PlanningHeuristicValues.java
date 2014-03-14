package saasim.config;

import saasim.planning.heuristic.AGHeuristic;
import saasim.planning.heuristic.HistoryBasedHeuristic;
import saasim.planning.heuristic.OptimalHeuristic;
import saasim.planning.heuristic.OverProvisionHeuristic;
import saasim.planning.heuristic.PlanningHeuristic;

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
