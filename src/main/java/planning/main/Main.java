package planning.main;

import static commons.sim.util.SaaSAppProperties.APPLICATION_SLA_MAX_RESPONSE_TIME;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import planning.Executor;
import planning.Planner;

import commons.config.Configuration;
import commons.io.GEISTWorkloadParser;
import commons.io.HistoryBasedWorkloadParser;
import commons.io.TimeBasedWorkloadParser;
import commons.sim.util.SaaSUsersProperties;

/**
 * This class is responsible for obtaining input parameters, from a configuration file, such as: workload, cloud provider
 * data (price, limits, etc.) and planning heuristics to be used!
 * @author davidcmm
 *
 */
public class Main {
	
	public static void main(String[] args) {
		if(args.length != 1){
			System.err.println("Configuration file is missing!");
			System.exit(1);
		}
		
		try {
			//Loading simulator configuration data
			Configuration.buildInstance(args[0]);
			Configuration config = Configuration.getInstance();
			
			//Parsing workload
			String[] workloads = Configuration.getInstance().getStringArray(SaaSUsersProperties.SAAS_USER_WORKLOAD);
			HistoryBasedWorkloadParser workloadParser = new HistoryBasedWorkloadParser(new GEISTWorkloadParser(workloads), TimeBasedWorkloadParser.YEAR_IN_MILLIS);
			
			//Creating planner
			Planner planner = new Planner(config.getProviders(), config.getUsers(), workloadParser);
			List<String> plan = planner.plan();
			
			//FIXME: Change workload! Performing plan execution!
			Executor executor = new Executor(config.getProviders(), config.getUsers(), workloadParser, config.getDouble(APPLICATION_SLA_MAX_RESPONSE_TIME));
			executor.execute(plan);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
