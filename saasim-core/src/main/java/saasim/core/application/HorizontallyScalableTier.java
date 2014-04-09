package saasim.core.application;

import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.provisioning.TierConfiguration;



/**
 * This tier can scale horizontally.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class HorizontallyScalableTier extends AbstractTier implements Tier{
	
	@Override
	public void config(TierConfiguration tierConfiguration) {
		
		switch (tierConfiguration.getAction()) {
		case INCREASE:
			scaleIn(tierConfiguration.getDescriptors(), tierConfiguration.isForce());
			break;
		case DECREASE:
			scaleOut(tierConfiguration.getDescriptors(), tierConfiguration.isForce());
			break;
		default:
			throw new RuntimeException("Unknown action of configuration!");
		}
	}
	
	/**
	 * Adds a new instance to this {@link Tier}
	 * 
	 * @param machineDescriptor {@link InstanceDescriptor} of the new server.
	 * @param force <code>true</code> to add immediately, otherwise there is a boot/set up time. 
	 */
	private void scaleIn(InstanceDescriptor[] instanceDescriptors, boolean force){
		for (InstanceDescriptor instanceDescriptor : instanceDescriptors) {
			loadBalancer.addMachine(instanceDescriptor, !force);
		}
	}
	
	/**
	 * Removes the instance described by {@link InstanceDescriptor} from this {@link Tier}.
	 *  
	 * @param force <code>true</code> to remove immediately, otherwise to gracefully remove it.
	 */
	private void scaleOut(InstanceDescriptor[] instanceDescriptors, boolean force){
		for (InstanceDescriptor instanceDescriptor : instanceDescriptors) {
			loadBalancer.removeMachine(instanceDescriptor, force);
		}
	}

}