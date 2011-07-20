package provisioning;

import java.util.ArrayList;
import java.util.List;

import commons.config.SimulatorConfiguration;
import commons.sim.Simulator;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;

public class StaticProvisioningSystem extends JEAbstractEventHandler implements DPS{

	private long availableIDs;
	private Simulator simulator;

	public StaticProvisioningSystem(JEEventScheduler scheduler) {
		super(scheduler);
		availableIDs = 0;
	}

	@Override
	public void handleEvent(JEEvent event) {
		// TODO Auto-generated method stub
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
	public void setConfigurable(Simulator simulator) {
		this.simulator = simulator;
	}

}
