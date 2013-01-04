package saasim.core.application;

import java.util.List;

import saasim.core.io.WorkloadParser;
import saasim.core.provisioning.DPS;


/**
 * Interface for applications which underlying infrastructure can be dynamically configurable.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface DynamicallyConfigurable extends ServiceEntry{
	
	/**
	 * Add a new server to the infrastructure
	 * @param tier The application tier which the new machine will serve.
	 * @param machineDescriptor {@link MachineDescriptor} of the new server.
	 */
	void addMachine(int tier, MachineDescriptor machineDescriptor);
	
	/**
	 * Set the {@link WorkloadParser}.
	 * @param parser {@link WorkloadParser} implementation for workload reading.
	 */
	void setWorkloadParser(WorkloadParser<List<Request>> parser);

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

	void config(int tier, double threshold);
	
	/**
	 * Gets the tiers of simulation.
	 * @return The tiers of simulation.
	 */
	LoadBalancer[] getTiers();
}
