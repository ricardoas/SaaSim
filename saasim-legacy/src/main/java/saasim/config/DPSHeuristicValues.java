package saasim.config;

import saasim.cloud.MachineType;
import saasim.provisioning.DPS;
import saasim.provisioning.DynamicProvisioningSystem;
import saasim.provisioning.EC2UrgaonkarProvisioningSystem;
import saasim.provisioning.OptimalProvisioningSystemForHeterogeneousMachines;
import saasim.provisioning.RanjanProvisioningSystem;
import saasim.provisioning.RanjanProvisioningSystemForHeterogeneousMachines;
import saasim.provisioning.UrgaonkarProvisioningSystem;
import saasim.sim.util.SimulatorProperties;


/**
 * Provisioning options.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum DPSHeuristicValues {
	/**
	 * Static provisioning. The initial machine allocation is used until the end of simulation time
	 */
	STATIC(DynamicProvisioningSystem.class.getCanonicalName()), 
	/**
	 * Default Ranjan et al' algorithm implementation.
	 */
	RANJAN(RanjanProvisioningSystem.class.getCanonicalName()), 
	/**
	 * Modification of Ranjan et al' algorithm to allow provisioning using different {@link MachineType}
	 */
	RANJAN_HET(RanjanProvisioningSystemForHeterogeneousMachines.class.getCanonicalName()), 
	/**
	 * Indicates a custom {@link DPS} implementation is provided by you. 
	 * @see SimulatorProperties#DPS_CUSTOM_HEURISTIC
	 */
	CUSTOM(""),
	/**
	 * 
	 */
	OPTIMAL(OptimalProvisioningSystemForHeterogeneousMachines.class.getCanonicalName()),
	/**
	 * Default Urgaonkar's algorithm implementation.
	 */
	URGAONKAR(UrgaonkarProvisioningSystem.class.getCanonicalName()),
	/**
	 * Urgaonkar's algorithm optimized for EC2 environment.
	 */
	EC2_URGAONKAR(EC2UrgaonkarProvisioningSystem.class.getCanonicalName());
	
	private final String className;

	/**
	 * Default private constructor.
	 * @param className the name of heuristic class
	 */
	private DPSHeuristicValues(String className){
		this.className = className;
	}

	/**
	 * Gets the name of heuristic class.
	 * @return Class canonical name to load.
	 */
	public String getClassName() {
		return className;
	}
}
