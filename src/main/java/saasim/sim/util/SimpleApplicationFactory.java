package saasim.sim.util;

import saasim.config.Configuration;
import saasim.provisioning.DPS;
import saasim.provisioning.Monitor;
import saasim.sim.DynamicConfigurable;
import saasim.sim.SimpleMultiTierApplication;
import saasim.sim.components.LoadBalancer;
import saasim.sim.core.EventScheduler;
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
	public LoadBalancer[] buildApplication(EventScheduler scheduler, Monitor monitor) {
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
	 * Build a {@link LoadBalancer}.
	 * @param scheduler {@link EventScheduler} represent a event scheduler
	 * @param monitor 
	 * @param heuristic a {@link SchedulingHeuristic} for this {@link LoadBalancer} 
	 * @param maxServerPerTier the maximum number of servers per tier 
	 * @param tier the tier of {@link LoadBalancer}
	 * @return A builded {@link LoadBalancer}.
	 */
	private static LoadBalancer buildLoadBalancer(EventScheduler scheduler, Monitor monitor, Class<?> heuristic,
			int maxServerPerTier, int tier) {
		try {
			return new LoadBalancer(scheduler, monitor, (SchedulingHeuristic) heuristic.newInstance(), 
					   maxServerPerTier, tier);
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ heuristic, e);
		}
	}

	@Override
	public DynamicConfigurable buildApplication(EventScheduler scheduler,
			DPS dps) {
		
		return new SimpleMultiTierApplication(scheduler, dps);
	}

}
