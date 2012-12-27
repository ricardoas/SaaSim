package saasim.provisioning;

import java.util.LinkedList;

import org.apache.commons.configuration.ConfigurationException;

import saasim.cloud.Provider;
import saasim.cloud.User;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.schedulingheuristics.MachineStatistics;
import saasim.util.TimeUnit;


/**
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public class EC2UrgaonkarProvisioningSystem extends UrgaonkarProvisioningSystem {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4904370979372548295L;

	/**
	 * Default constructor 
	 * @param users TODO
	 * @param providers TODO
	 * @throws ConfigurationException 
	 */
	public EC2UrgaonkarProvisioningSystem(User[] users, Provider[] providers) throws ConfigurationException {
		super(users, providers);
	}

	@Override
	protected LinkedList<LinkedList<MachineDescriptor>> buildMachineList() {
		LinkedList<LinkedList<MachineDescriptor>> machineList = new LinkedList<LinkedList<MachineDescriptor>>();
		for (int i = 0; i < TimeUnit.HOUR.getMillis()/(reactiveTickInSeconds*1000); i++) {
			machineList.add(new LinkedList<MachineDescriptor>());
		}
		return machineList;
	}
	
	protected int removeMachine(MachineStatistics statistics, int tier,
			LinkedList<MachineDescriptor> availableToTurnOff, int serversToAdd) {
		int normalizedServersToAdd;
		normalizedServersToAdd = (int) Math.ceil(1.0*serversToAdd/type.getNumberOfCores());

		if(-normalizedServersToAdd >= statistics.totalNumberOfActiveServers){
			normalizedServersToAdd = 1-statistics.totalNumberOfActiveServers;
		}
		
		normalizedServersToAdd = -Math.min(-normalizedServersToAdd, availableToTurnOff.size());

		for (int i = 0; i < -normalizedServersToAdd; i++) {
			configurable.removeMachine(tier,  availableToTurnOff.poll(), forceShutdown);
		}
		return normalizedServersToAdd;
	}

	
}
