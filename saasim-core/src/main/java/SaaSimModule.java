import org.apache.commons.configuration.ConfigurationException;

import saasim.core.cloud.IaaSProvider;
import saasim.core.config.Configuration;
import saasim.core.event.EventScheduler;
import saasim.core.provisioning.DPS;
import saasim.core.sim.SaaSim;
import saasim.core.sim.Simulator;
import saasim.ext.cloud.AmazonEC2;
import saasim.ext.provisioning.StaticProvisioningSystem;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;


public class SaaSimModule extends AbstractModule {

	private String configFilePath;

	public SaaSimModule(String configFilePath) {
		this.configFilePath = configFilePath;
	}

	@Override
	protected void configure() {
		
		bind(EventScheduler.class).in(Singleton.class);
		
		bind(Simulator.class).to(SaaSim.class);
		
		extracted();
		
		bind(DPS.class).to(StaticProvisioningSystem.class).in(Singleton.class);
	}

	protected void extracted() {
		
		bind(IaaSProvider.class).to((Class<? extends IaaSProvider>) loadFactory(provideConfiguration().getString("iaas.factory"))).in(Singleton.class);
	}
	
	private Class<?> loadFactory(String name){
		
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	
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
