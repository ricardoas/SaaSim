package planning.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import planning.Executor;
import planning.Planner;
import config.ContractConfiguration;
import config.GEISTMonthlyWorkloadParser;
import config.MainConfiguration;
import config.ProviderConfiguration;

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
		
		MainConfiguration mainConfig = new MainConfiguration();
		try {
			mainConfig.loadPropertiesFromFile(args[0]);//Loading main simulation configuration
			
			//Parsing workload
//			Map<Integer, Map<User, List<Request>>> workload = GEISTMonthlyWorkloadParser.getWorkloadPerMonth(mainConfig.getWorkloadFile());
			GEISTMonthlyWorkloadParser workloadParser = new GEISTMonthlyWorkloadParser(mainConfig.getWorkloadFile());
			
			//Loading SaaS provider contracts
			ContractConfiguration contractConfig = new ContractConfiguration();
			contractConfig.loadPropertiesFromFile(mainConfig.getContractsFile());
			
			//Loading IaaS provider config
			ProviderConfiguration providerConfig = new ProviderConfiguration();
			providerConfig.loadPropertiesFromFile(mainConfig.getIAASFile());
			
			//Creating planner
			Planner planner = new Planner(providerConfig.providers, mainConfig.getHeuristic(), contractConfig.usersContracts, workloadParser, mainConfig.getSLA());
			List<String> plan = planner.plan();
			
			//FIXME: Change workload! Performing plan execution!
			Executor executor = new Executor(providerConfig.providers, contractConfig.usersContracts, workloadParser, mainConfig.getSLA());
			executor.execute(plan);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
