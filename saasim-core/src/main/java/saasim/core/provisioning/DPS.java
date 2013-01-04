package saasim.core.provisioning;


import saasim.core.application.DynamicallyConfigurable;
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
	 * @param dynamicConfigurables {@link DynamicallyConfigurable} instances.
	 */
	void registerConfigurable(DynamicallyConfigurable... dynamicConfigurables);
	
	/**
	 * Compute application total utility.
	 * @return {@link UtilityResult} object.
	 */
	UtilityFunction calculateUtility();
}
