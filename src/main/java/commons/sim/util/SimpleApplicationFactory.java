package commons.sim.util;

import java.util.ArrayList;
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
	public List<LoadBalancer> createNewApplication(JEEventScheduler scheduler,
			Monitor monitor, List<Machine> setupMachines) {
		SimulatorConfiguration config = SimulatorConfiguration.getInstance();
		int numOfTiers = config.getApplicationNumOfTiers();
		
		Class<?>[] heuristicClasses = config.getApplicationHeuristics();
		int [] serversPerTier = config.getApplicationInitialServersPerTier();
		int [] maxServerPerTier = config.getApplicationMaxServersPerTier();
		
		List<LoadBalancer> loadBalancers = new ArrayList<LoadBalancer>();
		
		for (int i = 0; i < numOfTiers; i++) {
			loadBalancers.add(buildLoadBalancer(scheduler, monitor, heuristicClasses[i], serversPerTier[i], maxServerPerTier[i], setupMachines));
		}
		
		return loadBalancers;
	}

	/**
	 * @param scheduler
	 * @param monitor
	 * @param heuristic
	 * @param serversPerTier 
	 * @param maxServerPerTier 
	 * @param setupMachines 
	 * @return
	 */
	private LoadBalancer buildLoadBalancer(JEEventScheduler scheduler, Monitor monitor,
			Class<?> heuristic, int serversPerTier, int maxServerPerTier, List<Machine> setupMachines) {
		try {
			Machine [] servers = new Machine[serversPerTier];
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
