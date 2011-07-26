package provisioning;

import java.util.ArrayList;
import java.util.List;

import commons.cloud.Request;
import commons.config.SimulatorConfiguration;
import commons.sim.AccountingSystem;
import commons.sim.Simulator;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;
import commons.util.Triple;

public class StaticProvisioningSystem extends JEAbstractEventHandler implements DPS{

	private long availableIDs;
	private Simulator simulator;
	private AccountingSystem accountingSystem;

	public StaticProvisioningSystem(JEEventScheduler scheduler) {
		super(scheduler);
		availableIDs = 0;
		
		this.accountingSystem = new AccountingSystem();
	}

	@Override
	public void handleEvent(JEEvent event) {
		// TODO Auto-generated method stub
		switch (event.getType()) {
			case MACHINE_TURNED_OFF:
				Machine machine = (Machine) event.getValue()[0];
				this.accountingSystem.reportMachineUtilization(machine.getMachineID(), event.getScheduledTime().timeMilliSeconds);
				break;
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
		for (int i = 0; i < totalServers; i++) {
			machines.add(new Machine(getScheduler(), availableIDs++, i < 20));
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
	public void reportUtilization(double utilization, List<Triple> arrivalsAndCompletions) {
		// TODO Auto-generated method stub
		
	}

}
