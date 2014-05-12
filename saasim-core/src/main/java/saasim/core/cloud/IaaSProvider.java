package saasim.core.cloud;

import java.io.Serializable;

import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.util.TimeUnit;
import saasim.ext.cloud.aws.AWSInstanceType;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface IaaSProvider extends Serializable {
	
	void makeReservation(AWSInstanceType instanceType, TimeUnit duration);
	
	InstanceDescriptor acquire(AWSInstanceType instanceType);
	
	void release(InstanceDescriptor descriptor);

}
