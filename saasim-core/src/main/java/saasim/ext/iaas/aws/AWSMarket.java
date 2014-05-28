package saasim.ext.iaas.aws;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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


	public AWSMarket(EventScheduler scheduler, BillingInfo billingInfo, String name, AWSInstanceType[] types, double[] upfront, double[] hourly, int quota, int[] plan) {
		this.scheduler = scheduler;
		this.billingInfo = billingInfo;
		this.name = name;
		this.quota = quota;
		this.typeQuota = plan;
		
		this.running = new HashSet<>();
		
		if(typeQuota.length != 0){
			quota = 0;
			for (int i = 0; i < typeQuota.length; i++) {
				quota += typeQuota[i];
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
		AWSInstanceDescriptor descriptor = new AWSInstanceDescriptor(type, this, scheduler.now());
		running.add(descriptor);
		
		typeQuota[indexMapping.get(descriptor.getType())]--;
		quota--;

		return descriptor;
	}

	public void release(AWSInstanceDescriptor descriptor) {
		running.remove(descriptor);
		descriptor.turnOff(scheduler.now());
		billingInfo.account(descriptor, scheduler.now());
		
		typeQuota[indexMapping.get(descriptor.getType())]++;
		quota++;
	}
	
	public boolean canAcquire(AWSInstanceType type) {
		return quota != 0 && typeQuota[indexMapping.get(type)] != 0;
	}

	public void reportBilling() {
		for (InstanceDescriptor instance : running) {
			billingInfo.account(instance, scheduler.now());
		}
	}
}
