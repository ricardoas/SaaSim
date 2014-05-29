package saasim.ext.deprovisioning;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import saasim.core.deprovisioning.DeprovisioningSystem;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.util.TimeUnit;

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
	
	public static void main(String[] args) {
//		Locale.setDefault(Locale.FRANCE);
		Calendar instance = GregorianCalendar.getInstance();
		instance.setTimeInMillis(893971817000L + 5 * 3600000);
		
		
		
		
		instance.setTimeZone(TimeZone.getTimeZone("GMT+2:00"));
		System.out.println(new SimpleDateFormat().format(instance.getTime()));
		
		System.out.println(new SimpleDateFormat().format(new Date(897102001000L)));
		System.out.println(new SimpleDateFormat().format(new Date(897084001000L + 5 * 3600000)));
		System.out.println(new SimpleDateFormat().format(new Date(897121510000L + 5 * 3600000)));
		
	}

}
