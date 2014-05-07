package saasim.core.infrastructure;

import saasim.core.application.Request;

public interface AdmissionControl {
	
	void process(long timestamp, LoadBalancer loadBalancer);
	
	void updatePolicy();

	boolean queue(Request request);

}
