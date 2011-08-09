package provisioning;

import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;

public class ProfitDrivenProvisioningSystem extends DynamicProvisioningSystem{

	public ProfitDrivenProvisioningSystem(JEEventScheduler scheduler) {
		super(scheduler);
	}
	
	@Override
	protected void handleEventRequestQueued(JEEvent event) {
		evaluateMachinesToBeAdded();
	}
	
	private void evaluateMachinesToBeAdded() {
		boolean canAddAReservedMachine = this.accountingSystem.canAddAReservedMachine();
		boolean canAddAOnDemandMachine = this.accountingSystem.canAddAOnDemandMachine();
		
		if(canAddAReservedMachine){
			MachineDescriptor descriptor = new MachineDescriptor(availableIDs++, canAddAReservedMachine, getScheduler().now().timeMilliSeconds);
			this.configurable.addServer(0, descriptor);
			//Registering machines for accounting
			this.accountingSystem.createMachine(descriptor);
		}else if(canAddAOnDemandMachine){
			MachineDescriptor  descriptor = new MachineDescriptor(availableIDs++, canAddAReservedMachine, getScheduler().now().timeMilliSeconds);
			this.configurable.addServer(0, descriptor);
			//Registering machines for accounting
			this.accountingSystem.createMachine(descriptor);
		}
	}
}
