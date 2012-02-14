package saasim.sim.schedulingheuristics;

import java.io.Serializable;
import java.util.List;

import saasim.cloud.Request;
import saasim.sim.components.Machine;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.provisioningheuristics.MachineStatistics;


/**
 * Interface to represent a generic scheduling heuristic and it commons features.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface SchedulingHeuristic extends Serializable{

	/**
	 * Determines to which server should this request be redirected to.
	 * @param request The next {@link Request} to be scheduled.
	 * @return The next {@link Machine} chosen.
	 */
	Machine next(Request request);

	/**
	 * Reports when a specific {@link Request} has been finished.
	 * @param requestFinished {@link Request} finished.
	 */
	void reportFinishedRequest(Request requestFinished);

	/**
	 * Adds a new {@link Machine} in the list of servers of application. 
	 * @param machine New {@link Machine} to be added.
	 */
	void addMachine(Machine machine);

	/**
	 * Removes a last {@link Machine} from the list of serves of application.
	 * @return The removed machine.
	 */
	@Deprecated
	Machine removeMachine();

	/**
	 * Gets the number of {@link Machine}s in the application. 
	 * @return The number of machines.
	 */
	int getNumberOfMachines();

	/**
	 * Gets the statics about all machines in the application.
	 * @param eventTime the time to compute utilisation of machines
	 * @return A {@link MachineStatistics} encapsulating the statics of all machines.
	 */
	MachineStatistics getStatistics(long eventTime);

	/**
	 * Gets the {@link Machine}s  in the application.
	 * @return A list containing the machines.
	 */
	List<Machine> getMachines();

	/**
	 * Removes a {@link Machine} represented by given {@link MachineDescriptor} 
	 * from the list of servers.
	 * @param descriptor The machine to be removed.
	 * @return The removed machine.
	 */
	Machine removeMachine(MachineDescriptor descriptor);
}
