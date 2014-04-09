package saasim.core.application;

import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.provisioning.TierConfiguration;



/**
 * This tier can scale vertically.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class VerticallyScalableTier extends AbstractTier{
	
	@Override
	public void config(TierConfiguration tierConfiguration) {
		
		switch (tierConfiguration.getAction()) {
		case INCREASE:
			scaleUp(tierConfiguration.getDescriptors(), tierConfiguration.isForce());
			break;
		case DECREASE:
			scaleDown(tierConfiguration.getDescriptors(), tierConfiguration.isForce());
			break;
		default:
			throw new RuntimeException("Unknown action of configuration!");
		}
	}
	
	/**
	 * Scales up this tier.
	 * 
	 * @param machineDescriptor {@link InstanceDescriptor} of the new server.
	 */
	private void scaleUp(InstanceDescriptor[] instanceDescriptors, boolean force){
		for (InstanceDescriptor instanceDescriptor : instanceDescriptors) {
			loadBalancer.addMachine(instanceDescriptor, !force);
		}
	}
	
	/**
	 * Scales down this tier.
	 *  
	 * @param force <code>true</code> to remove immediately, and <code>false</code> to stop scheduling and wait
	 * until machine becomes idle to remove.
	 */
	private void scaleDown(InstanceDescriptor[] instanceDescriptors, boolean force){
		for (InstanceDescriptor instanceDescriptor : instanceDescriptors) {
			loadBalancer.removeMachine(instanceDescriptor, force);
		}
	}
}
