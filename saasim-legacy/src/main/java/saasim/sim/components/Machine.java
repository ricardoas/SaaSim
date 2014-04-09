package saasim.sim.components;

import java.util.Queue;

import saasim.cloud.Request;
import saasim.sim.core.EventHandler;


/**
 * Methods to manage a machine.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Machine extends EventHandler{

	/**
	 * @return The {@link SimpleLoadBalancerWithAdmissionControl} responsible for this machine.
	 */
	LoadBalancer getLoadBalancer();

	/**
	 * @return The {@link Queue} of {@link Request}s this machine is serving.
	 */
	Queue<Request> getProcessorQueue();

	/**
	 * @return The descriptor of this machine. Refer to {@link MachineDescriptor} to 
	 * detailed information.
	 */
	MachineDescriptor getDescriptor();

	/**
	 * Send a new {@link Request} to be processed by this machine.
	 * @param request a new {@link Request} to be processed by this machine.
	 */
	void sendRequest(Request request);

	/**
	 * Send a shutdown signal to the machine.
	 */
	void shutdownOnFinish();

	/**
	 * Compute the utilisation of the machine.
	 * @param timeInMillis current time in milliseconds
	 * @return the result of the computed utilisation
	 */
	double computeUtilisation(long timeInMillis);
	
	/**
	 * Gets the total time used per this {@link Machine}.
	 * @return the total time used
	 */
	long getTotalTimeUsed();
	
	/**
	 * @return The number of cores of this {@link Machine}.
	 */
	int getNumberOfCores();

	/**
	 * Cancels the shutdown of {@link Machine}.
	 */
	void cancelShutdown();

	void shutdownNow();
}