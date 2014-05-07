package saasim.core.io;

import saasim.core.application.Request;

public interface TraceParcer {
	
	/**
	 * Parse a line of workload trace to a {@link Request}.
	 * @param traceLine line to be parsed to a {@link Request}
	 * @param tenantID application tenant ID
	 * @return the {@link Request}
	 */
	Request parseRequest(String traceLine, int tenantID);

}
