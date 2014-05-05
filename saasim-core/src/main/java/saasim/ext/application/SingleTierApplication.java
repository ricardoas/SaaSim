package saasim.ext.application;

import saasim.core.application.Application;
import saasim.core.application.HorizontallyScalableTier;
import saasim.core.application.Request;
import saasim.core.application.ResponseListener;
import saasim.core.application.Tier;
import saasim.core.provisioning.TierConfiguration;

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
	private Tier tier;

	public SingleTierApplication() {
		tier = new HorizontallyScalableTier();
	}

	@Override
	public void process(Request request, ResponseListener callback) {
		
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
