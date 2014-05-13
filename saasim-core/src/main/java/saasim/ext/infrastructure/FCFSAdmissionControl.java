package saasim.ext.infrastructure;

import saasim.core.application.Request;
import saasim.core.config.Configuration;
import saasim.core.infrastructure.AdmissionControl;

import com.google.inject.Inject;

/**
 * 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class FCFSAdmissionControl implements AdmissionControl {
	
	private int acceptanceRate;
	private long currentTimeSlot;
	private int counter;
	private Configuration configuration;
	
	/**
	 * Default constructor
	 */
	@Inject
	public FCFSAdmissionControl(Configuration configuration) {
		
		this.configuration = configuration;
		this.currentTimeSlot = -1;
		this.counter = 0;
		
		updatePolicy();
	}

	@Override
	public boolean canAccept(Request request) {
		
		if(request.getArrivalTimeInSeconds() != currentTimeSlot){
			counter = 0;
		}
		
		return (counter++ < acceptanceRate);
	}

	@Override
	public void updatePolicy() {
		this.acceptanceRate = configuration.getInt(ADMISSIONCONTROL_ACCEPTANCERATE, Integer.MAX_VALUE);
	}
}
