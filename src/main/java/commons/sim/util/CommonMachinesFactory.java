package commons.sim.util;

import commons.sim.components.Machine;
import commons.sim.jeevent.JEEventScheduler;

public class CommonMachinesFactory extends MachineFactory {

	@Override
	public Machine createMachine(JEEventScheduler scheduler, long machineID, boolean isReserved) {
		return new Machine(scheduler, machineID, isReserved);
	}

	@Override
	public Machine createMachine(JEEventScheduler scheduler, long machineID) {
		return new Machine(scheduler, machineID);
	}
}
