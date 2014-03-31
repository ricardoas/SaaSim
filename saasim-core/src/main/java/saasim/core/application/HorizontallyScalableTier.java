package saasim.core.application;

import saasim.core.infrastructure.InstanceDescriptor;



/**
 * This tier can scale horizontally.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface HorizontallyScalableTier extends Tier{
	
	/**
	 * Scales up this tier.
	 * 
	 * @param machineDescriptor {@link InstanceDescriptor} of the new server.
	 */
	void scaleIn(InstanceDescriptor machineDescriptor);
	
	/**
	 * Scales down this tier.
	 *  
	 * @param force <code>true</code> to remove immediately, and <code>false</code> to stop scheduling and wait
	 * until machine becomes idle to remove.
	 */
	void scaleOut(InstanceDescriptor descriptor, boolean force);

}