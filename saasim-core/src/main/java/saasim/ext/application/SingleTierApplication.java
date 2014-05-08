package saasim.ext.application;

import saasim.core.application.Application;
import saasim.core.application.Request;
import saasim.core.application.Tier;
import saasim.core.provisioning.TierConfiguration;

import com.google.inject.Inject;

/**
 * Single tier application.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SingleTierApplication implements Application {
	
	private Tier tier;

	@Inject
	public SingleTierApplication(Tier tier) {
		this.tier = tier;
	}

	@Override
	public void queue(Request request) {
		this.tier.queue(request);
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
