package saasim.ext.iaas.aws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.iaas.BillingInfo;
import saasim.core.iaas.Customer;
import saasim.core.iaas.Provider;
import saasim.core.infrastructure.InstanceDescriptor;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AWSProvider implements Provider {
	
	public static final String IAAS_AWS_STORAGE = "iaas.aws.storage";
	public static final String IAAS_AWS_MEMORY = "iaas.aws.memory";
	public static final String IAAS_AWS_ECU = "iaas.aws.ecu";
	public static final String IAAS_AWS_PROCESSOR = "iaas.aws.processor";
	public static final String IAAS_AWS_TYPES = "iaas.aws.types";
	public static final String IAAS_AWS_MARKETS = "iaas.aws.markets";
	
	public static final String IAAS_AWS_UPFRONT = "iaas.aws.upfront.";
	public static final String IAAS_AWS_HOURLY = "iaas.aws.hourly.";
	public static final String IAAS_AWS_QUOTA = "iaas.aws.quota.";
	public static final String IAAS_AWS_PLAN = "iaas.aws.plan.";

	private long timeBetweenBilling;

	private EventScheduler scheduler;

	private Customer customer;
	private BillingInfo billingInfo;
	
	private Map<String, AWSInstanceType> typeMapping;
	private List<AWSMarket> markets;
	
	@Inject
	public AWSProvider(Configuration globalConf, EventScheduler scheduler, Customer customer) {
		this.scheduler = scheduler;
		this.customer = customer;
		this.timeBetweenBilling = globalConf.getLong(IAAS_TIMEBETWEENBILLING);
		
		this.typeMapping = new HashMap<>();
		this.markets = new ArrayList<AWSMarket>();
		
		this.billingInfo = new AWSBillingInfo();
		
		AWSInstanceType[] types = parseInstanceTypes(globalConf);
		for (String name : globalConf.getStringArray(IAAS_AWS_MARKETS)) {
			double[] upfront = globalConf.getDoubleArray(IAAS_AWS_UPFRONT+name);
			double[] hourly = globalConf.getDoubleArray(IAAS_AWS_HOURLY+name);
			int quota = globalConf.getInt(IAAS_AWS_QUOTA+name, 0);
			int[] plan = globalConf.getIntegerArray(IAAS_AWS_PLAN+name);

			this.markets.add(new AWSMarket(scheduler, billingInfo, name, types, upfront, hourly, quota, plan));
		}

		this.scheduler.queueEvent(new Event(timeBetweenBilling){
			@Override
			public void trigger() {
				reportBilling();
			}
		});
	}

	private AWSInstanceType[] parseInstanceTypes(Configuration configuration) {
		
		String[] typeNames = configuration.getStringArray(IAAS_AWS_TYPES);
		String[] processors = configuration.getStringArray(IAAS_AWS_PROCESSOR);
		String[] ecus = configuration.getStringArray(IAAS_AWS_ECU);
		String[] memories = configuration.getStringArray(IAAS_AWS_MEMORY);
		String[] storages = configuration.getStringArray(IAAS_AWS_STORAGE);
		
		AWSInstanceType[] types = new AWSInstanceType[typeNames.length];
		for (int i = 0; i < typeNames.length; i++) {
			types[i] = new AWSInstanceType(typeNames[i], processors[i], ecus[i], memories[i], storages[i]);
			
			typeMapping.put(typeNames[i], types[i]);
			
		}
		
		return types;

	}

	protected void reportBilling() {
		
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

		for (AWSMarket market: markets) {
			if(market.canAcquire(type)){
				return market.acquire(type);
			}
		}
		return null;
	}

	@Override
	public void release(InstanceDescriptor descriptor) {

		for (AWSMarket market: markets) {
			market.release((AWSInstanceDescriptor) descriptor);
		}
	}

	@Override
	public boolean canAcquire(String instanceType) {
		AWSInstanceType type = parseType(instanceType);

		for (AWSMarket market: markets) {
			if(market.canAcquire(type)){
				return true;
			}
		}
		return false;
	}

	private AWSInstanceType parseType(String instanceType) {
		return typeMapping.get(instanceType);
	}
}
