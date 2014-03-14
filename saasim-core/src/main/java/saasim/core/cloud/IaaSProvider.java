package saasim.core.cloud;

import java.io.Serializable;

import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.util.TimeUnit;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface IaaSProvider extends Serializable {
	
	void makeReservation(InstanceType instanceType, TimeUnit duration);
	
	InstanceDescriptor acquire(InstanceType instanceType);
	
	void release(InstanceDescriptor descriptor);

}
