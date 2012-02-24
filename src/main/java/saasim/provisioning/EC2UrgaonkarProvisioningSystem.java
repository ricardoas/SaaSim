package saasim.provisioning;

import java.util.LinkedList;

import saasim.provisioning.util.DPSInfo;
import saasim.sim.components.MachineDescriptor;
import saasim.util.TimeUnit;


/**
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public class EC2UrgaonkarProvisioningSystem extends UrgaonkarProvisioningSystem {
	
	/**
	 * Default constructor 
	 */
	public EC2UrgaonkarProvisioningSystem() {
		super();
	}

	@Override
	protected LinkedList<LinkedList<MachineDescriptor>> buildMachineList(
			DPSInfo info) {
		LinkedList<LinkedList<MachineDescriptor>> machineList = new LinkedList<LinkedList<MachineDescriptor>>();
		for (int i = 0; i < TimeUnit.HOUR.getMillis()/(reactiveTickInSeconds*1000); i++) {
			machineList.add(new LinkedList<MachineDescriptor>());
		}
		return machineList;
	}
	
}
