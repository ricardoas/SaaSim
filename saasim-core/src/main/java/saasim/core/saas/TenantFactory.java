package saasim.core.saas;

import saasim.core.application.Application;


public interface TenantFactory{
	
	Tenant create(Application application);

}
