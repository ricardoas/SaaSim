package saasim.sim;

import java.util.List;

import saasim.cloud.Request;
import saasim.io.WorkloadParser;
import saasim.provisioning.DPS;
import saasim.provisioning.Monitor;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.schedulingheuristics.SchedulingHeuristic;


/**
 * Interface for applications which underlying infrastructure can be dynamically configurable.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface DynamicConfigurable {
	
	/**
	 * Add a new server to the infrastructure
	 * @param tier The application tier which the new machine will serve.
	 * @param machineDescriptor {@link MachineDescriptor} of the new server.
	 * @param useStartUpDelay <code>true</code> to use machine start up delay.
	 */
	void addMachine(int tier, MachineDescriptor machineDescriptor, boolean useStartUpDelay);
	
	/**
	 * Set the {@link WorkloadParser}.
	 * @param parser {@link WorkloadParser} implementation for workload reading.
	 */
	void setWorkloadParser(WorkloadParser<List<Request>> parser);

	/**
	 * Removes a server from specified tier. The removal policy is determined {@link SchedulingHeuristic}.
	 * @param tier The tier whose machine will be removed.
	 * @param force <code>true</code> to remove immediately, and <code>false</code> to stop scheduling and wait
	 * until machine becomes idle to remove.
	 */
	void removeMachine(int tier, boolean force);

	/**
	 * Removes the server represented by {@link MachineDescriptor} from specified tier. 
	 * @param force <code>true</code> to remove immediately, and <code>false</code> to stop scheduling and wait
	 * until machine becomes idle to remove.
	 */
	void removeMachine(int tier, MachineDescriptor descriptor, boolean force);

	/**
	 * Set the {@link Monitor} of application.
	 * @param monitor Monitoring system to collect information needed by {@link DPS}.
	 */
	void setMonitor(Monitor monitor);

	/**
	 * Cancels machine's removal.
	 * @param tier tier where the machines to be recovered is
	 * @param numberOfServers number of machines to be recovered
	 */
	void cancelMachineRemoval(int tier, int numberOfServers);
	
	void config(int threshold);
	
}
