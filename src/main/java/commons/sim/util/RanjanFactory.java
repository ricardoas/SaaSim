package commons.sim.util;

import commons.sim.components.Machine;
import commons.sim.components.RanjanMachine;
import commons.sim.jeevent.JEEventScheduler;

public class RanjanFactory extends MachineFactory {

	@Override
	public Machine createMachine(JEEventScheduler scheduler, long machineID, boolean isReserved) {
		return new RanjanMachine(scheduler, machineID, isReserved);
	}

	@Override
	public Machine createMachine(JEEventScheduler scheduler, long machineID) {
		return new RanjanMachine(scheduler, machineID);
	}
}
