import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.SystemConfiguration;

import saasim.core.application.ApplicationFactory;
import saasim.core.config.Configuration;
import saasim.ext.sim.SimpleSimulator;


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

		new SimpleSimulator(new Configuration()).start();
	}

}
