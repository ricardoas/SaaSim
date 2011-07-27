package commons.sim.util;

public class SimulatorProperties {
	
	//General configuration options
	public static final String APPLICATION_FACTORY="sim.applicationfactoryclass";
	
	public static final String APPLICATION_NUM_OF_TIERS= "sim.application.numberoftiers";
	
	public static final String APPLICATION_HEURISTIC = "sim.application.heuristic";

	public static final String APPLICATION_CUSTOM_HEURISTIC = "sim.application.heuristicclass";

	public static final String APPLICATION_INITIAL_SERVER_PER_TIER = "sim.application.startreplicas";

	public static final String APPLICATION_MAX_SERVER_PER_TIER = "sim.application.maxreplicas";

	public static final String WORKLOAD_PATH = "workload.file";

	public static final String DPS_HEURISTIC = "dps.heuristic";

	public static final String DPS_CUSTOM_HEURISTIC = "dps.heuristicclass";

	public static final String SETUP_TIME = "sim.setuptime";
	
	public static final String PLANNING_HEURISTIC = "sim.plan.heuristic";
	public static final String DEFAULT_PLANNING_HEURISTIC = "genetic";
	
	public static final String SLA = "sim.sla";
	
	public static final String PLANNING_PERIOD = "sim.planning.period";
}
