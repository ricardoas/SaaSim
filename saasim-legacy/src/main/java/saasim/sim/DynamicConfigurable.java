package saasim.sim;

import java.util.List;

import saasim.cloud.Request;
import saasim.io.WorkloadParser;
import saasim.provisioning.DPS;
import saasim.provisioning.Monitor;
import saasim.sim.components.LoadBalancer;
import saasim.sim.components.MachineDescriptor;


/**
 * Interface for applications which underlying infrastructure can be dynamically configurable.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface DynamicConfigurable extends ServiceEntry{
	
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
