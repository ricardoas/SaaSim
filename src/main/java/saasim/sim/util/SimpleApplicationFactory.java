package saasim.sim.util;

import saasim.config.Configuration;
import saasim.sim.components.LoadBalancer;
import saasim.sim.jeevent.JEEventScheduler;
import saasim.sim.schedulingheuristics.SchedulingHeuristic;

/**
 * This factory builds a simple application based on {@link ApplicationFactory}.  
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimpleApplicationFactory extends ApplicationFactory {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public LoadBalancer[] buildApplication(JEEventScheduler scheduler) {
		Configuration config = Configuration.getInstance();
		int numOfTiers = config.getInt(SaaSAppProperties.APPLICATION_NUM_OF_TIERS);
		
		Class<?>[] heuristicClasses = config.getApplicationHeuristics();
		int [] maxServerPerTier = config.getIntegerArray(SaaSAppProperties.APPLICATION_MAX_SERVER_PER_TIER);

		LoadBalancer [] loadBalancers = new LoadBalancer[numOfTiers];
		
		for (int i = 0; i < numOfTiers; i++) {
			loadBalancers[i] = buildLoadBalancer(scheduler, heuristicClasses[i], maxServerPerTier[i], i);
		}
		return loadBalancers;
	}

	/**
	 * Build a {@link LoadBalancer}.
	 * @param scheduler {@link JEEventScheduler} represent a event scheduler
	 * @param heuristic a {@link SchedulingHeuristic} for this {@link LoadBalancer} 
	 * @param maxServerPerTier the maximum number of servers per tier 
	 * @param tier the tier of {@link LoadBalancer}
	 * @return A builded {@link LoadBalancer}.
	 */
	private static LoadBalancer buildLoadBalancer(JEEventScheduler scheduler, Class<?> heuristic,
			int maxServerPerTier, int tier) {
		try {
			return new LoadBalancer(scheduler, (SchedulingHeuristic) heuristic.newInstance(), 
					   maxServerPerTier, tier);
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ heuristic, e);
		}
	}

}