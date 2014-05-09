package saasim.core.infrastructure;

import saasim.core.application.Request;

public interface AdmissionControl{
	
	boolean canAccept(Request request);
	
	void updatePolicy();
}
