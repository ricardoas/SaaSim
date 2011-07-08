package commons.sim;

import commons.cloud.Machine;
import commons.cloud.Request;

public interface SchedulingHeuristic {

	Machine getNextServer(Request request);

}
