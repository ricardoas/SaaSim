package commons.config;

import provisioning.DPS;

import commons.cloud.MachineType;
import commons.sim.util.SimulatorProperties;

/**
 * Provisioning options.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum DPSHeuristicValues {
	/**
	 * Static provisioning. The initial machine allocation is used until the end of simulation time
	 */
	STATIC, 
	/**
	 * Default Ranjan et all' algorithm implementation.
	 */
	RANJAN, 
	/**
	 * Modification of Ranjan et all'algorithm to allow provisioning using different {@link MachineType}
	 */
	RANJAN_HET, 
	/**
	 * Not fully implemented. We don't know a practical way of calculating information needed for decision making.
	 */
	@Deprecated
	PROFITDRIVEN, 
	/**
	 * Indicates a custom {@link DPS} implementation is provided by you. 
	 * @see SimulatorProperties#DPS_CUSTOM_HEURISTIC
	 */
	CUSTOM, OPTIMAL, 
}
