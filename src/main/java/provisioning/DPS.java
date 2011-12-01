package provisioning;


import commons.cloud.UtilityResult;
import commons.sim.DynamicConfigurable;

/**
 * Dynamic Provisioning System interface. A DPS is a {@link Monitor} object too.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface DPS extends Monitor{
	
	/**
	 * Resisters a new {@link DynamicConfigurable} instance to dynamically 
	 * provide infrastructure.
	 * @param configurable {@link DynamicConfigurable} instance.
	 */
	void registerConfigurable(DynamicConfigurable configurable);
	
	/**
	 * Compute application total utility.
	 * @return {@link UtilityResult} object.
	 */
	UtilityResult calculateUtility();
}
