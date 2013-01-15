import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;

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
			System.out.println("Usage: java <-Dsaasim.properties=path_to_file> -cp <path-to-jar-files> Main");
			System.exit(1);
		}

		Configuration.buildInstance();

		new SimpleSimulator().start();
	}

}
