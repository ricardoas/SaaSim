package saasim.core.sim;
import org.apache.commons.configuration.ConfigurationException;

import saasim.core.application.Application;
import saasim.core.application.Request;
import saasim.core.cloud.IaaSProvider;
import saasim.core.config.Configuration;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.Monitor;
import saasim.core.io.TraceParcer;
import saasim.core.io.TraceReader;
import saasim.core.io.TraceReaderFactory;
import saasim.core.provisioning.DPS;
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
		
		bind(IaaSProvider.class).to((Class<? extends IaaSProvider>) load(provideConfiguration().getString("iaas.class"))).in(Singleton.class);
		
		bind(DPS.class).to((Class<? extends DPS>) load(provideConfiguration().getString("dps.class"))).in(Singleton.class);
		
		bind(Application.class).to((Class<? extends Application>) load(provideConfiguration().getString("application.class"))).in(Singleton.class);
		
		bind(Monitor.class).to((Class<? extends Monitor>) load(provideConfiguration().getString("monitor.class"))).in(Singleton.class);
		
		bind(TraceParcer.class).to((Class<? extends TraceParcer>) load(provideConfiguration().getString("trace.parser.class"))).in(Singleton.class);
		
		install(new FactoryModuleBuilder()
	     .implement(new TypeLiteral<TraceReader<Request>>() {}, LineBasedTraceReader.class)
	     .build(new TypeLiteral<TraceReaderFactory<Request>>() {}));
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
