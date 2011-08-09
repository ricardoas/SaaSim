package commons.sim.schedulingheuristics;

import java.util.List;

import commons.cloud.Request;
import commons.sim.components.Machine;

/**
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface SchedulingHeuristic {

	/**
	 * Determine to which server should this request be redirected to.
	 * @param request The next {@link Request} to be scheduled.
	 * @param servers The collection of server from where the next server should be chosen.
	 * @return
	 */
	Machine getNextServer(Request request, List<Machine> servers);
}
