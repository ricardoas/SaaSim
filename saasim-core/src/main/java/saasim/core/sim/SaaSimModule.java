package saasim.core.sim;
import org.apache.commons.configuration.ConfigurationException;

import saasim.core.application.Application;
import saasim.core.application.Request;
import saasim.core.application.ScalableTier;
import saasim.core.application.Tier;
import saasim.core.cloud.IaaSCustomer;
import saasim.core.cloud.IaaSProvider;
import saasim.core.config.Configuration;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.AdmissionControl;
import saasim.core.infrastructure.LoadBalancer;
import saasim.core.infrastructure.Machine;
import saasim.core.infrastructure.MachineFactory;
import saasim.core.infrastructure.Monitor;
import saasim.core.infrastructure.MonitoringService;
import saasim.core.infrastructure.MonitoringServiceConsumer;
import saasim.core.io.TraceParcer;
import saasim.core.io.TraceReader;
import saasim.core.io.TraceReaderFactory;
import saasim.core.provisioning.ProvisioningSystem;
import saasim.ext.cloud.LoggerIaaSCustomer;
import saasim.ext.infrastructure.DefaultOutputWriter;
import saasim.ext.infrastructure.FCFSAdmissionControl;
import saasim.ext.infrastructure.RoundRobinLoadBalancer;
import saasim.ext.io.LineBasedTraceReader;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;


/**
 * {@link Guice} module, binding interfaces to implementations as configured at properties file. 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SaaSimModule extends AbstractModule {

	public static final String MACHINE_CLASS = "machine.class";
	public static final String MONITORING_SERVICE_CLASS = "monitoring.service.class";
	public static final String TRACE_PARSER_CLASS = "trace.parser.class";
	public static final String MONITORING_MONITOR_CLASS = "monitoring.monitor.class";
	public static final String APPLICATION_CLASS = "application.class";
	public static final String DPS_CLASS = "dps.class";
	public static final String IAAS_CLASS = "iaas.class";
	private final String configFilePath;

	/**
	 * Default constructor.
	 * 
	 * @param configFilePath path to configuration file.
	 */
	public SaaSimModule(String configFilePath) {
		this.configFilePath = configFilePath;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void configure() {
		
		bind(EventScheduler.class).in(Singleton.class);
		
		bind(SaaSim.class);
		
		bind(IaaSProvider.class).to((Class<? extends IaaSProvider>) load(provideConfiguration().getString(IAAS_CLASS))).in(Singleton.class);
		
		bind(ProvisioningSystem.class).to((Class<? extends ProvisioningSystem>) load(provideConfiguration().getString(DPS_CLASS))).in(Singleton.class);
		
		bind(Application.class).to((Class<? extends Application>) load(provideConfiguration().getString(APPLICATION_CLASS))).in(Singleton.class);
		
		bind(Monitor.class).to((Class<? extends Monitor>) load(provideConfiguration().getString(MONITORING_MONITOR_CLASS))).in(Singleton.class);
		
		bind(MonitoringService.class).to((Class<? extends MonitoringService>) load(provideConfiguration().getString(MONITORING_SERVICE_CLASS))).in(Singleton.class);
				
		bind(TraceParcer.class).to((Class<? extends TraceParcer>) load(provideConfiguration().getString(TRACE_PARSER_CLASS))).in(Singleton.class);
		
		install(new FactoryModuleBuilder()
	     .implement(new TypeLiteral<TraceReader<Request>>() {}, LineBasedTraceReader.class)
	     .build(new TypeLiteral<TraceReaderFactory<Request>>() {}));
		
		bind(Tier.class).to(ScalableTier.class);
		
		bind(AdmissionControl.class).to(FCFSAdmissionControl.class);
		
		bind(LoadBalancer.class).to(RoundRobinLoadBalancer.class);
		
		bind(IaaSCustomer.class).to(LoggerIaaSCustomer.class).in(Singleton.class);;
		
		install(new FactoryModuleBuilder()
	     .implement(Machine.class, (Class<? extends Machine>) load(provideConfiguration().getString(MACHINE_CLASS)))
	     .build(MachineFactory.class));
		
		bind(MonitoringServiceConsumer.class).to(DefaultOutputWriter.class).in(Singleton.class);
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
	@Singleton
	Configuration provideConfiguration(){
		try {
			return new Configuration(configFilePath);
		} catch (ConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
}
