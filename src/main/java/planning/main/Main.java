package planning.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;

import planning.Planner;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.config.Configuration;
import commons.io.HistoryBasedWorkloadParser;

/**
 * This class is responsible for obtaining input parameters, from a configuration file, such as: workload, cloud provider
 * data (price, limits, etc.) and planning heuristics to be used!
 * @author davidcmm
 *
 */
public class Main {
	
	private static final String OUTPUT_FILE = "output.plan";

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
			String[] workloads = Configuration.getInstance().getWorkloads();
			HistoryBasedWorkloadParser workloadParser = null;//new HistoryBasedWorkloadParser(new GEISTSingleFileWorkloadParser(workloads[0]), TimeBasedWorkloadParser.YEAR_IN_MILLIS);
			
			//Creating planner
			Planner planner = new Planner(config.getProviders(), config.getUsers(), workloadParser);
			Map<MachineType, Integer> plan = planner.plan();
			createPlanFile(plan, config.getProviders());
			
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
	
	private static void createPlanFile(Map<MachineType, Integer> plan, Provider[] providers) throws IOException {
		FileWriter writer = new FileWriter(new File(OUTPUT_FILE));
		
		String providerName = providers[0].getName();
		writer.write("iaas.plan.name="+providerName+"\n");
		StringBuilder machinesTypes = new StringBuilder();
		StringBuilder machinesAmount = new StringBuilder();
		
		Iterator<MachineType> iterator = plan.keySet().iterator();
		while(iterator.hasNext()){
			MachineType type = iterator.next();	
			machinesTypes.append(type.toString().toLowerCase());
			
			Integer amount = plan.get(type);
			machinesAmount.append(amount);
			
			if(iterator.hasNext()){
				machinesTypes.append("|");
				machinesAmount.append("|");
			}
		}
		writer.write("iaas.plan.types="+machinesTypes.toString()+"\n");
		writer.write("iaas.plan.reservation="+machinesAmount.toString());
		
		writer.close();
	}

}
