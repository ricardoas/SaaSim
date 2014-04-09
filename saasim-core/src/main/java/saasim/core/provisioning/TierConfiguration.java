package saasim.core.provisioning;

import saasim.core.infrastructure.InstanceDescriptor;

public class TierConfiguration {
	
	private final ConfigurationAction action;
	private final InstanceDescriptor[] descriptors;
	private final boolean force;
	
	/**
	 * Default constructor
	 * @param action
	 * @param descriptors
	 * @param force
	 */
	public TierConfiguration(ConfigurationAction action, InstanceDescriptor[] descriptors, boolean force) {
		this.action = action;
		this.descriptors = descriptors;
		this.force = force;
	}

	public ConfigurationAction getAction() {
		return action;
	}

	public InstanceDescriptor[] getDescriptors() {
		return descriptors;
	}

	public boolean isForce() {
		return force;
	}
}
