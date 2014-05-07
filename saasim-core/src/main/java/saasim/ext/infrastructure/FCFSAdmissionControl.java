package saasim.ext.infrastructure;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.configuration.Configuration;

import saasim.core.application.Request;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
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
	
	private final Queue<Request> acceptedQueue;
	private int acceptanceRate;
	private int frequency;
	private Monitor monitor;
	private EventScheduler scheduler;
	
	/**
	 * Default constructor
	 */
	@Inject
	public FCFSAdmissionControl(EventScheduler scheduler, Configuration configuration, Monitor monitor) {
		this.monitor = monitor;
		this.scheduler = scheduler;
		
		this.acceptedQueue = new LinkedList<>();
		this.acceptanceRate = Integer.MAX_VALUE;
		this.frequency = configuration.getInt("admissioncontrol.frquency");
	}

	@Override
	public void process(long timestamp, final LoadBalancer loadBalancer) {
		int counter = 0;
		
		while(!acceptedQueue.isEmpty()){
			if(acceptedQueue.peek().getArrivalTimeInMillis() != timestamp){
				break;
			}
			
			if(counter < acceptanceRate){
				loadBalancer.queue(acceptedQueue.poll());
				counter++;
			}else{
				monitor.requestFailed(acceptedQueue.poll());
			}
		}
		
		scheduler.queueEvent(new Event(scheduler.now() + frequency) {
			@Override
			public void trigger() {
				process(scheduler.now(), loadBalancer);
			}
		});
	}

	@Override
	public void updatePolicy() {

	}

	@Override
	public boolean queue(Request request) {
		if(acceptedQueue.size() >= acceptanceRate){
			return false;
		}
		return acceptedQueue.add(request);
	}

}
