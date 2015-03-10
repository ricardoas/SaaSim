package saasim.core.deprovisioning;

import java.util.List;

import saasim.core.infrastructure.InstanceDescriptor;

/**
 * It models a component responsible for deciding which instances should be turned off.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface DeprovisioningSystem {
	
	/**
	 * @param A list of {@link InstanceDescriptor} to choose one to turn off.
	 * @return The chosen one.
	 */
	InstanceDescriptor chooseMachineToTurnOff(List<InstanceDescriptor> instances);

}
