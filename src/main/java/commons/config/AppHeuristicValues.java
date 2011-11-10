package commons.config;

import commons.sim.schedulingheuristics.ProfitDrivenHeuristic;
import commons.sim.schedulingheuristics.RanjanHeuristic;
import commons.sim.schedulingheuristics.RoundRobinHeuristic;
import commons.sim.schedulingheuristics.HeterogenousRRHeuristic;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum AppHeuristicValues {
	
	ROUNDROBIN(RoundRobinHeuristic.class.getCanonicalName()), 
	ROUNDROBIN_HET(HeterogenousRRHeuristic.class.getCanonicalName()),
	RANJAN(RanjanHeuristic.class.getCanonicalName()), 
	PROFITDRIVEN(ProfitDrivenHeuristic.class.getCanonicalName()),
	CUSTOM("");
	
	private final String className;

	private AppHeuristicValues(String className){
		this.className = className;
	}

	public String getClassName() {
		return className;
	}
}
