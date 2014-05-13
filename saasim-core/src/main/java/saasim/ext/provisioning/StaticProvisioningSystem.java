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
	private Configuration config;
	private IaaSProvider provider;

	@Inject
	public StaticProvisioningSystem(Configuration config, IaaSProvider provider) {
		this.config = config;
		this.provider = provider;
	}

	@Override
	public void registerConfigurable(Application... applications) {
		this.applications = applications;
		setUp();
	}

	protected void setUp() {
		for (Application application : applications) {
			int numberOfTiers = application.getNumberOfTiers();
			String[] startNumberOfReplicas = config.getStringArray(Application.APPLICATION_TIER_REPLICAS);
			String[] vmTypePerTier = config.getStringArray(Application.APPLICATION_TIER_VMTYPE);
			
			for (int i = 0; i < numberOfTiers; i++) {
				int numberOfReplicas = Integer.valueOf(startNumberOfReplicas[i]);
				for (int j = 0; j < numberOfReplicas; j++) {
					if(provider.canAcquire(vmTypePerTier[i])){
						config.setProperty(ApplicationConfiguration.TIER_ID, i);
						config.setProperty(ApplicationConfiguration.ACTION, ApplicationConfiguration.ACTION_INCREASE);
						config.setProperty(ApplicationConfiguration.INSTANCE_DESCRIPTOR, provider.acquire(vmTypePerTier[i]));
						config.setProperty(ApplicationConfiguration.FORCE, true);
						application.configure();
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
