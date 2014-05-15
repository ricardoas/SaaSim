package saasim.core.sim;

import saasim.core.application.Application;


public interface WorkloadTrafficGeneratorFactory{
	
	WorkloadTrafficGenerator create(Application application, int tenantID);

}
