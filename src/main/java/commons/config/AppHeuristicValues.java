package commons.config;

import commons.sim.schedulingheuristics.ProfitDrivenHeuristic;
import commons.sim.schedulingheuristics.RanjanHeuristic;
import commons.sim.schedulingheuristics.RoundRobinHeuristic;
import commons.sim.schedulingheuristics.RoundRobinHeuristicForHeterogenousMachines;

public enum AppHeuristicValues {
	ROUNDROBIN(RoundRobinHeuristic.class.getCanonicalName()), 
	ROUNDROBIN_HET(RoundRobinHeuristicForHeterogenousMachines.class.getCanonicalName()),
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
