package saasim.ext.saas;

import saasim.core.io.TrafficGenerator;
import saasim.core.saas.Application;
import saasim.core.saas.Tenant;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class AbstractTenant implements Tenant{

	protected static int SEED = 0;
	
	protected final int id;
	protected final TrafficGenerator trafficGenerator;

	public AbstractTenant(TrafficGenerator trafficGenerator) {
		this.trafficGenerator = trafficGenerator;
		this.id = SEED ++;
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