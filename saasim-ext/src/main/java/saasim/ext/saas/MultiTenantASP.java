package saasim.ext.saas;

import saasim.core.config.Configuration;
import saasim.core.provisioning.ProvisioningSystem;
import saasim.core.saas.ASP;
import saasim.core.saas.Request;
import saasim.core.saas.Tenant;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class MultiTenantASP extends AbstractASP implements ASP {

	private Tenant[] tenants;
	
	@Inject
	public MultiTenantASP(Configuration globalConf, ProvisioningSystem provisioningSystem, Provider<Tenant> tenantProvider) {
		int numberOfTenants = globalConf.getInt(Tenant.SAAS_TENANT_NUMBER);
		this.tenants = new Tenant[numberOfTenants];
		
		for (int tenantID = 0; tenantID < tenants.length; tenantID++) {
			this.tenants[tenantID] = tenantProvider.get();
			provisioningSystem.registerConfigurable(tenants[tenantID].getApplication());
		}
	}

	@Override
	public void setUp() {
		for (Tenant tenant : tenants) {
			tenant.setUp();
		}
	}
	
	@Override
	public void finished(Request request) {
		// DO NOTHING
	}
	
	@Override
	public void failed(Request request) {
		// DO NOTHING
	}
	

}
