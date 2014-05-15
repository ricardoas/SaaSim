package saasim.core.sim;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import saasim.core.application.Application;
import saasim.core.application.ApplicationFactory;
import saasim.core.config.Configuration;
import saasim.core.event.Event;
import saasim.core.event.EventScheduler;
import saasim.core.iaas.Provider;
import saasim.core.infrastructure.MonitoringService;
import saasim.core.infrastructure.MonitoringServiceConsumer;
import saasim.core.provisioning.ProvisioningSystem;
import saasim.core.saas.Tenant;
import saasim.core.saas.TenantFactory;

import com.google.inject.Inject;

/**
 * SaaSim main class. It builds and glues all other components.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SaaSim{

	public static final String SAASIM_SIMULATION_TIME = "simulation.time";
	private EventScheduler scheduler;
	private ProvisioningSystem dps;
	private MonitoringService feed;
	private long simulationTime;
	private Tenant[] tenants;

	/**
	 * Default constructor.
	 * 
	 * @param globalConf {@link Configuration} instance.
	 * @param scheduler {@link Event} queue manager.
	 * @param iaasProvider {@link Provider} instance.
	 * @param dps provisioner instance.
	 * @param application {@link Application} being simulated.
	 * @param workloadGenerator traffic generator.
	 * 
	 * @throws ConfigurationException
	 */
	@Inject
	public SaaSim(Configuration globalConf, EventScheduler scheduler, ProvisioningSystem dps, ApplicationFactory factory, MonitoringService feed, MonitoringServiceConsumer consumer, TenantFactory tenantFactory) throws ConfigurationException {

		this.scheduler = scheduler;
		
		this.dps = dps;
		this.feed = feed;
		this.feed.subscribe(consumer);
		
		int numberOfTenants = globalConf.getInt(Tenant.SAAS_TENANT_NUMBER);
		this.tenants = new Tenant[numberOfTenants];

		switch(globalConf.getString(Tenant.SAAS_TENANT_MODE)){

		case Tenant.SAAS_TENANT_MODE_ARCHITECTURE: // one application and several tenants one provisioner
			Application singleApplication = factory.create();
			this.dps.registerConfigurable(singleApplication);
			while(numberOfTenants-- > 0){
				tenants[numberOfTenants] = tenantFactory.create(singleApplication);
			}
			break;
		case Tenant.SAAS_TENANT_MODE_VIRTUALIZED: // one application+provisioner per tenant
			while(numberOfTenants-- > 0){
				Application tenantApplication = factory.create();
				this.dps.registerConfigurable(tenantApplication);
				tenants[numberOfTenants] = tenantFactory.create(tenantApplication);
			}
			break;
		default:
			throw new ConfigurationException();
		}
		
		this.simulationTime = globalConf.getLong(SAASIM_SIMULATION_TIME);
	}

	/**
	 * Start simulation
	 */
	public void start() {
		
		long start = System.currentTimeMillis();
		Logger.getLogger(SaaSim.class).debug("SIMULATION START " + start);
		
		for (Tenant tenant : tenants) {
			tenant.start();
		}
		
		scheduler.start(simulationTime);

		Logger.getLogger(SaaSim.class).debug("SIMULATION END " + System.currentTimeMillis());
		Logger.getLogger(SaaSim.class).debug("SIMULATION DURATION " + (System.currentTimeMillis()-start));
	}
}
