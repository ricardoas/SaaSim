package saasim.core.application;

import static saasim.core.provisioning.ApplicationConfiguration.ACTION;
import static saasim.core.provisioning.ApplicationConfiguration.ACTION_DECREASE;
import static saasim.core.provisioning.ApplicationConfiguration.ACTION_INCREASE;
import static saasim.core.provisioning.ApplicationConfiguration.ACTION_RECONFIGURE;
import static saasim.core.provisioning.ApplicationConfiguration.FORCE;
import static saasim.core.provisioning.ApplicationConfiguration.INSTANCE_DESCRIPTOR;
import saasim.core.config.Configuration;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.LoadBalancer;
import saasim.core.infrastructure.Machine;

import com.google.inject.Inject;



/**
 * This tier can scale horizontally.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class ScalableTier extends AbstractTier{
	
	protected final LoadBalancer loadBalancer;
	private Configuration configuration;

	@Inject
	public ScalableTier(Configuration configuration, LoadBalancer loadBalancer) {
		super();
		this.configuration = configuration;
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
	public void configure() {
		
		System.out.println(this.configuration.getProperty(INSTANCE_DESCRIPTOR));
		switch (configuration.getString(ACTION)) {
		case ACTION_INCREASE:
			scaleIn((InstanceDescriptor) this.configuration.getProperty(INSTANCE_DESCRIPTOR), configuration.getBoolean(FORCE));
			break;
		case ACTION_DECREASE:
			scaleOut((InstanceDescriptor) configuration.getProperty(INSTANCE_DESCRIPTOR), configuration.getBoolean(FORCE));
			break;
		case ACTION_RECONFIGURE:
			reconfigure((InstanceDescriptor) configuration.getProperty(INSTANCE_DESCRIPTOR), configuration.getBoolean(FORCE));
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