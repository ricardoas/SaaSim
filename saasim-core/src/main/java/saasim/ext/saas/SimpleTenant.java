package saasim.ext.saas;

import java.io.FileNotFoundException;

import saasim.core.io.TrafficGenerator;
import saasim.core.saas.Tenant;

import com.google.inject.Inject;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class SimpleTenant extends AbstractTenant implements Tenant {
	
	/**
	 * @param trafficGenerator
	 * @throws FileNotFoundException
	 */
	@Inject
	public SimpleTenant(TrafficGenerator trafficGenerator) throws FileNotFoundException {
		super(trafficGenerator);
	}
}
