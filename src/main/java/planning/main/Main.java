package planning.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import planning.Executor;
import planning.Planner;

import commons.config.SimulatorConfiguration;

import config.GEISTMonthlyWorkloadParser;

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
			SimulatorConfiguration.buildInstance(args[0]);
			SimulatorConfiguration config = SimulatorConfiguration.getInstance();
			
			//Parsing workload
			GEISTMonthlyWorkloadParser workloadParser = new GEISTMonthlyWorkloadParser();
			
			//Creating planner
			Planner planner = new Planner(config.getProviders(), config.getPlanningHeuristic(), config.getContractsPerUser(), workloadParser);
			List<String> plan = planner.plan();
			
			//FIXME: Change workload! Performing plan execution!
			Executor executor = new Executor(config.getProviders(), config.getContractsPerUser(), workloadParser, config.getSLA());
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
