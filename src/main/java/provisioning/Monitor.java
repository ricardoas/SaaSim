package provisioning;

import commons.cloud.Request;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.RanjanStatistics;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Monitor{
	
	void reportRequestFinished(Request requestFinished);
	
	void requestQueued(long timeMilliSeconds, Request request, int tier);

	void evaluateUtilisation(long timeMilliSeconds, RanjanStatistics statistics, int tier);

	void machineTurnedOff(MachineDescriptor machineDescriptor);

	void chargeUsers(long currentTimeInMillis);
}
