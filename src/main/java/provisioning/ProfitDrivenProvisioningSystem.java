package provisioning;

import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.MachineFactory;

public class ProfitDrivenProvisioningSystem extends DynamicProvisioningSystem{

	public ProfitDrivenProvisioningSystem(JEEventScheduler scheduler) {
		super(scheduler);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void handleEventRequestQueued(JEEvent event) {
		evaluateMachinesToBeAdded();
	}
	
	private void evaluateMachinesToBeAdded() {
		boolean canAddAReservedMachine = this.accountingSystem.canAddAReservedMachine();
		boolean canAddAOnDemandMachine = this.accountingSystem.canAddAOnDemandMachine();
		MachineFactory machineFactory = MachineFactory.getInstance();
		
		if(canAddAReservedMachine){
			this.loadBalancer.addServer(machineFactory.createMachine(getScheduler(), availableIDs++, canAddAReservedMachine));
			//Registering machines for accounting
			this.accountingSystem.createMachine(availableIDs-1, canAddAReservedMachine, getScheduler().now().timeMilliSeconds);
		}else if(canAddAOnDemandMachine){
			this.loadBalancer.addServer(machineFactory.createMachine(getScheduler(), availableIDs++, canAddAOnDemandMachine));
			//Registering machines for accounting
			this.accountingSystem.createMachine(availableIDs-1, canAddAOnDemandMachine, getScheduler().now().timeMilliSeconds);
		}
	}
}
