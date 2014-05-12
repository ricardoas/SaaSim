package saasim.core.provisioning;

import saasim.core.infrastructure.InstanceDescriptor;

public class TierConfiguration {
	
	private final int tierID;	
	private final ConfigurationAction action;
	private final InstanceDescriptor descriptor;
	private final boolean force;
	
	/**
	 * Default constructor
	 * @param tierID 
	 * @param action
	 * @param descriptors
	 * @param force
	 */
	public TierConfiguration(int tierID, ConfigurationAction action, InstanceDescriptor descriptors, boolean force) {
		this.tierID = tierID;
		this.action = action;
		this.descriptor = descriptors;
		this.force = force;
	}
	
	public int getTierID() {
		return tierID;
	}

	public ConfigurationAction getAction() {
		return action;
	}

	public InstanceDescriptor getDescriptor() {
		return descriptor;
	}

	public boolean isForce() {
		return force;
	}
}
