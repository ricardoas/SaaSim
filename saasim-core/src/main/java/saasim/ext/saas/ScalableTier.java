package saasim.ext.saas;

import static saasim.core.config.Configuration.ACTION;
import static saasim.core.config.Configuration.ACTION_DECREASE;
import static saasim.core.config.Configuration.ACTION_INCREASE;
import static saasim.core.config.Configuration.ACTION_RECONFIGURE;
import static saasim.core.config.Configuration.MACHINE;
import saasim.core.config.Configuration;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.infrastructure.LoadBalancer;
import saasim.core.infrastructure.Machine;
import saasim.core.saas.Request;
import saasim.core.saas.Tier;

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
	public ScalableTier(LoadBalancer loadBalancer) {
		super();
		this.loadBalancer = loadBalancer;
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.saas.Tier#queue(saasim.core.saas.Request)
	 */
	@Override
	public void queue(Request request) {
		this.loadBalancer.queue(request);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.saas.Tier#configure(saasim.core.config.Configuration)
	 */
	@Override
	public void configure(Configuration configuration) {
		
		switch (configuration.getString(ACTION)) {
		case ACTION_INCREASE:
			scaleIn((Machine) configuration.getProperty(MACHINE));
			break;
		case ACTION_DECREASE:
			scaleOut((Machine) configuration.getProperty(MACHINE));
			break;
		case ACTION_RECONFIGURE:
			reconfigure((Machine) configuration.getProperty(MACHINE));
		default:
			throw new RuntimeException("Unknown action of configuration!");
		}
	}
	
	/**
	 * Adds a new instance to this {@link Tier}
	 * @param machine TODO
	 * @param machineDescriptor {@link InstanceDescriptor} of the new server.
	 */
	private void scaleIn(Machine machine){
		this.loadBalancer.addMachine(machine);
	}
	
	/**
	 * Removes the instance described by {@link InstanceDescriptor} from this {@link Tier}.
	 * @param machine TODO
	 */
	private void scaleOut(Machine machine){
		this.loadBalancer.removeMachine(machine);
	}
	
	/**
	 * Reconfigures a {@link Machine}.
	 * @param machine TODO
	 * @param machineDescriptor {@link InstanceDescriptor} of the new server.
	 */
	private void reconfigure(Machine machine){
		this.loadBalancer.reconfigureMachine(machine);
	}
}