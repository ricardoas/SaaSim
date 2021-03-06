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
public class SimpleTier extends AbstractTier{
	
	private Machine server;

	/**
	 * Default constructor
	 * @param loadBalancer A {@link LoadBalancer}.
	 */
	@Inject
	public SimpleTier() {
		super();
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.saas.Tier#queue(saasim.core.saas.Request)
	 */
	@Override
	public void queue(Request request) {
		this.server.queue(request);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.saas.Tier#configure(saasim.core.config.Configuration)
	 */
	@Override
	public void configure(Configuration configuration) {
		
		switch (configuration.getString(ACTION)) {
		case ACTION_RECONFIGURE:
			reconfigure((Machine) configuration.getProperty(MACHINE));
		case ACTION_INCREASE:
			if(server == null){
				scaleIn((Machine) configuration.getProperty(MACHINE));
			}
			break;
		case ACTION_DECREASE:
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
	private void scaleIn(Machine machine){
		
	}
	
	/**
	 * Reconfigures a {@link Machine}.
	 * 
	 * @param machineDescriptor {@link InstanceDescriptor} of the new server.
	 */
	private void reconfigure(Machine machine){
		
	}
}