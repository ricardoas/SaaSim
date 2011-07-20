package commons.sim.util;

import java.util.List;

import provisioning.Monitor;

import commons.config.SimulatorConfiguration;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.schedulingheuristics.SchedulingHeuristic;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class SimpleApplicationFactory extends ApplicationFactory {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoadBalancer createNewApplication(JEEventScheduler scheduler,
			Monitor monitor, List<Machine> setupMachines) {
		SimulatorConfiguration config = SimulatorConfiguration.getInstance();
		int numOfTiers = config.getApplicationNumOfTiers();
		Class<?>[] heuristicClasses = config.getApplicationHeuristics();
		int [] serverPerTier = config.getApplicationInitialServersPerTier();
		int [] maxServerPerTier = config.getApplicationMaxServersPerTier();
		
		LoadBalancer entryPoint = buildLoadBalancer(scheduler, monitor, heuristicClasses[0], serverPerTier[0], maxServerPerTier[0], setupMachines);
		LoadBalancer currentTier = entryPoint;
		for (int i = 1; i < numOfTiers; i++) {
			LoadBalancer nextTier = buildLoadBalancer(scheduler, monitor, heuristicClasses[i], serverPerTier[i], maxServerPerTier[i], setupMachines);
			linkTiers(currentTier, nextTier);
			currentTier = nextTier;
		}
		return entryPoint;
	}

	/**
	 * @param formerTier
	 * @param latterTier
	 */
	private void linkTiers(LoadBalancer formerTier, LoadBalancer latterTier) {
		for (Machine machine : formerTier.getServers()) {
//				machine.setLoadBalancer(latterTier); TODO add this feature to allow multiple tiers
		}
	}

	/**
	 * @param scheduler
	 * @param monitor
	 * @param heuristic
	 * @param serverPerTier 
	 * @param maxServerPerTier 
	 * @param setupMachines 
	 * @return
	 */
	private LoadBalancer buildLoadBalancer(JEEventScheduler scheduler, Monitor monitor,
			Class<?> heuristic, int serverPerTier, int maxServerPerTier, List<Machine> setupMachines) {
		try {
			Machine [] servers = new Machine[serverPerTier];
			for (int i = 0; i < servers.length; i++) {
				servers[i] = setupMachines.remove(0);
			}
			return new LoadBalancer(scheduler, monitor, 
					(SchedulingHeuristic) heuristic.newInstance(), 
					maxServerPerTier, servers);
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ heuristic, e);
		}
	}

}
