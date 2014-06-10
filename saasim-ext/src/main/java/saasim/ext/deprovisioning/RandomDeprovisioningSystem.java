package saasim.ext.deprovisioning;

import java.util.List;
import java.util.Random;

import com.google.inject.Singleton;

import saasim.core.deprovisioning.DeprovisioningSystem;
import saasim.core.infrastructure.InstanceDescriptor;

@Singleton
public class RandomDeprovisioningSystem implements DeprovisioningSystem {

	@Override
	public InstanceDescriptor chooseMachineToTurnOff(
			List<InstanceDescriptor> instances) {
		
		assert !instances.isEmpty();
		
		return instances.get(new Random().nextInt(instances.size()));
	}

}
