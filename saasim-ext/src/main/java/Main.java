import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;

import saasim.core.config.Configuration;
import saasim.core.sim.SaaSim;


public class Main {
	
	/**
	 * Entry point.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws ConfigurationException {

		if (args.length != 0) {
			System.out.println("Usage: java -cp <path-to-jar-files> Main config.properties");
			System.exit(1);
		}

		new SaaSim(new Configuration()).start();
	}

}
