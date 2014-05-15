package saasim.ext.cloud.aws;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.iaas.BillingInfo;
import saasim.core.iaas.Customer;
import saasim.core.iaas.Provider;
import saasim.core.infrastructure.InstanceDescriptor;

import com.google.inject.Inject;

public class AWSProvider implements Provider {
	
	public static final String IAAS_AWS_HEAVY_HOURLY = "iaas.aws.heavy.hourly";
	public static final String IAAS_AWS_HEAVY_UPFRONT = "iaas.aws.heavy.upfront";
	public static final String IAAS_AWS_MEDIUM_HOURLY = "iaas.aws.medium.hourly";
	public static final String IAAS_AWS_MEDIUM_UPFRONT = "iaas.aws.medium.upfront";
	public static final String IAAS_AWS_LIGHT_HOURLY = "iaas.aws.light.hourly";
	public static final String IAAS_AWS_LIGHT_UPFRONT = "iaas.aws.light.upfront";
	public static final String IAAS_AWS_STORAGE = "iaas.aws.storage";
	public static final String IAAS_AWS_MEMORY = "iaas.aws.memory";
	public static final String IAAS_AWS_ECU = "iaas.aws.ecu";
	public static final String IAAS_AWS_PROCESSOR = "iaas.aws.processor";
	public static final String IAAS_AWS_TYPES = "iaas.aws.types";
	
	public enum MarketType {
		ONDEMAND, LIGHT, MEDIUM, HEAVY, SPOT
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long timeBetweenBilling;

	private EventScheduler scheduler;

	private int quota;
	private AWSReservation reservation;
	private Set<InstanceDescriptor> running;
	private Customer customer;
	private BillingInfo billingInfo;
	
	private Map<String,AWSInstanceType> types;

	@Inject
	public AWSProvider(Configuration globalConf, EventScheduler scheduler, Customer customer) {
		this.scheduler = scheduler;
		this.customer = customer;
		this.timeBetweenBilling = globalConf.getLong(IAAS_TIMEBETWEENBILLING);
		this.quota = globalConf.getInt(IAAS_QUOTA);
		this.running = new HashSet<>();
		
		billingInfo = new AWSBillingInfo();
		
		this.scheduler.queueEvent(new Event(timeBetweenBilling){
			@Override
			public void trigger() {
				reportBilling();
			}
		});
		
		this.types = parseInstanceTypes(globalConf);
		
		this.reservation = new AWSReservation(globalConf, billingInfo, types);
	}

	private Map<String, AWSInstanceType> parseInstanceTypes(Configuration configuration) {
		
		Map<String, AWSInstanceType> types = new HashMap<>();
		
		String[] typeNames = configuration.getStringArray(IAAS_AWS_TYPES);
		String[] processors = configuration.getStringArray(IAAS_AWS_PROCESSOR);
		String[] ecus = configuration.getStringArray(IAAS_AWS_ECU);
		String[] memories = configuration.getStringArray(IAAS_AWS_MEMORY);
		String[] storages = configuration.getStringArray(IAAS_AWS_STORAGE);
		String[] hourly = configuration.getStringArray("iaas.aws.hourly");
		String[] lightUpfront = configuration.getStringArray(IAAS_AWS_LIGHT_UPFRONT);
		String[] lightHourly = configuration.getStringArray(IAAS_AWS_LIGHT_HOURLY);
		String[] mediumUpfront = configuration.getStringArray(IAAS_AWS_MEDIUM_UPFRONT);
		String[] mediumHourly = configuration.getStringArray(IAAS_AWS_MEDIUM_HOURLY);
		String[] heavyUpfront = configuration.getStringArray(IAAS_AWS_HEAVY_UPFRONT);
		String[] heavyHourly = configuration.getStringArray(IAAS_AWS_HEAVY_HOURLY);
		
		for (int i = 0; i < typeNames.length; i++) {
			
			types.put(typeNames[i], new AWSInstanceType(typeNames[i], processors[i], ecus[i], memories[i], storages[i], hourly[i],
					lightUpfront[i], lightHourly[i], mediumUpfront[i], mediumHourly[i], heavyUpfront[i], heavyHourly[i]));
		}
		
		return types;

	}

	protected void reportBilling() {
		
		for (InstanceDescriptor descriptor : running) {
			billingInfo.account(descriptor, scheduler.now());
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
			AWSInstanceDescriptor descriptor = new AWSInstanceDescriptor(parseType(instanceType), MarketType.ONDEMAND, scheduler.now());
			running.add(descriptor);
			return descriptor;
		}
		
		return null;
	}

	@Override
	public void release(InstanceDescriptor descriptor) {

		descriptor.turnOff(scheduler.now());
		billingInfo.account(descriptor, scheduler.now());
		
		if(running.contains(descriptor)){
			running.remove(descriptor);
		}else{
			reservation.release(descriptor, scheduler.now());
		}
	}

	@Override
	public boolean canAcquire(String instanceType) {
		return reservation.isAvailable(parseType(instanceType)) || !quotaExceeded();
	}

	private AWSInstanceType parseType(String instanceType) {
		return types.get(instanceType);
	}

	private boolean quotaExceeded() {
		return quota == running.size();
	}

}
