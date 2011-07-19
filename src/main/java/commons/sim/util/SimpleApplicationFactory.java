package commons.sim.util;

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
			Monitor monitor) {
		SimulatorConfiguration config = SimulatorConfiguration.getInstance();
		int numOfTiers = config.getApplicationNumOfTiers();
		String[] heuristicClassName = config.getApplicationHeuristics();
		LoadBalancer entryPoint = buildLoadBalancer(scheduler, monitor, heuristicClassName[0]);
		LoadBalancer currentTier = entryPoint;
		for (int i = 1; i < numOfTiers; i++) {
			LoadBalancer nextTier = buildLoadBalancer(scheduler, monitor, heuristicClassName[i]);
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
	 * @param heuristicClassName
	 * @return
	 */
	private LoadBalancer buildLoadBalancer(JEEventScheduler scheduler, Monitor monitor,
			String heuristicClassName) {
		try {
			return new LoadBalancer(scheduler, monitor, (SchedulingHeuristic) Class.forName(heuristicClassName).newInstance());
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ heuristicClassName, e);
		}
	}

}
