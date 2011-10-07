package commons.sim.util;

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
	public LoadBalancer[] createNewApplication(JEEventScheduler scheduler, Monitor monitor) {
		Configuration config = Configuration.getInstance();
		int numOfTiers = config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS);
		
		Class<?>[] heuristicClasses = config.getApplicationHeuristics();
		int [] maxServerPerTier = config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER);

		LoadBalancer [] loadBalancers = new LoadBalancer[numOfTiers];
		
		for (int i = 0; i < numOfTiers; i++) {
			loadBalancers[i] = buildLoadBalancer(scheduler, monitor, heuristicClasses[i], maxServerPerTier[i], i);
		}
		
		return loadBalancers;
	}

	/**
	 * @param scheduler
	 * @param monitor
	 * @param heuristic
	 * @param maxServerPerTier 
	 * @param tier 
	 * @return
	 */
	private static LoadBalancer buildLoadBalancer(JEEventScheduler scheduler, Monitor monitor,
			Class<?> heuristic, int maxServerPerTier, int tier) {
		try {
			return new LoadBalancer(scheduler, monitor, 
					(SchedulingHeuristic) heuristic.newInstance(), 
					maxServerPerTier, tier);
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ heuristic, e);
		}
	}

}
