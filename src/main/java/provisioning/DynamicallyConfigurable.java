package provisioning;

import commons.sim.components.MachineDescriptor;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface DynamicallyConfigurable {
	
	/**
	 * @param tier
	 * @param server
	 */
	void addServer(int tier, MachineDescriptor machineDescriptor);
	
	/**
	 * @param tier
	 * @param serverID
	 * @param force TODO
	 */
	void removeServer(int tier, MachineDescriptor machineDescriptor, boolean force);

}
