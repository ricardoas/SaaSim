package provisioning;


import commons.cloud.UtilityResult;

/**
 * Dynamic Provisioning System interface. A DPS is a {@link Monitor} object too.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface DPS extends Monitor{
	
	void registerConfigurable(DynamicConfigurable configurable);
	
	UtilityResult calculateUtility();

}
