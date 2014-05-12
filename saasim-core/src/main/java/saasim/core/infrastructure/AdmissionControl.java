package saasim.core.infrastructure;

import saasim.core.application.Request;

public interface AdmissionControl{
	
	public static final String ADMISSIONCONTROL_ACCEPTANCERATE = "admissioncontrol.acceptancerate";

	boolean canAccept(Request request);
	
	void updatePolicy();
}
