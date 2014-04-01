package saasim.core.application;

import saasim.core.infrastructure.InstanceDescriptor;



/**
 * This tier can scale horizontally.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface HorizontallyScalableTier extends Tier{
	
	/**
	 * Adds a new instance to this {@link Tier}
	 * 
	 * @param machineDescriptor {@link InstanceDescriptor} of the new server.
	 */
	void scaleIn(InstanceDescriptor machineDescriptor);
	
	/**
	 * Removes the instance described by {@link InstanceDescriptor} from this {@link Tier}.
	 *  
	 * @param force <code>true</code> to remove immediately, otherwise to gracefully remove it.
	 */
	void scaleOut(InstanceDescriptor descriptor, boolean force);

}