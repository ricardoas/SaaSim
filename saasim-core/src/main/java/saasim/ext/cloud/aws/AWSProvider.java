package saasim.ext.cloud.aws;

import saasim.core.cloud.IaaSProvider;
import saasim.core.config.Configuration;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.util.TimeUnit;

import com.google.inject.Inject;

public class AWSProvider implements IaaSProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	public AWSProvider(Configuration config) {
		
	}

	@Override
	public void makeReservation(AWSInstanceType instanceType, TimeUnit duration) {
		// TODO Auto-generated method stub
	}

	@Override
	public InstanceDescriptor acquire(AWSInstanceType instanceType) {
		return new AWSInstanceDescriptor(instanceType);
	}

	@Override
	public void release(InstanceDescriptor descriptor) {
		// TODO Auto-generated method stub

	}

}
