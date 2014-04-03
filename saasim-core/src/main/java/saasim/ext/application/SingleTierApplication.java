package saasim.ext.application;

import saasim.core.application.Application;
import saasim.core.application.HorizontallyScalableTier;
import saasim.core.application.Request;
import saasim.core.application.Response;
import saasim.core.application.ResponseListener;
import saasim.core.application.Tier;
import saasim.core.infrastructure.InstanceDescriptor;

/**
 * Single tier application.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SingleTierApplication implements Application {
	
	private Tier tier;

	public SingleTierApplication() {
		tier = new HorizontallyScalableTier() {
			
			@Override
			public Response process(Request request) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void scaleIn(InstanceDescriptor machineDescriptor) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void scaleOut(InstanceDescriptor descriptor, boolean force) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public void config(int d) {

	}

	@Override
	public void process(Request request, ResponseListener callback) {
		
	}
}
