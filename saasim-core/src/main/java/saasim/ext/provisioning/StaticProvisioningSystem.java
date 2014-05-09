package saasim.ext.provisioning;

import saasim.core.application.Application;
import saasim.core.cloud.IaaSProvider;
import saasim.core.cloud.utility.UtilityFunction;
import saasim.core.config.Configuration;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.provisioning.ConfigurationAction;
import saasim.core.provisioning.DPS;
import saasim.core.provisioning.TierConfiguration;
import saasim.ext.cloud.InstanceType;

import com.google.inject.Inject;

public class StaticProvisioningSystem implements DPS {
	
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
			String[] startNumberOfReplicas = config.getStringArray("application.tier.replicas");
			String[] vmTypePerTier = config.getStringArray("application.tier.vmtype");
			
			for (int i = 0; i < numberOfTiers; i++) {
				int numberOfReplicas = Integer.valueOf(startNumberOfReplicas[i]);
				InstanceDescriptor[] descriptors = new InstanceDescriptor[numberOfReplicas];
				for (int j = 0; j < numberOfReplicas; j++) {
					descriptors[j] = provider.acquire(InstanceType.valueOf(vmTypePerTier[i]));
				}
				application.config(new TierConfiguration(i, ConfigurationAction.INCREASE, descriptors, true));
			}
		}
	}

	@Override
	public UtilityFunction calculateUtility() {
		return null;
	}
}
