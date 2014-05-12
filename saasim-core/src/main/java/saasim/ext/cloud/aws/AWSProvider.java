package saasim.ext.cloud.aws;

import java.util.HashSet;
import java.util.Set;

import saasim.core.cloud.BillingInfo;
import saasim.core.cloud.IaaSCustomer;
import saasim.core.cloud.IaaSProvider;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.InstanceDescriptor;

import com.google.inject.Inject;

public class AWSProvider implements IaaSProvider {
	
	

	public static final String IAAS_TIMEBETWEENBILLING = "iaas.timebetweenbilling";
	public static final String IAAS_QUOTA = "iaas.quota";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long timeBetweenBilling;

	private EventScheduler scheduler;

	private int quota;
	private AWSReservation reservation;
	private Set<InstanceDescriptor> running;
	private IaaSCustomer customer;
	private BillingInfo billingInfo;

	@Inject
	public AWSProvider(Configuration configuration, EventScheduler scheduler, IaaSCustomer customer) {
		this.scheduler = scheduler;
		this.customer = customer;
		this.timeBetweenBilling = configuration.getLong(IAAS_TIMEBETWEENBILLING);
		this.quota = configuration.getInt(IAAS_QUOTA);
		this.running = new HashSet<>();
		
		billingInfo = new AWSBillingInfo();
		this.reservation = new AWSReservation(configuration, billingInfo);
		
		this.scheduler.queueEvent(new Event(timeBetweenBilling){
			@Override
			public void trigger() {
				reportBilling();
			}
		});
	}

	protected void reportBilling() {
		
		for (InstanceDescriptor descriptor : running) {
			billingInfo.account(descriptor);
		}
		customer.reportIaaSUsage(billingInfo);
		billingInfo.reset();
		
		this.scheduler.queueEvent(new Event(scheduler.now() + timeBetweenBilling){
			@Override
			public void trigger() {
				reportBilling();
			}
		});
	}

	@Override
	public InstanceDescriptor acquire(String instanceType) {
		AWSInstanceType type = parseType(instanceType);
		
		if(reservation.isAvailable(type)){
			return reservation.acquire(type);
		}
		
		if(!quotaExceeded()){
			AWSInstanceDescriptor descriptor = new AWSInstanceDescriptor(parseType(instanceType), false, scheduler.now());
			running.add(descriptor);
			return descriptor;
		}
		
		return null;
	}

	@Override
	public void release(InstanceDescriptor descriptor) {

		descriptor.turnOff(scheduler.now());
		billingInfo.account(descriptor);
		
		if(running.contains(descriptor)){
			running.remove(descriptor);
		}else{
			reservation.release(descriptor);
		}
	}

	@Override
	public boolean canAcquire(String instanceType) {
		return reservation.isAvailable(parseType(instanceType)) || !quotaExceeded();
	}

	private AWSInstanceType parseType(String instanceType) {
		return AWSInstanceType.valueOf(instanceType);
	}

	private boolean quotaExceeded() {
		return quota == running.size();
	}

}
