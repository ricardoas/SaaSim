package saasim.core.application;

import com.google.inject.Inject;

import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.AdmissionControl;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.LoadBalancer;
import saasim.core.infrastructure.Machine;
import saasim.core.provisioning.TierConfiguration;



/**
 * This tier can scale vertically.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class VerticallyScalableTier extends AbstractTier{
	
	@Inject
	public VerticallyScalableTier(EventScheduler scheduler,
			AdmissionControl admissionControl, LoadBalancer loadBalancer) {
		super(scheduler, admissionControl, loadBalancer);
	}

	@Override
	public void config(TierConfiguration tierConfiguration) {
		
		switch (tierConfiguration.getAction()) {
		case INCREASE:
		case DECREASE:
			reconfigure(tierConfiguration.getDescriptors(), tierConfiguration.isForce());
			break;
		default:
			throw new RuntimeException("Unknown action of configuration!");
		}
	}
	
	/**
	 * Scales up or down a {@link Machine}.
	 * 
	 * @param machineDescriptor {@link InstanceDescriptor} of the new server.
	 */
	private void reconfigure(InstanceDescriptor[] instanceDescriptors, boolean force){
		for (InstanceDescriptor instanceDescriptor : instanceDescriptors) {
			loadBalancer.reconfigureMachine(instanceDescriptor, !force);
		}
	}
}
