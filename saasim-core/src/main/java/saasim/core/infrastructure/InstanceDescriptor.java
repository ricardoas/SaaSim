package saasim.core.infrastructure;

import saasim.core.cloud.InstanceType;

public class InstanceDescriptor {

	private InstanceType type;

	public InstanceDescriptor(InstanceType instanceType) {
		this.type = instanceType;
	}

	public int getNumberOfCPUCores() {
		return type.getNumberOfCores();
	}

}
