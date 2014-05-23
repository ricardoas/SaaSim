package saasim.core.infrastructure;

import saasim.core.config.Configuration;
import saasim.core.saas.Request;

public interface AdmissionControl{
	
	public static final String ADMISSIONCONTROL_ACCEPTANCERATE = "application.admissioncontrol.acceptancerate";

	boolean canAccept(Request request);
	
	void updatePolicy(Configuration configuration);
}
