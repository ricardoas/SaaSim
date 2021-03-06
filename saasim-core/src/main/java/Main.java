import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import saasim.core.iaas.Customer;
import saasim.core.sim.SaaSim;
import saasim.core.sim.SaaSimModule;

import com.google.inject.Guice;

/**
 * Entry point.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Main {
	
	private static final String DEFAULT_CONFIG_FILEPATH = "saasim.properties";

	/**
	 * Entry point.
	 * 
	 * @param args path to configuration file.
	 * @throws ConfigurationException check your configuration file!
	 */
	public static void main(String[] args) throws ConfigurationException {
		
		if (args.length < 0 || args.length > 1) {
			System.out.println("Usage: java -cp <path-to-jar-files> Main [config.properties]");
			System.exit(1);
		}
		
		SaaSimModule module = new SaaSimModule(args.length == 1? args[0]: DEFAULT_CONFIG_FILEPATH);
		
		Guice.createInjector(module).getInstance(SaaSim.class).start();
		
		LogManager.shutdown();
	}
}
