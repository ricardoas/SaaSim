package commons.sim;

import java.io.IOException;

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
	
	private LoadBalancer applicationServer;
	
	/**
	 * Constructor
	 */
	public OneTierSimulator() {
		this.workloadParser = new GEISTSimpleWorkloadParser("");
		
	}

	@Override
	public void start() {
		
		Clock.INSTANCE.reset();
		try {
			while(workloadParser.hasNext()){
				Request request = workloadParser.next();
				
				Clock.INSTANCE.walk();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
