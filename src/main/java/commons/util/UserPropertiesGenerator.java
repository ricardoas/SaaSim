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

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

/**
 * This class is responsible for creating a users.properties file according to: number of saas clients, days to simulate and load files
 * @author David Candeia
 *
 */
public class UserPropertiesGenerator {
	
	private static final int TRANSITION_PERIOD_IN_DAYS = 15;
	public static final String DEFAULT_PLAN = "platinum";
	public static final long DEFAULT_STORAGE_IN_BYTES = 700000000;//700 MB
	
	public static void main(String[] args) throws IOException {
		
		if(args.length < 6){
			System.err.println("usage: <number of saas clients> <simulation period in days> <output file> <normal load pool>" +
					"<transition load pool> <peak load pool> <peak days>");
			System.exit(1);
		}
		
		long numberOfClients = Long.parseLong(args[0]);
		long simulationPeriod = Long.parseLong(args[1]);
		String outputFile = args[2];
		String normalPool = args[3];
		String transPool = args[4];
		String peakPool = args[5];
		
		long[] peakDays = new long[]{-360};
		if(args.length > 6){
			peakDays = new long[args.length - 6];
		}
		int index = 0;
		
		for(int i = 6; i < args.length; i++){
			peakDays[index] = Long.parseLong(args[i]);
			index++;
		}
		
		String[] normalFiles = new File(normalPool).list();
		String[] peakFiles = new File(peakPool).list();
		String[] transFiles = new File(transPool).list();
		
		//Creating users workload files to be used
		Map<Integer, List<String>> workloadFilesPerUser = new HashMap<Integer, List<String>>();
		RandomData random = new RandomDataImpl();
		int nextPeakIndex = 0;
		
		for(int user = 0; user < numberOfClients; user++){
			List<String> workloads = new ArrayList<String>();
			for(int day = 0; day < simulationPeriod; day++){
				
				if(day >= peakDays[nextPeakIndex] - TRANSITION_PERIOD_IN_DAYS && day < peakDays[nextPeakIndex]){//transition workload
					int workloadFileIndex = random.nextInt(0, transFiles.length-1);
					workloads.add(transPool+transFiles[workloadFileIndex]);
					
				}else if(day >= peakDays[nextPeakIndex] && day <= peakDays[nextPeakIndex] + TRANSITION_PERIOD_IN_DAYS){//peak workload
					int workloadFileIndex = random.nextInt(0, peakFiles.length-1);
					workloads.add(peakPool+peakFiles[workloadFileIndex]);
					
				}else{//normal workload
					int workloadFileIndex = random.nextInt(0, normalFiles.length-1);
					workloads.add(normalPool+normalFiles[workloadFileIndex]);
				}
			}
			
			workloadFilesPerUser.put(user, workloads);
		}
		
		createOutputFile(outputFile, workloadFilesPerUser);
	}
	
	private static void createOutputFile(String outputFile, Map<Integer, List<String>> workloadFilesPerUser) throws IOException {
		BufferedWriter usersPropertiesWriter = new BufferedWriter(new FileWriter(outputFile));
		usersPropertiesWriter.write("saas.number="+workloadFilesPerUser.size()+"\n\n");
		
		for(Entry<Integer, List<String>> entry : workloadFilesPerUser.entrySet()){
			usersPropertiesWriter.write("saas.user.id="+entry.getKey()+"\n");
			usersPropertiesWriter.write("saas.user.plan="+DEFAULT_PLAN+"\n");
			usersPropertiesWriter.write("saas.user.storage="+DEFAULT_STORAGE_IN_BYTES+"\n");
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
