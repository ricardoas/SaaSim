package commons.sim.components;

import java.util.Queue;

import commons.cloud.Request;
import commons.sim.jeevent.JEEventHandler;

public interface Machine extends JEEventHandler{

	/**
	 * @return
	 */
	LoadBalancer getLoadBalancer();

	/**
	 * @return
	 */
	Queue<Request> getProcessorQueue();

	/**
	 * @return
	 */
	MachineDescriptor getDescriptor();

	/**
	 * @param request
	 */
	void sendRequest(Request request);

	/**
	 * 
	 */
	void shutdownOnFinish();

}