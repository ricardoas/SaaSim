package commons.sim.components;

import java.util.Queue;

import commons.cloud.Request;
import commons.sim.jeevent.JEEventHandler;

/**
 * Methods to manage a machine.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Machine extends JEEventHandler{

	/**
	 * @return The {@link LoadBalancer} responsible for this machine.
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
	 * @param request Send a new {@link Request} to be processed by this machine.
	 */
	void sendRequest(Request request);

	/**
	 * Send a shutdown signal to the machine.
	 */
	void shutdownOnFinish();

	/**
	 * @param timeInMillis
	 * @return
	 */
	double computeUtilisation(long timeInMillis);

}