package saasim.core.application;

import saasim.core.infrastructure.AdmissionControl;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.Machine;
import saasim.core.provisioning.TierConfiguration;

import com.google.inject.Inject;



/**
 * This tier can scale vertically.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class VerticallyScalableTier extends AbstractTier{
	
	@Inject
	public VerticallyScalableTier(AdmissionControl admissionControl) {
		super(admissionControl);
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
			admissionControl.getLoadBalancer().reconfigureMachine(instanceDescriptor, !force);
		}
	}
}
