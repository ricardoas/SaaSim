package commons.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

import commons.config.ComplexPropertiesConfiguration;


/**
 * Simplification of {@link UserPropertiesGenerator}
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class SimpleUserPropertiesGenerator {
	
	
	private static final String PROP_SIMULATION_DURATION = "simulation.duration";
	private static final String PROP_TRANSITION_DURATION = "transition.duration";
	private static final String PROP_PEAK_DURATION = "peak.duration";

	private static final String PROP_CLIENT_NUMBER = "client.number";
	private static final String PROP_CLIENT_TYPE = "client.type";
	private static final String PROP_CLIENT_STORAGE = "client.storage";
	
	

	
	private static final String PEAK_DAY_PATTERN = "peak_";
	private static final String UNDERLOADED_DAY_PATTERN = "under_";
	private static final String TYPYCAL_DAY_PATTERN = "typ_";

	private static final int TRANSITION_PERIOD_IN_DAYS = 15;

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
			if(string.equals("cilents")){
				generateClients = true;
			}else if(string.equals("scenarios")){
				generateScenarios = true;
			}else{
				propertiesFile = string;
			}
		}
		
		PropertiesConfiguration config = new PropertiesConfiguration(propertiesFile);
		
		if(generateClients){
			generateClients(config);
		}
		
		if(generateScenarios){
			generateScenarios(config);
		}
		
		
		
		
		//Creating output file
		createOutputFile(outputFile, workloadFilesPerUser);
	}

	private static void generateClients(PropertiesConfiguration config) {
		
		String[] clients = config.getStringArray(PROP_CLIENT_NUMBER);
		
		for (int i = 0; i < clients.length; i++) {
			int numberOfClients = Integer.valueOf(clients[0]);
			
			long simulationDuration = config.getLong(PROP_SIMULATION_DURATION);
			long transitionDuration = config.getLong(PROP_SIMULATION_DURATION);
			long peakDuration = config.getLong(PROP_SIMULATION_DURATION);
			
			
			String outputFile = args[2];
			
			String diamondPool = args[3];
			String goldPool = args[4];
			String bronzePool = args[5];
			
			long[] peakDays = new long[]{-360};
			if(args.length > 6){
				peakDays = new long[args.length - 6];
			}
			int index = 0;
			
			for(int i = 6; i < args.length; i++){
				peakDays[index] = Long.parseLong(args[i]);
				index++;
			}
			
			Map<Integer, List<String>> workloadFilesPerUser = new HashMap<Integer, List<String>>();

			createPlanWorkload(simulationPeriod, diamondPool, peakDays, random,
					0, numberOfClients, workloadFilesPerUser);
		}
		
		
		//Calculating number of users in each plan
		RandomData random = new RandomDataImpl();
		int numberOfBronzeClients = 0;
		int numberOfGoldClients = 0;
		int numberOfDiamondClients = 0;
		
		for(int i = 0; i < numberOfClients; i++){
			int value = random.nextInt(0, numberOfClients);
			if(value >= 0 && value < 0.33 * numberOfClients){//bronze client
				numberOfBronzeClients++;
			}else if(value >= 0.33 * numberOfClients && value < 0.66 * numberOfClients){//gold clients
				numberOfGoldClients++;
			}else{
				numberOfDiamondClients++;
			}
		}
		
		Map<Integer, List<String>> workloadFilesPerUser = new HashMap<Integer, List<String>>();
		
		//Creating diamond trace
		createPlanWorkload(simulationPeriod, diamondPool, peakDays, random,
				0, numberOfDiamondClients,workloadFilesPerUser);
		
		//Creating gold trace
		createPlanWorkload(simulationPeriod, goldPool, peakDays, random,
				numberOfDiamondClients, (numberOfDiamondClients+numberOfGoldClients), workloadFilesPerUser);
		
		//Creating bronze trace
		createPlanWorkload(simulationPeriod, bronzePool, peakDays, random,
				(numberOfDiamondClients+numberOfGoldClients), (numberOfDiamondClients+numberOfGoldClients+numberOfBronzeClients),
				workloadFilesPerUser);
	}

	private static void generateScenarios(PropertiesConfiguration config) {
		// TODO Auto-generated method stub
		
	}

	private static void createPlanWorkload(long simulationPeriod,
			String pool, long[] peakDays, RandomData random,
			int initialIndex, int endIndex,
			Map<Integer, List<String>> workloadFilesPerUser) {
		
		String[] normalTyp = new File(pool+"/norm/").list(new TraceFilter(TYPYCAL_DAY_PATTERN));
		String[] normalUnder = new File(pool+"/norm/").list(new TraceFilter(UNDERLOADED_DAY_PATTERN));
		String[] normalPeak = new File(pool+"/norm/").list(new TraceFilter(PEAK_DAY_PATTERN));
		
		String[] peakTyp = new File(pool+"/peak/").list(new TraceFilter(TYPYCAL_DAY_PATTERN));
		String[] peakUnder = new File(pool+"/peak/").list(new TraceFilter(UNDERLOADED_DAY_PATTERN));
		String[] peakPeak = new File(pool+"/peak/").list(new TraceFilter(PEAK_DAY_PATTERN));
		
		String[] transTyp = new File(pool+"/trans/").list(new TraceFilter(TYPYCAL_DAY_PATTERN));
		String[] transUnder = new File(pool+"/trans/").list(new TraceFilter(UNDERLOADED_DAY_PATTERN));
		String[] transPeak = new File(pool+"/trans/").list(new TraceFilter(PEAK_DAY_PATTERN));
		
		for(int user = initialIndex; user < endIndex; user++){
			List<String> workloads = new ArrayList<String>();
			int currentWeekDay = 0;
			int nextPeakIndex = 0;
			
			for(int day = 0; day < simulationPeriod; day++){
				
				if(currentWeekDay == 7){
					currentWeekDay = 0;
				}
				
				if(day >= peakDays[nextPeakIndex] - TRANSITION_PERIOD_IN_DAYS && day < peakDays[nextPeakIndex]){//transition workload
					verifyDayToAdd(pool+"/trans/", random, transTyp, transUnder,
							transPeak, workloads, currentWeekDay);
				}else if(day >= peakDays[nextPeakIndex] && day < peakDays[nextPeakIndex] + TRANSITION_PERIOD_IN_DAYS){//peak workload
					verifyDayToAdd(pool+"/peak/", random, peakTyp, peakUnder,
							peakPeak, workloads, currentWeekDay);
					if(day + 1 == peakDays[nextPeakIndex] + TRANSITION_PERIOD_IN_DAYS && (nextPeakIndex+1) < peakDays.length){
						nextPeakIndex++;
					}
				}else{//normal workload
					verifyDayToAdd(pool+"/norm/", random, normalTyp, normalUnder,
							normalPeak, workloads, currentWeekDay);
				}
				currentWeekDay++;
			}
			workloadFilesPerUser.put(user, workloads);
		}
	}

	private static void verifyDayToAdd(String pool, RandomData random,
			String[] typPeriod, String[] underPeriod, String[] peakPeriod,
			List<String> workloads, int currentWeekDay) {
	
		
		if(currentWeekDay == 5 || currentWeekDay == 6){//under load day in week
			int workloadFileIndex = random.nextInt(0, underPeriod.length-1);
			workloads.add(pool+underPeriod[workloadFileIndex]);
		}else if(currentWeekDay == 3){//peak day in week
			int workloadFileIndex = random.nextInt(0, peakPeriod.length-1);
			workloads.add(pool+peakPeriod[workloadFileIndex]);
		}else{//normal day
			int workloadFileIndex = random.nextInt(0, typPeriod.length-1);
			workloads.add(pool+typPeriod[workloadFileIndex]);
		}
	}
	
	private static void createOutputFile(String outputFile, Map<Integer, List<String>> workloadFilesPerUser) throws IOException {
		BufferedWriter usersPropertiesWriter = new BufferedWriter(new FileWriter(outputFile));
		usersPropertiesWriter.write("saas.number="+workloadFilesPerUser.size()+"\n\n");
		
		for(Entry<Integer, List<String>> entry : workloadFilesPerUser.entrySet()){
			
			boolean isGold = entry.getValue().get(0).contains("gold");
			boolean isDiamond = entry.getValue().get(0).contains("diamond");
			
			String plan;
			long storage;
			if(isGold){
				plan = "gold";
				storage = GOLD_STORAGE_IN_BYTES;
			}else if(isDiamond){
				plan = "diamond";
				storage = DIAMOND_STORAGE_IN_BYTES;
			}else{
				plan = "bronze";
				storage = BRONZE_STORAGE_IN_BYTES;
			}
			
			usersPropertiesWriter.write("saas.user.id="+entry.getKey()+"\n");
			usersPropertiesWriter.write("saas.user.plan="+plan+"\n");
			usersPropertiesWriter.write("saas.user.storage="+storage+"\n");
			usersPropertiesWriter.write("saas.user.workload="+entry.getKey()+".trc\n");
			usersPropertiesWriter.write("\n");
			
			writeWorkloadFile(entry.getKey(), entry.getValue());
		}
		
		usersPropertiesWriter.close();
	}

	private static void writeWorkloadFile(Integer key, List<String> pointers) throws IOException {
		BufferedWriter workloadWriter = new BufferedWriter(new FileWriter(key+".trc"));
		for(String pointer : pointers){
			workloadWriter.write(pointer+"\n");
		}
		workloadWriter.close();
	}

}
