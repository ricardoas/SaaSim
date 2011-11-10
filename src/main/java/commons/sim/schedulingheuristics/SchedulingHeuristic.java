package commons.sim.schedulingheuristics;

import java.io.Serializable;
import java.util.Collection;

import commons.cloud.Request;
import commons.sim.components.Machine;
import commons.sim.provisioningheuristics.MachineStatistics;

/**
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface SchedulingHeuristic extends Serializable{

	/**
	 * Determine to which server should this request be redirected to.
	 * @param request The next {@link Request} to be scheduled.
	 * @param servers The collection of server from where the next server should be chosen.
	 * @return
	 */
	Machine next(Request request);

	void reportRequestFinished();

	void addMachine(Machine machine);

	Machine removeMachine();

	int getNumberOfMachines();

	MachineStatistics getStatistics(long eventTime);

	Collection<? extends Machine> getMachines();
}
