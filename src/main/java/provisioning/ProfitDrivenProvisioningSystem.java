package provisioning;

import java.util.ArrayList;
import java.util.List;

import commons.cloud.Request;
import commons.config.SimulatorConfiguration;
import commons.sim.AccountingSystem;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.MachineFactory;

public class ProfitDrivenProvisioningSystem extends JEAbstractEventHandler implements DPS{

	private long availableIDs;
	
	private LoadBalancer loadBalancer;
	
	private AccountingSystem accountingSystem;

	public ProfitDrivenProvisioningSystem(JEEventScheduler scheduler, LoadBalancer loadBalancer) {
		super(scheduler);
		availableIDs = 0;
		
		this.loadBalancer = loadBalancer;
	}
	
	@Override
	public void handleEvent(JEEvent event) {
		// TODO Auto-generated method stub
		switch (event.getType()) {
			case MACHINE_TURNED_OFF:
				Machine machine = (Machine) event.getValue()[0];
				this.accountingSystem.reportMachineFinish(machine.getMachineID(), event.getScheduledTime().timeMilliSeconds);
				break;
			case EVALUATEUTILIZATION:
				//Nothing to do
				break;
			case REQUESTQUEUED:
				evaluateMachinesToBeAdded();
				break;
		}
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
	

	@Override
	public List<Machine> getSetupMachines() {
		int[] initialServersPerTier = SimulatorConfiguration.getInstance().getApplicationInitialServersPerTier();
		int totalServers = 0;
		List<Machine> machines = new ArrayList<Machine>();
		for (int i : initialServersPerTier) {
			totalServers += i;
		}
		MachineFactory machineFactory = MachineFactory.getInstance();
		for (int i = 0; i < totalServers; i++) {
			machines.add(machineFactory.createMachine(getScheduler(), availableIDs++, i < 20));
			
			//Registering machines for accounting
			this.accountingSystem.createMachine(availableIDs-1, i < 20, getScheduler().now().timeMilliSeconds);
		}
		return machines;
	}

	@Override
	public void setConfigurable(DynamicallyConfigurable configurable) {
		// TODO Auto-generated method stub
	}

	@Override
	public void reportRequestFinished(Request requestFinished) {
		this.accountingSystem.reportRequestFinished(requestFinished);
	}
	
	@Override
	public void setAccountingSystem(AccountingSystem system){
		this.accountingSystem = system;
	}

	@Override
	public AccountingSystem getAccountingSystem() {
		return this.accountingSystem;
	}
}
