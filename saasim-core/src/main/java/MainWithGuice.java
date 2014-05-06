import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;

import saasim.core.sim.Simulator;

import com.google.inject.Guice;

public class MainWithGuice {
	
	private static final String DEFAULT_CONFIG_FILEPATH = "saasim.properties";

	/**
	 * Entry point.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws ConfigurationException {

		if (args.length < 0 || args.length > 1) {
			System.out.println("Usage: java -cp <path-to-jar-files> Main [config.properties]");
			System.exit(1);
		}
		
		Guice.createInjector(new SaaSimModule(args.length == 1? args[0]: DEFAULT_CONFIG_FILEPATH)).getInstance(Simulator.class).start();
	}

}
