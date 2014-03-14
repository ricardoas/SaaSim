package saasim.core.provisioning;


import saasim.core.application.Tier;
import saasim.core.cloud.utility.UtilityFunction;

/**
 * Dynamic Provisioning System interface.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface DPS{
	
	/**
	 * Registers new applications to provision. Subsequent calls override 
	 * previously configured applications. Be sure to call it only once.
	 * 
	 * @param dynamicConfigurables {@link Tier} instances.
	 */
	void registerConfigurable(Tier... dynamicConfigurables);
	
	/**
	 * Compute application total utility.
	 * @return {@link UtilityResult} object.
	 */
	UtilityFunction calculateUtility();
}
