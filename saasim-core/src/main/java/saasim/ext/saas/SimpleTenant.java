package saasim.ext.saas;

import saasim.core.application.Application;
import saasim.core.saas.Tenant;
import saasim.core.sim.WorkloadTrafficGenerator;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class SimpleTenant implements Tenant {
	
	private WorkloadTrafficGenerator trafficGenerator;

	@Inject
	public SimpleTenant(WorkloadTrafficGenerator trafficGenerator, @Assisted Application application) {
		this.trafficGenerator = trafficGenerator;
		this.trafficGenerator.setApplication(application);
	}

	@Override
	public void start() {
		trafficGenerator.start();
	}
	
}
