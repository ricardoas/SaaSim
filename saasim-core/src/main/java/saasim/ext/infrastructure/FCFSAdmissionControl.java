package saasim.ext.infrastructure;

import java.util.Arrays;

import saasim.core.config.Configuration;
import saasim.core.infrastructure.AdmissionControl;
import saasim.core.saas.Application;
import saasim.core.saas.Request;

import com.google.inject.Inject;

/**
 * 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class FCFSAdmissionControl implements AdmissionControl {
	
	private long currentTimeSlot;
	
	private int [] acceptanceRate;
	private int [] counter;
	
	
	/**
	 * Default constructor
	 */
	@Inject
	public FCFSAdmissionControl(Configuration globalConf) {
		
		int numberOfTiers = globalConf.getInt(Application.APPLICATION_TIER_NUMBER);
		this.counter = new int[numberOfTiers];
		this.acceptanceRate = new int[numberOfTiers];
		
		this.currentTimeSlot = -1;
		
		updatePolicy(globalConf);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.AdmissionControl#canAccept(saasim.core.saas.Request)
	 */
	@Override
	public boolean canAccept(Request request) {
		
		int currentTier = request.getCurrentTier();
		
		if(request.getArrivalTimeInSeconds() != currentTimeSlot){
			currentTimeSlot = request.getArrivalTimeInSeconds();
			Arrays.fill(counter, 0);
		}
		
		return (counter[currentTier]++ < acceptanceRate[currentTier]);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.infrastructure.AdmissionControl#updatePolicy(saasim.core.config.Configuration)
	 */
	@Override
	public void updatePolicy(Configuration configuration) {
		Arrays.fill(this.acceptanceRate, Integer.MAX_VALUE);
		this.acceptanceRate [0] = configuration.getInt(ADMISSIONCONTROL_ACCEPTANCERATE);
	}
}
