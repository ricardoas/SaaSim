package provisioning;


import commons.cloud.UtilityResult;
import commons.sim.AccountingSystem;

/**
 * Dynamic Provisioning System
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface DPS extends Monitor{
	
	void setAccountingSystem(AccountingSystem system);
	
	AccountingSystem getAccountingSystem();
	
	void registerConfigurable(DynamicConfigurable configurable);
	
	UtilityResult calculateUtility();

}
