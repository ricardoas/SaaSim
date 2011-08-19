package planning.util;

import planning.heuristic.PlanningHeuristic;

import commons.config.Configuration;

public class PlanningHeuristicFactory {
	
	/**
	 * 
	 * @param initargs 
	 * @return
	 */
	public static PlanningHeuristic createHeuristic(){
		Class<?> clazz = Configuration.getInstance().getPlanningHeuristicClass();
		
		try {
			return (PlanningHeuristic) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ clazz.getCanonicalName(), e);
		}
	}

}
