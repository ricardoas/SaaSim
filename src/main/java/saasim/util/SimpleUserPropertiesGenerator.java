package saasim.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import saasim.config.ComplexPropertiesConfiguration;



/**
 * Simplification of {@link UserPropertiesGenerator}
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class SimpleUserPropertiesGenerator {
	
	
	private static final String PROP_SIMULATION_DURATION = "simulation.duration";
	private static final String PROP_TRANSITION_DURATION = "transition.duration";
	private static final String PROP_HOLIDAYS = "holidays";

	private static final String PROP_CLIENT_NUMBER = "client.number";
	private static final String PROP_CLIENT_TYPE = "client.type";
	private static final String PROP_CLIENT_STORAGE = "client.storage";
	
	private static final String PROP_SCENARIO = "scenario";
	
	private static final String PROP_NORM_DIR = "norm.dir";
	private static final String PROP_TRANS_DIR = "trans.dir";
	private static final String PROP_PEAK_DIR = "peak.dir";
	
	private static final String PEAK_DAY_PATTERN = "peak_";
	private static final String UNDERLOADED_DAY_PATTERN = "under_";
	private static final String TYPYCAL_DAY_PATTERN = "typ_";

	public static final long DIAMOND_STORAGE_IN_BYTES = 700;//700 MB
	public static final long GOLD_STORAGE_IN_BYTES = 500;//500 MB
	public static final long BRONZE_STORAGE_IN_BYTES = 200;//200 MB
	
	public static void main(String[] args) throws IOException, ConfigurationException {
		
		if(args.length < 1){
			System.err.println("usage: SimpleUserPropertiesGenerator <clients> <scenarios> <properties file>");
			System.exit(1);
		}
		
		boolean generateClients = false;
		boolean generateScenarios = false;
		String propertiesFile = null;
		
		for (String string : args) {
			if(string.equals("clients")){
				generateClients = true;
			}else if(string.equals("scenarios")){
				generateScenarios = true;
			}else{
				propertiesFile = string;
			}
		}
		
		ComplexPropertiesConfiguration config = new ComplexPropertiesConfiguration(propertiesFile){};
		
		if(generateClients){
			generateClients(config);
		}
		
		if(generateScenarios){
			generateScenarios(config);
		}
		
		
		
		
		//Creating output file
//		createOutputFile(outputFile, workloadFilesPerUser);
	}

	private static void generateClients(ComplexPropertiesConfiguration config) throws IOException {
		
		long simulationDuration = config.getLong(PROP_SIMULATION_DURATION);
		long transitionDuration = config.getLong(PROP_TRANSITION_DURATION);
		int [] holidays = config.getIntegerArray(PROP_HOLIDAYS);

		int[] clients = config.getIntegerArray(PROP_CLIENT_NUMBER);
		String[] types = config.getStringArray(PROP_CLIENT_TYPE);
		String[] normDirs = config.getStringArray(PROP_NORM_DIR);
		String[] transDirs = config.getStringArray(PROP_TRANS_DIR);
		String[] peakDirs = config.getStringArray(PROP_PEAK_DIR);
		
		for (int i = 0; i < clients.length; i++) {
			String normPool = normDirs[i];
			String transPool = transDirs[i];
			String peakPool = peakDirs[i];
			
			for (int clientID = 0; clientID < clients[i]; clientID++) {
				
				File[] normalTyp = new File(normPool).listFiles(new TraceFilter(TYPYCAL_DAY_PATTERN));
				File[] normalUnder = new File(normPool).listFiles(new TraceFilter(UNDERLOADED_DAY_PATTERN));
				File[] normalPeak = new File(normPool).listFiles(new TraceFilter(PEAK_DAY_PATTERN));
				
				File[] peakTyp = new File(peakPool).listFiles(new TraceFilter(TYPYCAL_DAY_PATTERN));
				File[] peakUnder = new File(peakPool).listFiles(new TraceFilter(UNDERLOADED_DAY_PATTERN));
				File[] peakPeak = new File(peakPool).listFiles(new TraceFilter(PEAK_DAY_PATTERN));
				
				File[] transTyp = new File(transPool).listFiles(new TraceFilter(TYPYCAL_DAY_PATTERN));
				File[] transUnder = new File(transPool).listFiles(new TraceFilter(UNDERLOADED_DAY_PATTERN));
				File[] transPeak = new File(transPool).listFiles(new TraceFilter(PEAK_DAY_PATTERN));
				
				List<String> workloads = new ArrayList<String>();
				
				Calendar today = GregorianCalendar.getInstance();
				today.set(Calendar.DAY_OF_YEAR, 0);
				
				for (int holiday : holidays) {
					for (int day = today.get(Calendar.DAY_OF_YEAR); day < holiday-transitionDuration; day++) {

						chooseTrace(normalTyp, normalUnder, normalPeak, workloads, today);

						today.add(Calendar.DAY_OF_YEAR, 1);
					}
					for (int day = today.get(Calendar.DAY_OF_YEAR); day < holiday; day++) {
						chooseTrace(transTyp, transUnder, transPeak, workloads, today);

						today.add(Calendar.DAY_OF_YEAR, 1);
					}
					for (int day = today.get(Calendar.DAY_OF_YEAR); day < holiday+7; day++) {
						chooseTrace(peakTyp, peakUnder, peakPeak, workloads, today);

						today.add(Calendar.DAY_OF_YEAR, 1);
					}
				}

				for (int day = holidays[holidays.length-1]; day < simulationDuration; day++) {
					chooseTrace(normalTyp, normalUnder, normalPeak, workloads, today);

					today.add(Calendar.DAY_OF_YEAR, 1);
				}
				
				writeWorkloadFile(types[i] + "_" + clientID + ".trc", workloads);
			}
		}
	}

	private static void chooseTrace(File[] typicalDays, File[] underloadedDays,
			File[] peakDays, List<String> workloads, Calendar today) {
		switch (today.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.TUESDAY: // PEAK
			workloads.add(randomPick(peakDays));
			break;
		case Calendar.THURSDAY:
		case Calendar.FRIDAY: // UNDER
			workloads.add(randomPick(underloadedDays));
			break;
		default: // NORMAL
			workloads.add(randomPick(typicalDays));
			break;
		}
	}

	private static String randomPick(File[] array) {
		return array[(int)Math.floor(Math.random() * array.length)].getAbsolutePath();
	}

	private static void writeWorkloadFile(String fileName, List<String> pointers) throws IOException {
		BufferedWriter workloadWriter = new BufferedWriter(new FileWriter(fileName));
		for(String pointer : pointers){
			workloadWriter.write(pointer+"\n");
		}
		workloadWriter.close();
	}

	private static void generateScenarios(ComplexPropertiesConfiguration config) throws IOException {
		
		int[] scenarios = config.getIntegerArray(PROP_SCENARIO);
		
		String[] types = config.getStringArray(PROP_CLIENT_TYPE);
		String[] storage = config.getStringArray(PROP_CLIENT_STORAGE);
		
		File[][] workloads = new File[types.length][];
		for (int i = 0; i < types.length; i++) {
			workloads[i] = new File("./").getAbsoluteFile().listFiles(new TraceFilter(types[i]));
		}
		
		for (int scenarioID = 0; scenarioID < scenarios.length/types.length; scenarioID++) {
			
			int totalOfUsersInThisScenario = 0;
			for (int j = scenarioID*types.length; j < (scenarioID+1)*types.length; j++) {
				totalOfUsersInThisScenario += scenarios[j];
			}
			
			StringBuilder sb = new StringBuilder("saas.number=");
			sb.append(totalOfUsersInThisScenario);
			sb.append('\n');
			sb.append('\n');
			
			for (int scenarioIndex = scenarioID*types.length; scenarioIndex < (scenarioID+1)*types.length; scenarioIndex++) {

				List<File> list = Arrays.asList(workloads[scenarioIndex%types.length]);
				Collections.shuffle(list);
				Iterator<File> workloadIterator = list.iterator();
				
				for (int numberOfClients = 0; numberOfClients < scenarios[scenarioIndex]; numberOfClients++) {
					sb.append("saas.user.plan=");
					sb.append(types[scenarioIndex%types.length]);
					sb.append('\n');
					sb.append("saas.user.storage=");
					sb.append(storage[scenarioIndex%types.length]);
					sb.append('\n');
					sb.append("saas.user.workload=");
					sb.append(workloadIterator.next().getAbsolutePath());
					sb.append('\n');
				}
			}
			
			FileWriter fileWriter = new FileWriter("user_" + scenarioID + ".properties");
			fileWriter.write(sb.toString());
			fileWriter.close();
		}
	}
}
