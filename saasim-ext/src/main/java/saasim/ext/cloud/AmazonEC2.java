package saasim.ext.cloud;

import saasim.core.application.InstanceDescriptor;
import saasim.core.cloud.IaaSProvider;
import saasim.core.cloud.InstanceType;
import saasim.core.util.TimeUnit;

public class AmazonEC2 implements IaaSProvider {

	public AmazonEC2() {
		
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
