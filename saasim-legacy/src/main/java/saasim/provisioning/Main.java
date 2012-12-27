package saasim.provisioning;

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;

import saasim.config.Configuration;
import saasim.planning.heuristic.OverProvisionHeuristic;
import saasim.sim.SimpleMultiTierApplication;
import saasim.sim.components.LoadBalancer;
import saasim.sim.components.TimeSharedMachine;

/**
 * Provisioning simulator execution entry point.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Main {

	/**
	 * Entry point.
	 * 
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	public static void main(String[] args) throws IOException,
			ConfigurationException {

		if (args.length != 0) {
			System.out
					.println("Usage: java <-Dsaasim.properties=path_to_file> -cp saasim.jar saasim.provisioning.Main");
			System.exit(1);
		}

		Configuration.buildInstance();

		Configuration.getInstance().getScheduler()
				.registerHandlerClass(LoadBalancer.class)
				.registerHandlerClass(SimpleMultiTierApplication.class)
				.registerHandlerClass(TimeSharedMachine.class)
				.registerHandlerClass(OverProvisionHeuristic.class);

		Configuration.getInstance().getSimulator().start();
	}
}
