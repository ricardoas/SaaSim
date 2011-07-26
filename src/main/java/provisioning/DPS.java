package provisioning;

import java.util.List;

import commons.sim.AccountingSystem;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEEventHandler;

/**
 * Dynamic Provisioning System
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface DPS extends JEEventHandler, Monitor{

	/**
	 * Configurable system.
	 * 
	 * @param configurable
	 */
	void setConfigurable(DynamicallyConfigurable configurable);

	/**
	 * These machines are started up artificially with no delay so 
	 * that the application can start running.
	 *  
	 * @return A list of machines.
	 */
	List<Machine> getSetupMachines();

	void setAccountingSystem(AccountingSystem system);
}
