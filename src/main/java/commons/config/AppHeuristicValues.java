package commons.config;

import provisioning.UrgaonkarProvisioningSystem;
import commons.sim.schedulingheuristics.FairRoundRobinHeuristic;
import commons.sim.schedulingheuristics.RanjanHeuristic;
import commons.sim.schedulingheuristics.RoundRobinHeuristic;
import commons.sim.schedulingheuristics.RoundRobinHeuristicForUrgaonkar;

/**
 * Values of application heuristic.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum AppHeuristicValues {
	/**
	 * Heuristic to choose servers in a Round Robin method.
	 */
	ROUNDROBIN(RoundRobinHeuristic.class.getCanonicalName()), 
	/**
	 * Heuristic to choose servers in a Round Robin method, specified for the fair way.
	 */
	FAIR_ROUNDROBIN(FairRoundRobinHeuristic.class.getCanonicalName()),
	/**
	 * This heuristic tries to arrange requests of a same session in the same servers.
	 */
	RANJAN(RanjanHeuristic.class.getCanonicalName()), 
	/**
	 * {@link RoundRobinHeuristic} strategy collecting statistics for {@link UrgaonkarProvisioningSystem}.
	 */
	ROUNDROBIN_U(RoundRobinHeuristicForUrgaonkar.class.getCanonicalName()), 
	/**
	 * 
	 */
	CUSTOM("");
	
	private final String className;
	
	/**
	 * Default private constructor.
	 * @param className the name of heuristic class.
	 */
	private AppHeuristicValues(String className){
		this.className = className;
	}

	/**
	 * Gets the name of heuristic class.
	 * @return The name of heuristic class.
	 */
	public String getClassName() {
		return className;
	}
}
