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
public class ScalableTier extends AbstractTier{
	
	protected final LoadBalancer loadBalancer;

	@Inject
	public ScalableTier(LoadBalancer loadBalancer) {
		super();
		this.loadBalancer = loadBalancer;
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.application.Tier#queue(saasim.core.application.Request)
	 */
	@Override
	public void queue(Request request) {
		request.setResponseListener(this);
		this.loadBalancer.queue(request);
	}


	@Override
	public void config(TierConfiguration tierConfiguration) {
		
		switch (tierConfiguration.getAction()) {
		case INCREASE:
			scaleIn(tierConfiguration.getDescriptor(), tierConfiguration.isForce());
			break;
		case DECREASE:
			scaleOut(tierConfiguration.getDescriptor(), tierConfiguration.isForce());
			break;
		case CONFIGURE_MACHINE:
			reconfigure(tierConfiguration.getDescriptor(), tierConfiguration.isForce());
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
	private void scaleIn(InstanceDescriptor instanceDescriptor, boolean force){
		this.loadBalancer.addMachine(instanceDescriptor, !force);
	}
	
	/**
	 * Removes the instance described by {@link InstanceDescriptor} from this {@link Tier}.
	 *  
	 * @param force <code>true</code> to remove immediately, otherwise to gracefully remove it.
	 */
	private void scaleOut(InstanceDescriptor instanceDescriptor, boolean force){
		this.loadBalancer.removeMachine(instanceDescriptor, force);
	}
	
	/**
	 * Scales up or down a {@link Machine}.
	 * 
	 * @param machineDescriptor {@link InstanceDescriptor} of the new server.
	 */
	private void reconfigure(InstanceDescriptor instanceDescriptor, boolean force){
		this.loadBalancer.reconfigureMachine(instanceDescriptor, !force);
	}
}