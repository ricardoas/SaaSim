package provisioning;

import commons.cloud.Request;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.MachineStatistics;

/**
 * Application monitor. Interface for reporting information.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Monitor{
	
	void requestFinished(Request requestFinished);
	
	void requestQueued(long timeMilliSeconds, Request request, int tier);

	void sendStatistics(long timeMilliSeconds, MachineStatistics statistics, int tier);

	void machineTurnedOff(MachineDescriptor machineDescriptor);

	void chargeUsers(long currentTimeInMillis);
}
