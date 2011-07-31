package provisioning;

import commons.sim.components.Machine;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface DynamicallyConfigurable {
	
	/**
	 * @param tier
	 * @param server
	 */
	void addServer(int tier, Machine server);
	
	/**
	 * @param tier
	 * @param serverID
	 * @param force TODO
	 */
	void removeServer(int tier, long serverID, boolean force);

}
