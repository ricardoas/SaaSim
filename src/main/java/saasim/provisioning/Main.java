package saasim.provisioning;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import saasim.cloud.UtilityResult;
import saasim.config.Configuration;
import saasim.provisioning.util.DPSFactory;
import saasim.sim.Simulator;


/**
 * Provisioning simulator execution entry point.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Main {
	
	/**
	 * Entry point.
	 * 
	 * @param args Path to the configuration file.
	 * @throws ConfigurationException Related to problems during configuration loading.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws ConfigurationException, IOException {
		
		if(args.length != 1){
			System.out.println("Usage: java -cp saasim.jar saasim.provisioning.Main <property file>");
			System.exit(1);
		}
		
		Configuration.buildInstance(args[0]);
		
		Configuration configuration = Configuration.getInstance();
		
		DPS dps = DPSFactory.createDPS();
		
		Simulator simulator = configuration.getApplication();
		
		dps.registerConfigurable(simulator);
		
		simulator.start();
		
		if(configuration.getSimulationInfo().isFinishDay()){
			
			UtilityResult utilityResult = dps.calculateUtility();
			
			Logger.getLogger(Main.class).info(utilityResult);
			
			String events = configuration.getScheduler().dumpPostMortemEvents();
			if(!events.isEmpty()){
				FileWriter writer = new FileWriter(new File("events.dat"));
				writer.write(events);
				writer.close();
			}
		}else{//Persisting dump
			configuration.save();
		}
	}
}
