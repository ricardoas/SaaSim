package saasim.ext.deprovisioning;

import java.util.List;

import saasim.core.deprovisioning.DeprovisioningSystem;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.util.TimeUnit;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AWSFullBillingUptimeBasedDeprovisioningSystem implements DeprovisioningSystem {
	
	private static final long HOUR_IN_MILLIS = TimeUnit.HOUR.getMillis();
	private EventScheduler scheduler;

	@Inject
	public AWSFullBillingUptimeBasedDeprovisioningSystem(EventScheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public InstanceDescriptor chooseMachineToTurnOff(
			List<InstanceDescriptor> instances) {
		
		assert !instances.isEmpty();

		InstanceDescriptor turnOff = null;
		for (InstanceDescriptor instance : instances) {
			if((scheduler.now() - instance.getCreationTime())%HOUR_IN_MILLIS == HOUR_IN_MILLIS - 5 * TimeUnit.MINUTE.getMillis()){
				if(turnOff == null || instance.getCreationTime() < turnOff.getCreationTime()){
					turnOff = instance;
				}
			}
		}
		return turnOff;
	}
}
