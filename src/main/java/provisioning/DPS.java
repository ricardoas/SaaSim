package provisioning;

import java.util.List;

import commons.sim.Simulator;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEEventHandler;

/**
 * Dynamic Provisioning System
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface DPS extends JEEventHandler{

	void setConfigurable(Simulator simulator);

	List<Machine> getSetupMachines();

}
