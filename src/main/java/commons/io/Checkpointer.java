package commons.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import planning.util.MachineUsageData;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
import commons.util.SimulationInfo;

public class Checkpointer {

	public static final String USERS_DUMP = "users.txt";
	public static final String PROVIDERS_DUMP = "providers.txt";
	public static final String APPLICATION_DUMP = "application.txt";
	public static final String SIMULATION_DUMP = "simulation.txt";
	public static final String MACHINE_DATA_DUMP = "machineData.txt";
	
	/**
	 * Check if there's a previous checkpoint available to read. Such operation consists in check if there
	 * exists a readable and writable simulation status file.
	 * @return <code>true</code> if there is an available checkpoint to read, <code>false</code> otherwise.
	 */
	public static boolean hasCheckpoint(){
		return new File(SIMULATION_DUMP).canWrite();
	}
	
	/**
	 * Use {@link Checkpointer#save(SimulationInfo, User[], Provider[], LoadBalancer[])} instead.
	 * @param info
	 * @param users
	 * @param providers
	 * @param machines
	 * @throws IOException
	 */
	@Deprecated
	public static void dumpObjects(SimulationInfo info, User[] users, Provider[] providers,
			List<Machine> machines) throws IOException{
		
		saveInfo(info);
		saveUsers(users);
		saveProviders(providers);
		dumpMachines(machines);
	}

	public static void save(SimulationInfo info, User[] users, Provider[] providers,
			LoadBalancer[] application) throws IOException{
		
		saveInfo(info);
		saveUsers(users);
		saveProviders(providers);
		saveApplication(application);
	}

	@Deprecated
	private static void dumpMachines(List<Machine> machines) throws IOException {
		if(machines == null){
			return;
		}
		
		FileOutputStream fout = new FileOutputStream(new File(APPLICATION_DUMP));
		ObjectOutputStream out = new ObjectOutputStream(fout);
		
		out.writeObject(machines);
		
		out.close();
		fout.close();
	}

	private static void saveApplication(LoadBalancer[] application) throws IOException {
		if(application == null){
			return;
		}
		
		FileOutputStream fout = new FileOutputStream(new File(APPLICATION_DUMP));
		ObjectOutputStream out = new ObjectOutputStream(fout);
		
		out.writeObject(application);
		
		out.close();
		fout.close();
	}

	private static void saveProviders(Provider[] providers) throws IOException {
		if(providers == null){
			return;
		}
		
		FileOutputStream fout = new FileOutputStream(new File(PROVIDERS_DUMP));
		ObjectOutputStream out = new ObjectOutputStream(fout);
		
		out.writeObject(providers);
		
		out.close();
		fout.close();
	}

	private static void saveUsers(User[] users) throws IOException {
		if(users == null){
			return;
		}
		
		FileOutputStream fout = new FileOutputStream(new File(USERS_DUMP));
		ObjectOutputStream out = new ObjectOutputStream(fout);
		
		out.writeObject(users);
		
		out.close();
		fout.close();
	}

	private static void saveInfo(SimulationInfo info) throws IOException {
		if(info == null){
			return;
		}
		
		FileOutputStream fout = new FileOutputStream(new File(SIMULATION_DUMP));
		ObjectOutputStream out = new ObjectOutputStream(fout);
		
		out.writeObject(info);
		
		out.close();
		fout.close();
	}

	public static void dumpMachineData(MachineUsageData machineData) throws IOException {
		if(machineData == null){
			return;
		}
		
		FileOutputStream fout = new FileOutputStream(new File(MACHINE_DATA_DUMP));
		ObjectOutputStream out = new ObjectOutputStream(fout);
		
		out.writeObject(machineData);
		
		out.close();
		fout.close();
	}
	
	
	public static SimulationInfo loadSimulationInfo() throws IOException, ClassNotFoundException {
		FileInputStream fin = new FileInputStream(SIMULATION_DUMP);
		ObjectInputStream in = new ObjectInputStream(fin);
		try{
			return (SimulationInfo) in.readObject(); 
		}finally{
			in.close();
			fin.close();
		}
	}

	public static LoadBalancer[] loadApplication() throws IOException, ClassNotFoundException {
		FileInputStream fin = new FileInputStream(APPLICATION_DUMP);
		ObjectInputStream in = new ObjectInputStream(fin);
		try{
			return (LoadBalancer[]) in.readObject(); 
		}finally{
			in.close();
			fin.close();
		}

	}

	public static Provider[] loadProviders() throws IOException, ClassNotFoundException {
		FileInputStream fin = new FileInputStream(PROVIDERS_DUMP);
		ObjectInputStream in = new ObjectInputStream(fin);	
		try{
			return (Provider[]) in.readObject(); 
		}finally{
			in.close();
			fin.close();
		}
	}

	public static User[] loadUsers() throws IOException, ClassNotFoundException {
		FileInputStream fin = new FileInputStream(USERS_DUMP);
		ObjectInputStream in = new ObjectInputStream(fin);	
		try{
			return (User[]) in.readObject(); 
		}finally{
			in.close();
			fin.close();
		}

	}
	
	/**
	 * It removes all dump files.
	 */
	public static void clear(){
		new File(SIMULATION_DUMP).delete();
		new File(APPLICATION_DUMP).delete();
		new File(PROVIDERS_DUMP).delete();
		new File(USERS_DUMP).delete();
		new File(MACHINE_DATA_DUMP).delete();
	}
}
