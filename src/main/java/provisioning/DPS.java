package provisioning;

import java.util.List;

import commons.sim.Simulator;
import commons.sim.components.Machine;

/**
 * Dynamic Provisioning System
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface DPS {

	void setConfigurable(Simulator simulator);

	List<Machine> getSetupMachines();

}
