package saasim.ext.cloud.aws;

import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import saasim.core.cloud.BillingInfo;
import saasim.core.config.Configuration;
import saasim.core.infrastructure.InstanceDescriptor;

public class AWSReservation {
	
	private Map<AWSInstanceType, Deque<AWSInstanceDescriptor>> reservation;
	
	private Set<InstanceDescriptor> running;

	private BillingInfo billingInfo;

	public AWSReservation(Configuration configuration, BillingInfo billingInfo) {
		this.billingInfo = billingInfo;
		this.reservation = new HashMap<>();
		this.running = new HashSet<>();
		
		for (AWSInstanceType type : AWSInstanceType.values()) {
			//FIXME LOAD PLAN AND FILL RESERVATION AND PUSH INTO STACK
			reservation.put(type, new LinkedList<AWSInstanceDescriptor>());
		}
	}

	public boolean isAvailable(AWSInstanceType type) {
		return !reservation.get(type).isEmpty();
	}

	public AWSInstanceDescriptor acquire(AWSInstanceType type) {
		AWSInstanceDescriptor descriptor = reservation.get(type).pop();
		running.add(descriptor);
		return descriptor;
	}

	public void release(InstanceDescriptor descriptor) {
		billingInfo.account(descriptor);
		running.remove(descriptor);
	}

}
