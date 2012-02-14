package saasim.planning.util;

import static saasim.sim.jeevent.JECheckpointer.MACHINE_DATA_DUMP;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Map;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.planning.heuristic.PlanningHeuristic;


/**
 * This class represents a manager of the files used in {@link PlanningHeuristic}s.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br 
 */
public class PlanIOHandler {
	
	private static final String OUTPUT_FILE = "output.plan";
	public static final String NUMBER_OF_MACHINES_FILE = "maxServers.dat";
	
	/**
	 * Delete the file who represents a number of machines.
	 */
	public static void clear(){
		new File(NUMBER_OF_MACHINES_FILE).delete();
	}
	
	/**
	 * Gets the machine of data, see {@link MachineUsageData}. 
	 * @return The recovered {@link MachineUsageData}.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static MachineUsageData getMachineData() throws IOException, ClassNotFoundException {
		if(new File(MACHINE_DATA_DUMP).exists()){
			File simulationDump = new File(MACHINE_DATA_DUMP);
			FileInputStream fin = new FileInputStream(simulationDump);
			ObjectInputStream in = new ObjectInputStream(fin);
			try{
				MachineUsageData data = (MachineUsageData) in.readObject();
				return data;
			}catch(EOFException e){
			}finally{
				in.close();
				fin.close();
			}
		}
		return null;
	}
	
	/**
	 * Create the file who represents a number of machines.
	 * @param maximumNumberOfServers the maximum number of serves
	 * @param nextRequestsCounter the next request counter
	 * @param requestsMeanDemand the mean demand of requests
	 * @throws IOException
	 */
	public static void createNumberOfMachinesFile(int maximumNumberOfServers, int[] nextRequestsCounter, double requestsMeanDemand) throws IOException{
		FileWriter writer = new FileWriter(new File(NUMBER_OF_MACHINES_FILE));
		writer.write(maximumNumberOfServers+"\n");
		writer.write(requestsMeanDemand+"\n");
		for(int value : nextRequestsCounter){
			writer.write(value+"\t");
		}
		writer.close();
	}
	
	/**
	 * Gets the number of machine recovered from {@link PlanIOHandler#NUMBER_OF_MACHINES_FILE}.
	 * @return An integer to represent the number of machines.
	 * @throws IOException
	 */
	public static int getNumberOfMachinesFromFile() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(NUMBER_OF_MACHINES_FILE)));
		int numberOfServers = Integer.parseInt(reader.readLine());
		reader.close();
		return numberOfServers;
	}
	
	/**
	 * Gets the mean demand of requests recovered from {@link PlanIOHandler#NUMBER_OF_MACHINES_FILE}.
	 * @return A double represents the mean demand of requests.
	 * @throws IOException
	 */
	public static double getRequestsMeanDemandFromFile() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(NUMBER_OF_MACHINES_FILE)));
		reader.readLine();
		double requestsMeanDemand = Double.parseDouble(reader.readLine());
		reader.close();
		return requestsMeanDemand;
	}
	
	/**
	 * Gets the number of machines recovered from {@link PlanIOHandler#NUMBER_OF_MACHINES_FILE} and put them 
	 * in an array of integers.
	 * @return An array of integers containing the number of machines.
	 * @throws IOException
	 */
	public static int[] getNumberOfMachinesArray() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(NUMBER_OF_MACHINES_FILE)));
		reader.readLine();
		reader.readLine();
		
		String line = reader.readLine();
		String[] data = line.split("\t");
		int[] values = new int[data.length];
		for(int i = 0; i < values.length; i++){
			values[i] = Integer.parseInt(data[i]);
		}
		
		reader.close();
		return values;
	}
	
	/**
	 * Creates a plan file.
	 * @param plan a {@link Map} represents features of plan
	 * @param providers the {@link Provider}s of application
	 * @throws IOException
	 */
	public static void createPlanFile(Map<MachineType, Integer> plan, Provider[] providers) throws IOException {
		if(plan.size() == 0){
			return;
		}
		
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
