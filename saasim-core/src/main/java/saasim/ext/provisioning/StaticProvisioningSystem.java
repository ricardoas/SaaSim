package saasim.ext.provisioning;

import saasim.core.application.Application;
import saasim.core.cloud.IaaSProvider;
import saasim.core.cloud.utility.UtilityFunction;
import saasim.core.config.Configuration;
import saasim.core.provisioning.ApplicationConfiguration;
import saasim.core.provisioning.ProvisioningSystem;

import com.google.inject.Inject;

public class StaticProvisioningSystem implements ProvisioningSystem {
	
	private Application[] applications;
	private IaaSProvider provider;
	private String[] startNumberOfReplicas;
	private String[] vmTypePerTier;

	@Inject
	public StaticProvisioningSystem(Configuration globalConf, IaaSProvider provider) {
		this.provider = provider;
		startNumberOfReplicas = globalConf.getStringArray(Application.APPLICATION_TIER_REPLICAS);
		vmTypePerTier = globalConf.getStringArray(Application.APPLICATION_TIER_VMTYPE);
	}

	@Override
	public void registerConfigurable(Application... applications) {
		this.applications = applications;
		setUp();
	}

	protected void setUp() {
		for (Application application : applications) {
			for (int tierID = 0; tierID < startNumberOfReplicas.length; tierID++) {
				int numberOfReplicas = Integer.valueOf(startNumberOfReplicas[tierID]);
				while(numberOfReplicas-- > 0){
					if(provider.canAcquire(vmTypePerTier[tierID])){
						Configuration config = new Configuration();
						config.setProperty(ApplicationConfiguration.TIER_ID, tierID);
						config.setProperty(ApplicationConfiguration.ACTION, ApplicationConfiguration.ACTION_INCREASE);
						config.setProperty(ApplicationConfiguration.INSTANCE_DESCRIPTOR, provider.acquire(vmTypePerTier[tierID]));
						config.setProperty(ApplicationConfiguration.FORCE, true);
						application.configure(config);
					}
				}
			}
		}
	}

	@Override
	public UtilityFunction calculateUtility() {
		return null;
	}
}
