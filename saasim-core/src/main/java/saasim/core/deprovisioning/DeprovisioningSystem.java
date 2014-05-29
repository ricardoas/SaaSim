package saasim.core.deprovisioning;

import java.util.List;

import saasim.core.infrastructure.InstanceDescriptor;

public interface DeprovisioningSystem {
	
	InstanceDescriptor chooseMachineToTurnOff(List<InstanceDescriptor> instances);

}
