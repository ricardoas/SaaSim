package saasim.ext.infrastructure;

import saasim.core.application.Request;
import saasim.core.config.Configuration;
import saasim.core.infrastructure.AdmissionControl;
import saasim.core.infrastructure.LoadBalancer;
import saasim.core.infrastructure.Monitor;

import com.google.inject.Inject;

/**
 * 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class FCFSAdmissionControl implements AdmissionControl {
	
	private int acceptanceRate;
	private Monitor monitor;
	
	private int counter;
	private long currentTimeSlot;
	private LoadBalancer loadBalancer;
	
	/**
	 * Default constructor
	 */
	@Inject
	public FCFSAdmissionControl(LoadBalancer loadBalancer, Configuration configuration, Monitor monitor) {
		this.loadBalancer = loadBalancer;
		this.monitor = monitor;
		
		this.acceptanceRate = configuration.getInt("admissioncontrol.acceptancerate", Integer.MAX_VALUE);
		this.currentTimeSlot = -1;
		this.counter = 0;
	}

	@Override
	public void updatePolicy() {
		// TODO Change acceptanceRate on demand
	}

	@Override
	public void queue(Request request) {
		
		
		if(request.getArrivalTimeInSeconds() != currentTimeSlot){
			counter = 0;
		}
		
		if(counter++ < acceptanceRate){
			loadBalancer.queue(request);
		}else{
			monitor.requestFailed(request);
		}
	}

	@Override
	public LoadBalancer getLoadBalancer() {
		return loadBalancer;
	}
}
