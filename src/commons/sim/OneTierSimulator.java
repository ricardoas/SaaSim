package commons.sim;

import commons.cloud.Request;
import commons.config.WorkloadParser;
import config.GEISTSimpleWorkloadParser;

import provisioning.Monitor;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public class OneTierSimulator implements Simulator {
	
	private WorkloadParser<Request> workloadParser;
	
	/**
	 * Constructor
	 */
	public OneTierSimulator() {
		this.workloadParser = new GEISTSimpleWorkloadParser("");
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setMonitor(Monitor monitor) {
		// TODO Auto-generated method stub
		return false;
	}

}
