package provisioning;

import java.util.ArrayList;
import java.util.List;

import commons.cloud.Request;
import commons.config.SimulatorConfiguration;
import commons.sim.AccountingSystem;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.MachineFactory;

public class DynamicProvisioningSystem extends JEAbstractEventHandler implements DPS{

	protected long availableIDs;
	
	protected AccountingSystem accountingSystem;
	
	protected DynamicallyConfigurable configurable;

	public DynamicProvisioningSystem(JEEventScheduler scheduler) {
		super(scheduler);
		this.availableIDs = 0;
	}
	
	@Override
	public void handleEvent(JEEvent event) {
		// TODO Auto-generated method stub
		switch (event.getType()) {
			case MACHINE_TURNED_OFF:
				handleEventMachineTurnedOff(event);
				break;
			case REQUESTQUEUED:
				handleEventRequestQueued(event);
				break;
			case EVALUATEUTILIZATION:
				handleEventEvaluateUtilization(event);
				break;
			default:
				//FIXME throw an exception?
				break;
		}
	}
	
	/**
	 * @param timeInMillis
	 * @param machine
	 */
	protected void handleEventMachineTurnedOff(JEEvent event){
		this.accountingSystem.reportMachineFinish(((Machine)event.getValue()[0]).getMachineID(), event.getScheduledTime().timeMilliSeconds);
	}

	protected void handleEventRequestQueued(JEEvent event) {
	}

	protected void handleEventEvaluateUtilization(JEEvent event) {
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
		this.configurable = configurable;
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
