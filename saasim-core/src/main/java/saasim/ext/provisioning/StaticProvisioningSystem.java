package saasim.ext.provisioning;

import com.google.inject.Inject;

import saasim.core.application.Application;
import saasim.core.cloud.IaaSProvider;
import saasim.core.cloud.InstanceType;
import saasim.core.cloud.utility.UtilityFunction;
import saasim.core.config.Configuration;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.provisioning.ConfigurationAction;
import saasim.core.provisioning.DPS;
import saasim.core.provisioning.TierConfiguration;

public class StaticProvisioningSystem implements DPS {
	
	private Application[] applications;
	private Configuration config;
	private IaaSProvider provider;

	@Inject
	public StaticProvisioningSystem(Configuration config, IaaSProvider provider) {
		System.out
				.println("StaticProvisioningSystem.StaticProvisioningSystem() config="+config + " provider=" + provider);
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
			TierConfiguration[] tierConfiguration = new TierConfiguration[numberOfTiers];
			String[] startNumberOfReplicas = config.getStringArray("application.startreplicas");
			String[] vmTypePerTier = config.getStringArray("application.tier.vmtype");
			
			for (int i = 0; i < tierConfiguration.length; i++) {
				int numberOfReplicas = Integer.valueOf(startNumberOfReplicas[i]);
				InstanceDescriptor[] descriptors = new InstanceDescriptor[numberOfReplicas];
				for (int j = 0; j < numberOfReplicas; j++) {
					descriptors[j] = provider.acquire(InstanceType.valueOf(vmTypePerTier[i]));
				}
				tierConfiguration[i] = new TierConfiguration(ConfigurationAction.INCREASE, descriptors, true);
			}
			application.config(tierConfiguration);
		}
	}

	@Override
	public UtilityFunction calculateUtility() {
		return null;
	}
}
