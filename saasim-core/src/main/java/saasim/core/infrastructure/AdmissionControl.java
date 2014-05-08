package saasim.core.infrastructure;

import saasim.core.application.Request;

public interface AdmissionControl {
	
	void updatePolicy();

	void queue(Request request);

	LoadBalancer getLoadBalancer();

}
