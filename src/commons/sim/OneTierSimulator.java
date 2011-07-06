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
	private Monitor monitor;
	
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
		if(this.monitor != null){
			return false;
		}
		this.monitor = monitor;
		return false;
	}

}
