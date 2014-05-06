package saasim.ext.cloud;

import saasim.core.cloud.IaaSProvider;
import saasim.core.cloud.InstanceType;
import saasim.core.config.Configuration;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.util.TimeUnit;

import com.google.inject.Inject;

public class AmazonEC2 implements IaaSProvider {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	public AmazonEC2(Configuration config) {
		System.out.println("AmazonEC2.AmazonEC2() config="+ config);
	}

	@Override
	public void makeReservation(InstanceType instanceType, TimeUnit duration) {
		// TODO Auto-generated method stub

	}

	@Override
	public InstanceDescriptor acquire(InstanceType instanceType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void release(InstanceDescriptor descriptor) {
		// TODO Auto-generated method stub

	}

}
