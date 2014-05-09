package saasim.ext.cloud;

import saasim.core.infrastructure.InstanceDescriptor;

public class AmazonInstanceDescriptor implements InstanceDescriptor {

	private InstanceType type;

	public AmazonInstanceDescriptor(InstanceType instanceType) {
		this.type = instanceType;
	}

	@Override
	public int getNumberOfCPUCores() {
		return type.getNumberOfCores();
	}

}
