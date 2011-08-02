package commons.sim.util;

import commons.config.SimulatorConfiguration;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.schedulingheuristics.RanjanHeuristic;

public abstract class MachineFactory {
	
	/**
	 * Unique instance
	 */
	private static MachineFactory instance;
	
	/**
	 * Builds and gets the single instance of thsi factory.
	 * @return
	 */
	public static MachineFactory getInstance(){
		
		if(instance == null){
			Class<?>[] heuristicClasses = SimulatorConfiguration.getInstance().getApplicationHeuristics();
			boolean ranjanFactory = false;
			for(Class heuristic : heuristicClasses){
				if(heuristic.equals(RanjanHeuristic.class)){
					ranjanFactory = true;
					break;
				}
			}
			
			if(ranjanFactory){//Constructing a machine factory for RANJAN heuristic
				instance = new RanjanFactory();
			}else{
				instance = new CommonMachinesFactory();
			}
		}
		return instance;
	}

	/**
	 * 
	 * @param scheduler
	 * @param monitor
	 * @param setupMachines 
	 * @return
	 */
	public abstract Machine createMachine(JEEventScheduler scheduler, long id, boolean isReserved);
	
	public abstract Machine createMachine(JEEventScheduler scheduler, long machineID);

}
