package commons.sim.util;

import provisioning.Monitor;

import commons.sim.components.LoadBalancer;
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
		return new LoadBalancer(scheduler, monitor, new RoundRobinHeuristic());
	}

}
