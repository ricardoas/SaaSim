package saasim.core.application;

import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.LoadBalancer;
import saasim.core.infrastructure.Machine;
import saasim.core.provisioning.TierConfiguration;

import com.google.inject.Inject;



/**
 * This tier can scale horizontally.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class ScalableTier extends AbstractTier implements Tier{
	
	@Inject
	public ScalableTier(LoadBalancer loadBalancer) {
		super(loadBalancer);
	}

	@Override
	public void config(TierConfiguration tierConfiguration) {
		
		switch (tierConfiguration.getAction()) {
		case INCREASE:
			scaleIn(tierConfiguration.getDescriptors(), tierConfiguration.isForce());
			break;
		case DECREASE:
			scaleOut(tierConfiguration.getDescriptors(), tierConfiguration.isForce());
			break;
		case CONFIGURE_MACHINE:
			reconfigure(tierConfiguration.getDescriptors(), tierConfiguration.isForce());
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
			getLoadBalancer().addMachine(instanceDescriptor, !force);
		}
	}
	
	/**
	 * Removes the instance described by {@link InstanceDescriptor} from this {@link Tier}.
	 *  
	 * @param force <code>true</code> to remove immediately, otherwise to gracefully remove it.
	 */
	private void scaleOut(InstanceDescriptor[] instanceDescriptors, boolean force){
		for (InstanceDescriptor instanceDescriptor : instanceDescriptors) {
			getLoadBalancer().removeMachine(instanceDescriptor, force);
		}
	}
	
	/**
	 * Scales up or down a {@link Machine}.
	 * 
	 * @param machineDescriptor {@link InstanceDescriptor} of the new server.
	 */
	private void reconfigure(InstanceDescriptor[] instanceDescriptors, boolean force){
		for (InstanceDescriptor instanceDescriptor : instanceDescriptors) {
			getLoadBalancer().reconfigureMachine(instanceDescriptor, !force);
		}
	}
}