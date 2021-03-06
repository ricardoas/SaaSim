package saasim.core.provisioning;


import saasim.core.cloud.utility.UtilityFunction;
import saasim.core.saas.Application;

/**
 * Provisioning System interface.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface ProvisioningSystem{
	
	/**
	 * Registers new {@link Application}s to provision. Subsequent calls override 
	 * previously configured applications. Be sure to call it only once unless you really know what you're doing!
	 * FIXME deal with duplicated application
	 * @param dynamically configurables {@link Application} applications.
	 */
	void registerConfigurable(Application... applications);
	
	/**
	 * Compute application total utility.
	 * @return {@link UtilityResult} object.
	 */
	UtilityFunction calculateUtility();
}
