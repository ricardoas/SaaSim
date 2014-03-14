package saasim.provisioning;


import saasim.cloud.utility.UtilityResult;
import saasim.sim.DynamicConfigurable;

/**
 * Dynamic Provisioning System interface. A DPS is a {@link Monitor} object too.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface DPS extends Monitor{
	
	/**
	 * Registers new applications to provision. Subsequent calls override 
	 * previously configured applications. Be sure to call it only once.
	 * 
	 * @param dynamicConfigurables {@link DynamicConfigurable} instances.
	 */
	void registerConfigurable(DynamicConfigurable... dynamicConfigurables);
	
	/**
	 * Compute application total utility.
	 * @return {@link UtilityResult} object.
	 */
	UtilityResult calculateUtility();
}
