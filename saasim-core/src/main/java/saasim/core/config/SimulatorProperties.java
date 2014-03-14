package saasim.core.config;

import saasim.core.sim.Simulator;

/**
 * This class containing the properties to use in {@link Simulator}.
 *  
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public class SimulatorProperties {
	
	public static final String DPS_HEURISTIC = "dps.heuristic";
	public static final String DPS_CUSTOM_HEURISTIC = "dps.heuristicclass";
	public static final String DPS_MONITOR_INTERVAL = "dps.monitor.interval";
	public static final String PARSER_IDIOM = "dps.workload.parser";
	public static final String PARSER_PAGE_SIZE = "dps.workload.pagesize";

	public static final String MACHINE_NUMBER_OF_TOKENS = "machine.numberoftokens";
	public static final String MACHINE_BACKLOG_SIZE = "machine.backlogsize";
	public static final String MACHINE_SESSION_AFFINITY = "machine.session";
	public static final String MACHINE_SESSION_TIMEOUT = "machine.session.timeout";
	public static final String MACHINE_QUANTUM = "machine.psquantum";
	public static final String MACHINE_ENABLE_CORRECTION_FACTOR = "machine.cf";
	public static final String MACHINE_CORRECTION_FACTOR_CONCURRENCY = "machine.cf.concurrency";
	public static final String MACHINE_CORRECTION_FACTOR_CONCURRENCY_VALUES = "machine.cf.concurrency.values";
	public static final String MACHINE_CORRECTION_FACTOR_IDLENESS = "machine.cf.idleness";
	public static final String MACHINE_CORRECTION_FACTOR_VALUE = "machine.cf.value";
	public static final String MACHINE_CORRECTION_FACTOR_A = "machine.cf.a";
	public static final String MACHINE_CORRECTION_FACTOR_B = "machine.cf.b";
	
	public static final String PLANNING_HEURISTIC = "planning.heuristic";
	public static final String PLANNING_PERIOD = "planning.period";
	public static final String PLANNING_TYPE = "planning.type";
	public static final String PLANNING_RISK = "planning.risk";
	public static final String PLANNING_ERROR = "planning.error";
	public static final String PLANNING_INTERVAL_SIZE = "planning.interval.size";
	
	public static final String PLANNING_USE_ERROR = "planning.enableerror";
}
