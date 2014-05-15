package saasim.ext.saas;

import saasim.core.application.Application;
import saasim.core.config.Configuration;
import saasim.core.event.EventScheduler;
import saasim.core.saas.Tenant;
import saasim.core.sim.WorkloadTrafficGenerator;
import saasim.core.sim.WorkloadTrafficGeneratorFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class SimpleTenant implements Tenant {
	
	private static int IDGEN = 0;
	
	private WorkloadTrafficGenerator trafficGenerator;

	private final int id;

	@Inject
	public SimpleTenant(@Assisted Application application, Configuration globalConf, EventScheduler scheduler, WorkloadTrafficGeneratorFactory factory) {
		this.id = IDGEN++;
		this.trafficGenerator = factory.create(application, this.id);
	}

	@Override
	public void start() {
		trafficGenerator.start();
	}

	@Override
	public int getID() {
		return id;
	}
	
}
