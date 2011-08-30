package commons.sim.util;

import java.lang.reflect.Constructor;

import commons.config.Configuration;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;
import commons.sim.components.MultiCoreRanjanMachine;
import commons.sim.components.MultiCoreTimeSharedMachine;
import commons.sim.components.RanjanMachine;
import commons.sim.components.TimeSharedMachine;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.schedulingheuristics.RanjanHeuristic;

public class MachineFactory {
	
	/**
	 * Unique instance
	 */
	private static MachineFactory instance;
	
	private Constructor<?> machineClass;
	
	/**
	 * Builds and gets the single instance of thsi factory.
	 * @return
	 */
	public static MachineFactory getInstance(){
		
		if(instance == null){
			instance = new MachineFactory();
		}
		return instance;
	}
	
	private MachineFactory() {
		Class<?>[] heuristicClasses = Configuration.getInstance().getApplicationHeuristics();
		boolean ranjanFactory = false;
		for(Class<?> heuristic : heuristicClasses){
			if(heuristic.equals(RanjanHeuristic.class)){
				ranjanFactory = true;
				break;
			}
		}
		
		if(ranjanFactory){//Constructing a machine factory for RANJAN heuristic
			machineClass = MultiCoreRanjanMachine.class.getConstructors()[0];
		}else{
			machineClass = MultiCoreTimeSharedMachine.class.getConstructors()[0];
		} 

	}
	
	public Machine createMachine(JEEventScheduler scheduler, MachineDescriptor descriptor, LoadBalancer loadBalancer){
		try {
			return (Machine) machineClass.newInstance(scheduler, descriptor, loadBalancer);
		} catch (Exception e) {
			throw new RuntimeException("Error instantiating new machine. Check constructor availability.", e);
		}
	}
}
