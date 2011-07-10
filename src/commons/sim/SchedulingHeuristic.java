package commons.sim;

import java.util.List;

import commons.cloud.Machine;
import commons.cloud.Request;

public interface SchedulingHeuristic {

	Machine getNextServer(Request request, List<Machine> servers);

	int evaluateUtilization(List<Machine> servers, Long eventTime);
}
