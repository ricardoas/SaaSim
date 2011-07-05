package commons.scheduling;

import java.util.List;
import java.util.Map;

import commons.cloud.Request;
import commons.cloud.User;

public class ProfitDrivenScheduler {

	private final Map<User, List<Request>> currentWorkload;

	public ProfitDrivenScheduler(Map<User, List<Request>> currentWorkload) {
		this.currentWorkload = currentWorkload;
	}

}
