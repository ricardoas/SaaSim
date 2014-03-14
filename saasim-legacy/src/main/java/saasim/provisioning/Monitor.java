package saasim.provisioning;

import java.io.Serializable;

import saasim.cloud.Request;
import saasim.sim.components.Machine;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.schedulingheuristics.Statistics;

/**
 * Application monitor. Interface for reporting information.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Monitor extends Serializable{
	
	/**
	 * Report when a specific {@link Request} has been finished.
	 * @param requestFinished A {@link Request} finished.
	 */
	void requestFinished(Request requestFinished);
	
	/**
	 * Report when a specific {@link Request} was lost. 
	 * @param timeMilliSeconds the time when the {@link Request} was lost
	 * @param request {@link Request} that was lost
	 * @param tier the tier of machine
	 */
	void requestQueued(long timeMilliSeconds, Request request, int tier);

	/**
	 * Send the calculated statistics coming from {@link Statistics}.
	 * @param timeMilliSeconds the time when send statistics
	 * @param statistics {@link Statistics} encapsulating the statitiscs
	 * @param tier the tier of machine
	 */
	void sendStatistics(long timeMilliSeconds, Statistics statistics, int tier);

	/**
	 * Report when a specific {@link Machine} was shutdown.
	 * @param machineDescriptor {@link MachineDescriptor} turned off.
	 */
	void machineTurnedOff(MachineDescriptor machineDescriptor);

	/**
	 * Report the calculated utility to users.
	 * @param currentTimeInMillis the current time in milliseconds
	 */
	void chargeUsers(long currentTimeInMillis);

	/**
	 * 
	 * @return
	 */
	boolean isOptimal();
}
