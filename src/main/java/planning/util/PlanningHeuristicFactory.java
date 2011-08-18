package planning.util;

import planning.heuristic.PlanningHeuristic;

import commons.config.Configuration;

public enum PlanningHeuristicFactory {
	
	/**
	 * Single instance.
	 */
	INSTANCE;
	
	/**
	 * Private constructor
	 */
	private PlanningHeuristicFactory() {}
	
	/**
	 * 
	 * @param initargs 
	 * @return
	 */
	public PlanningHeuristic createHeuristic(){
		Class<?> clazz = Configuration.getInstance().getPlanningHeuristicClass();
		
		try {
			return (PlanningHeuristic) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ clazz.getCanonicalName(), e);
		}
	}

}
