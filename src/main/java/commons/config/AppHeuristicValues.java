package commons.config;

import commons.sim.schedulingheuristics.FairRoundRobinHeuristic;
import commons.sim.schedulingheuristics.RanjanHeuristic;
import commons.sim.schedulingheuristics.RoundRobinHeuristic;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum AppHeuristicValues {
	
	ROUNDROBIN(RoundRobinHeuristic.class.getCanonicalName()), 
	FAIR_ROUNDROBIN(FairRoundRobinHeuristic.class.getCanonicalName()),
	RANJAN(RanjanHeuristic.class.getCanonicalName()), 
	CUSTOM("");
	
	private final String className;

	private AppHeuristicValues(String className){
		this.className = className;
	}

	public String getClassName() {
		return className;
	}
}
