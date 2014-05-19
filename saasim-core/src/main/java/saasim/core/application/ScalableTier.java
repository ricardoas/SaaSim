package saasim.core.application;

import static saasim.core.config.Configuration.ACTION;
import static saasim.core.config.Configuration.ACTION_DECREASE;
import static saasim.core.config.Configuration.ACTION_INCREASE;
import static saasim.core.config.Configuration.ACTION_RECONFIGURE;
import static saasim.core.config.Configuration.FORCE;
import static saasim.core.config.Configuration.INSTANCE_DESCRIPTOR;

import java.util.HashMap;
import java.util.Map;

import saasim.core.config.Configuration;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.LoadBalancer;
import saasim.core.infrastructure.Machine;
import saasim.core.infrastructure.MonitoringService;

import com.google.inject.Inject;



/**
 * {@link Tier} able to scale the infrastructure up, down, in or out.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class ScalableTier extends AbstractTier{
	
	protected final LoadBalancer loadBalancer;

	/**
	 * Default constructor
	 * @param loadBalancer A {@link LoadBalancer}.
	 */
	@Inject
	public ScalableTier(LoadBalancer loadBalancer, MonitoringService monitor) {
		super();
		this.loadBalancer = loadBalancer;
		monitor.setMonitorable(this);
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
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.application.Tier#configure(saasim.core.config.Configuration)
	 */
	@Override
	public void configure(Configuration configuration) {
		
		switch (configuration.getString(ACTION)) {
		case ACTION_INCREASE:
			scaleIn((InstanceDescriptor) configuration.getProperty(INSTANCE_DESCRIPTOR), configuration.getBoolean(FORCE));
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
	 * Reconfigures a {@link Machine}.
	 * 
	 * @param machineDescriptor {@link InstanceDescriptor} of the new server.
	 */
	private void reconfigure(InstanceDescriptor instanceDescriptor, boolean force){
		this.loadBalancer.reconfigureMachine(instanceDescriptor, !force);
	}
	
	@Override
	public Map<String, Double> collect(long now, long elapsedTime) {
		return new HashMap<String, Double>();
	}

}