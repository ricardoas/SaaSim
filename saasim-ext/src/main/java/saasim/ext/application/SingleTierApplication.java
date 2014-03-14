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
			public void scaleUp(InstanceDescriptor machineDescriptor) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void scaleDown(InstanceDescriptor descriptor, boolean force) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	@Override
	public void config(int d) {
		// TODO Auto-generated method stub

	}
	
	public static void main(String[] args) {
		Application app = new SingleTierApplication();
		
		Request request = new Request() {};
		
		ResponseListener callback = new ResponseListener() {
			@Override
			public void processDone(Request request, Response response) {
				System.out.println("OK");
			}
		};
		
		app.process(request, callback);
	}

}
