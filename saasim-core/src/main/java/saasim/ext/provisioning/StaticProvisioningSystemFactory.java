package saasim.ext.provisioning;

import saasim.core.cloud.IaaSProvider;
import saasim.core.config.AbstractFactory;
import saasim.core.config.Configuration;
import saasim.core.provisioning.DPS;

public class StaticProvisioningSystemFactory extends AbstractFactory<DPS> {

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.config.AbstractFactory#build()
	 */
	@Override
	public DPS build(Object... args) {
		return new StaticProvisioningSystem((Configuration) args[0], (IaaSProvider) args[1]);
	}
	
}