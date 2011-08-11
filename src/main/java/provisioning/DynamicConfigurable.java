package provisioning;

import java.util.List;

import commons.cloud.Request;
import commons.io.WorkloadParser;
import commons.sim.components.MachineDescriptor;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface DynamicConfigurable {
	
	/**
	 * @param tier
	 * @param useStartUpDelay 
	 * @param server
	 */
	void addServer(int tier, MachineDescriptor machineDescriptor, boolean useStartUpDelay);
	
	/**
	 * @param tier
	 * @param serverID
	 * @param force TODO
	 */
	void removeServer(int tier, MachineDescriptor machineDescriptor, boolean force);

	void setWorkloadParser(WorkloadParser<List<Request>> parser);

	void removeServer(int tier, boolean force);
	
}
