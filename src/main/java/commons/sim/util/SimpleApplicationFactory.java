package commons.sim.util;

import provisioning.Monitor;

import commons.config.SimulatorConfiguration;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.schedulingheuristics.RoundRobinHeuristic;

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
		LoadBalancer entryPoint = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic());
		LoadBalancer currentTier = entryPoint;
		for (int i = 0; i < numOfTiers -1; i++) {
			LoadBalancer nextTier = new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic());
			for (Machine machine : currentTier.getServers()) {
//				machine.setLoadBalancer(nextTier); TODO add this feature to allow multiple tiers
			}
			currentTier = nextTier;
		}
		return entryPoint;
	}

}
