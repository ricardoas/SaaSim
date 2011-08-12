package commons.sim.util;

import static commons.sim.util.SimulatorProperties.APPLICATION_NUM_OF_TIERS;

import java.util.ArrayList;
import java.util.List;

import provisioning.Monitor;

import commons.config.Configuration;
import commons.sim.components.LoadBalancer;
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
			Monitor monitor) {
		Configuration config = Configuration.getInstance();
		int numOfTiers = config.getInt(APPLICATION_NUM_OF_TIERS);
		
		Class<?>[] heuristicClasses = config.getApplicationHeuristics();
		int [] serversPerTier = config.getApplicationInitialServersPerTier();
		int [] maxServerPerTier = config.getApplicationMaxServersPerTier();
		
		List<LoadBalancer> loadBalancers = new ArrayList<LoadBalancer>();
		
		for (int i = 0; i < numOfTiers; i++) {
			loadBalancers.add(buildLoadBalancer(scheduler, monitor, heuristicClasses[i], serversPerTier[i], maxServerPerTier[i], i));
		}
		
		return loadBalancers;
	}

	/**
	 * @param scheduler
	 * @param monitor
	 * @param heuristic
	 * @param serversPerTier 
	 * @param maxServerPerTier 
	 * @param tier 
	 * @return
	 */
	private LoadBalancer buildLoadBalancer(JEEventScheduler scheduler, Monitor monitor,
			Class<?> heuristic, int serversPerTier, int maxServerPerTier, int tier) {
		try {
			return new LoadBalancer(scheduler, monitor, 
					(SchedulingHeuristic) heuristic.newInstance(), 
					maxServerPerTier, tier);
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ heuristic, e);
		}
	}

}
