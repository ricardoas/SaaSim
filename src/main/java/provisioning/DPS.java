package provisioning;


import provisioning.util.DPSInfo;
import commons.cloud.UtilityResult;
import commons.sim.DynamicConfigurable;

/**
 * Dynamic Provisioning System interface. A DPS is a {@link Monitor} object too.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface DPS extends Monitor{
	
	/**
	 * @param configurable The new application to provide infrastructure.
	 */
	void registerConfigurable(DynamicConfigurable configurable);
	
	/**
	 * @return Compute application total utility.
	 */
	UtilityResult calculateUtility();
	
	DPSInfo getDPSInfo();

}
