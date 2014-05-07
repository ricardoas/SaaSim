package saasim.ext.application;

import saasim.core.application.Application;
import saasim.core.application.HorizontallyScalableTier;
import saasim.core.application.Request;
import saasim.core.application.Tier;
import saasim.core.infrastructure.Monitor;
import saasim.core.provisioning.TierConfiguration;

import com.google.inject.Inject;

/**
 * Single tier application.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SingleTierApplication implements Application {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Monitor monitor;
	private Tier tier;

	@Inject
	public SingleTierApplication(Monitor monitor) {
		this.monitor = monitor;
		this.tier = new HorizontallyScalableTier();
	}

	@Override
	public void queue(Request request) {
		
	}

	@Override
	public void config(TierConfiguration... tierConfiguration) {
		tier.config(tierConfiguration[0]);
	}

	@Override
	public int getNumberOfTiers() {
		return 1;
	}
}
