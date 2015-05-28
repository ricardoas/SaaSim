package saasim.ext.iaas.aws;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.iaas.BillingInfo;
import saasim.core.infrastructure.InstanceDescriptor;

public class AWSMarket{
	
	private Set<InstanceDescriptor> running;

	private EventScheduler scheduler;
	private BillingInfo billingInfo;

	private String name;

	private int quota;

	private int[] typeQuota;

	private Map<AWSInstanceType, Integer> indexMapping;

	private double[] upfront;

	private double[] hourly;

	private long timeBetweenBilling;


	public AWSMarket(EventScheduler scheduler, BillingInfo billingInfo, String name, AWSInstanceType[] types, double[] upfront, double[] hourly, int quota, int[] plan, long timeBetweenBilling) {
		this.scheduler = scheduler;
		this.billingInfo = billingInfo;
		this.name = name;
		this.upfront = upfront;
		this.hourly = hourly;
		this.quota = quota;
		this.typeQuota = plan;
		this.timeBetweenBilling = timeBetweenBilling;
		
		this.running = new HashSet<>();
		
		if(typeQuota.length != 0){
			this.quota = 0;
			for (int i = 0; i < typeQuota.length; i++) {
				this.quota += typeQuota[i];
				if(typeQuota[i] != 0){
					billingInfo.account(scheduler.now(), name, types[i].getName(), "UPFRONT", 0, upfront[i] * typeQuota[i]);
				}
			}
		}else{
			typeQuota = new int[types.length];
			Arrays.fill(typeQuota, Integer.MAX_VALUE);
		}
		
		indexMapping = new HashMap<>();
		for (int i = 0; i < types.length; i++) {
			indexMapping.put(types[i], i);
		}
	}

	public AWSInstanceDescriptor acquire(AWSInstanceType type) {
		final AWSInstanceDescriptor descriptor = new AWSInstanceDescriptor(type, this, scheduler.now());
		running.add(descriptor);
		
		typeQuota[indexMapping.get(descriptor.getType())]--;
		quota--;
		
		scheduler.queueEvent(new Event(scheduler.now() + timeBetweenBilling){
			@Override
			public void trigger() {
				accountMachine(descriptor);
			}
		});
		
		return descriptor;
	}

	protected void accountMachine(final AWSInstanceDescriptor descriptor) {
		long uptime = descriptor.isOn()?timeBetweenBilling:timeBetweenBilling + descriptor.getFinishTime() - scheduler.now();
		billingInfo.account(scheduler.now(), name, descriptor.getType().toString(), descriptor.toString(), uptime, hourly[indexMapping.get(descriptor.getType())]);
		
		if(descriptor.isOn()){
			scheduler.queueEvent(new Event(scheduler.now() + timeBetweenBilling){
				@Override
				public void trigger() {
					accountMachine(descriptor);
				}
			});
		}
	}

	public void release(AWSInstanceDescriptor descriptor) {
		running.remove(descriptor);
		descriptor.turnOff(scheduler.now());
		typeQuota[indexMapping.get(descriptor.getType())]++;
		quota++;
	}
	
	public boolean canAcquire(AWSInstanceType type) {
		return quota != 0 && typeQuota[indexMapping.get(type)] != 0;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
