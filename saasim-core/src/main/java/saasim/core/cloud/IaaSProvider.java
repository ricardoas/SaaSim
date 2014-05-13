package saasim.core.cloud;

import java.io.Serializable;

import saasim.core.infrastructure.InstanceDescriptor;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface IaaSProvider extends Serializable {
	
	public static final String IAAS_TIMEBETWEENBILLING = "iaas.timebetweenbilling";
	public static final String IAAS_QUOTA = "iaas.quota";
	
	InstanceDescriptor acquire(String instanceType);
	
	boolean canAcquire(String instanceType);
	
	void release(InstanceDescriptor descriptor);
	
}
