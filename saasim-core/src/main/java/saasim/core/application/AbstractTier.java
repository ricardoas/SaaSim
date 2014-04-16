package saasim.core.application;

import saasim.core.infrastructure.AdmissionControl;
import saasim.core.infrastructure.LoadBalancer;

public abstract class AbstractTier implements Tier{

	private AdmissionControl admissionControl;
	protected LoadBalancer loadBalancer;

	public AbstractTier() {
		super();
	}

	@Override
	public void process(Request request, ResponseListener responseListener) {
		if(admissionControl.canProcess()){
			loadBalancer.process(request);
		}
	}
}