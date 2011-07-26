package commons.sim.schedulingheuristics;

import java.util.List;

import commons.cloud.Request;
import commons.sim.components.Machine;

public interface SchedulingHeuristic {

	Machine getNextServer(Request request, List<Machine> servers);

	double evaluateUtilization(List<Machine> servers, Long eventTime);
}
