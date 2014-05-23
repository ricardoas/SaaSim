package saasim.core.sim;
import org.apache.commons.configuration.ConfigurationException;

import saasim.core.config.Configuration;
import saasim.core.event.EventScheduler;
import saasim.core.iaas.Customer;
import saasim.core.iaas.MonitoringService;
import saasim.core.iaas.Provider;
import saasim.core.infrastructure.AdmissionControl;
import saasim.core.infrastructure.LoadBalancer;
import saasim.core.infrastructure.Machine;
import saasim.core.infrastructure.MachineFactory;
import saasim.core.io.TraceReader;
import saasim.core.io.TrafficGenerator;
import saasim.core.provisioning.ProvisioningSystem;
import saasim.core.saas.ASP;
import saasim.core.saas.Application;
import saasim.core.saas.Tenant;
import saasim.core.saas.Tier;
import saasim.ext.iaas.LoggerIaaSCustomer;
import saasim.ext.saas.MultiTenantASP;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;


/**
 * {@link Guice} module, binding interfaces to implementations as configured at properties file. 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SaaSimModule extends AbstractModule {

	public static final String TRACE_PARSER_CLASS = "trace.reader.class";
	public static final String TRAFFIC_GENERATOR_CLASS = "traffic.generator.class";
	
	public static final String TENANT_CLASS = "saas.tenant.class";
	public static final String APPLICATION_CLASS = "application.class";
	public static final String TIER_CLASS = "application.tier.class";
	public static final String ADMISSION_CONTROL_CLASS = "application.admissioncontrol.class";
	public static final String LOADBALANCER_CLASS = "application.loadbalancer.class";
	
	public static final String MACHINE_CLASS = "machine.class";
	public static final String MONITORING_SERVICE_CLASS = "monitoring.service.class";
	public static final String MONITORING_MONITOR_CLASS = "monitoring.monitor.class";
	public static final String DPS_CLASS = "dps.class";
	public static final String IAAS_CLASS = "iaas.class";
	private final Configuration configuration;

	/**
	 * Default constructor.
	 * 
	 * @param configFilePath path to configuration file.
	 * @throws ConfigurationException 
	 */
	public SaaSimModule(String configFilePath) throws ConfigurationException {
		configuration = new Configuration(configFilePath);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void configure() {
		
		// SIMULATION
		bind(EventScheduler.class);
		
		bind(SaaSim.class);
		
		// WORKLOAD
		bind(TraceReader.class).to((Class<? extends TraceReader>) load(configuration.getString(TRACE_PARSER_CLASS)));
		
		bind(TrafficGenerator.class).to((Class<? extends TrafficGenerator>) load(configuration.getString(TRAFFIC_GENERATOR_CLASS)));

		// APPLICATION
		bind(ASP.class).to(MultiTenantASP.class);
		
		bind(Tenant.class).to((Class<? extends Tenant>) load(configuration.getString(TENANT_CLASS)));
		
		bind(Application.class).to((Class<? extends Application>) load(configuration.getString(APPLICATION_CLASS)));
		
		if(configuration.containsKey(TIER_CLASS)){
			bind(Tier.class).to((Class<? extends Tier>) load(configuration.getString(TIER_CLASS)));
		}

		if(configuration.containsKey(ADMISSION_CONTROL_CLASS)){
			bind(AdmissionControl.class).to((Class<? extends AdmissionControl>) load(configuration.getString(ADMISSION_CONTROL_CLASS)));
		}

		if(configuration.containsKey(LOADBALANCER_CLASS)){
			bind(LoadBalancer.class).to((Class<? extends LoadBalancer>) load(configuration.getString(LOADBALANCER_CLASS)));
		}
		
		
		
		
		bind(Provider.class).to((Class<? extends Provider>) load(configuration.getString(IAAS_CLASS))).in(Singleton.class);
		
		bind(ProvisioningSystem.class).to((Class<? extends ProvisioningSystem>) load(configuration.getString(DPS_CLASS))).in(Singleton.class);
		
		bind(MonitoringService.class).to((Class<? extends MonitoringService>) load(configuration.getString(MONITORING_SERVICE_CLASS))).in(Singleton.class);
		
		bind(Customer.class).to(LoggerIaaSCustomer.class).in(Singleton.class);;
		
		install(new FactoryModuleBuilder()
	     .implement(Machine.class, (Class<? extends Machine>) load(configuration.getString(MACHINE_CLASS)))
	     .build(MachineFactory.class));
	}

	/**
	 * Load {@link Class} from name.
	 * @param name {@link Class} name.
	 * @return a {@link Class} instance.
	 */
	private Class<?> load(String name){
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	
	/**
	 * @return A {@link Configuration} instance.
	 */
	@Provides
	Configuration provideConfiguration(){
		return configuration;
	}
}
