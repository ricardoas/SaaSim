package saasim.ext.deprovisioning;

import java.util.List;

import com.google.inject.Singleton;

import saasim.core.deprovisioning.DeprovisioningSystem;
import saasim.core.infrastructure.InstanceDescriptor;

@Singleton
public class UptimeBasedDeprovisioningSystem implements DeprovisioningSystem {

	@Override
	public InstanceDescriptor chooseMachineToTurnOff(
			List<InstanceDescriptor> instances) {
		
		assert !instances.isEmpty();
		
		InstanceDescriptor turnOff = instances.get(0);
		for (InstanceDescriptor instance : instances) {
			if(instance.getCreationTime() < turnOff.getCreationTime()){
					turnOff = instance;
			}
		}
		return turnOff;
	}

}
