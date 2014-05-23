package saasim.ext.saas;

import java.io.FileNotFoundException;

import saasim.core.io.TrafficGenerator;
import saasim.core.saas.Application;
import saasim.core.saas.Tenant;

import com.google.inject.Inject;

public class SimpleTenant implements Tenant {
	
	private static int IDGEN = 0;
	
	private final int id;
	private final TrafficGenerator trafficGenerator;

	@Inject
	public SimpleTenant(TrafficGenerator trafficGenerator) throws FileNotFoundException {
		this.trafficGenerator = trafficGenerator;
		this.id = IDGEN++;
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
