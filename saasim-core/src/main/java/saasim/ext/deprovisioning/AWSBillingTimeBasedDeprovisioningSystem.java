package saasim.ext.deprovisioning;

import java.util.List;

import saasim.core.deprovisioning.DeprovisioningSystem;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.util.TimeUnit;

public class AWSBillingTimeBasedDeprovisioningSystem implements DeprovisioningSystem {
	
	private static final long HOUR_IN_MILLIS = TimeUnit.HOUR.getMillis();
	private EventScheduler scheduler;

	public AWSBillingTimeBasedDeprovisioningSystem(EventScheduler scheduler) {
		this.scheduler = scheduler;
	}

	@Override
	public InstanceDescriptor chooseMachineToTurnOff(
			List<InstanceDescriptor> instances) {
		
		assert !instances.isEmpty();
		
		InstanceDescriptor turnOff = instances.get(0);
		long uptime = scheduler.now() - turnOff.getCreationTime();
		long timeUntilNextBilling = HOUR_IN_MILLIS*((long)Math.ceil(1.0*uptime/HOUR_IN_MILLIS))-uptime;
		
		for (InstanceDescriptor instance : instances) {
			uptime = scheduler.now() - instance.getCreationTime();
			long instanceTimeUntilNextBilling = HOUR_IN_MILLIS*((long)Math.ceil(1.0*uptime/HOUR_IN_MILLIS))-uptime;

			if(instanceTimeUntilNextBilling < timeUntilNextBilling){
				turnOff = instance;
			}
		}
		
		return turnOff;
	}

}
