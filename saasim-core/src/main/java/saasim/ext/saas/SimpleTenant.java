package saasim.ext.saas;

import saasim.core.application.Application;
import saasim.core.saas.Tenant;
import saasim.core.sim.TrafficGenerator;
import saasim.core.sim.TrafficGeneratorFactory;

import com.google.inject.Inject;

public class SimpleTenant implements Tenant {
	
	private static int IDGEN = 0;
	
	private final int id;
	private final TrafficGenerator trafficGenerator;

	@Inject
	public SimpleTenant(TrafficGeneratorFactory factory) {
		this.id = IDGEN++;
		this.trafficGenerator = factory.create(this.id);
	}

	@Override
	public void setUp() {
		trafficGenerator.start();
	}

	@Override
	public int getID() {
		return id;
	}
	
	@Override
	public Application getApplication() {
		return trafficGenerator.getApplication();
	}
}
