package planning.util;

import static commons.io.Checkpointer.MACHINE_DATA_DUMP;

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

import commons.cloud.MachineType;
import commons.cloud.Provider;

public class PlanIOHandler {
	
	private static final String OUTPUT_FILE = "output.plan";
	public static final String NUMBER_OF_MACHINES_FILE = "maxServers.dat";
	
	public static void clear(){
		new File(NUMBER_OF_MACHINES_FILE).delete();
	}
	
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
	
	public static void createNumberOfMachinesFile(int maximumNumberOfServers, int[] nextRequestsCounter, double requestsMeanDemand) throws IOException{
		FileWriter writer = new FileWriter(new File(NUMBER_OF_MACHINES_FILE));
		writer.write(maximumNumberOfServers+"\n");
		writer.write(requestsMeanDemand+"\n");
		for(int value : nextRequestsCounter){
			writer.write(value+"\t");
		}
		writer.close();
	}
	
	public static int getNumberOfMachinesFromFile() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(NUMBER_OF_MACHINES_FILE)));
		int numberOfServers = Integer.parseInt(reader.readLine());
		reader.close();
		return numberOfServers;
	}
	
	public static double getRequestsMeanDemandFromFile() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(NUMBER_OF_MACHINES_FILE)));
		reader.readLine();
		double requestsMeanDemand = Double.parseDouble(reader.readLine());
		reader.close();
		return requestsMeanDemand;
	}
	
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
