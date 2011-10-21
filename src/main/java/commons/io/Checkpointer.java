package commons.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import planning.util.MachineUsageData;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.sim.AccountingSystem;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEEventScheduler;
import commons.util.SimulationInfo;

public class Checkpointer {

	public static final String MACHINE_DATA_DUMP = "machineData.txt";
	public static final String CHECKPOINT_FILE = ".je.dat";
	
	public static final long INTERVAL = 24 * 60 * 60 * 1000;
	
	private static JEEventScheduler scheduler;
	private static LoadBalancer[] application;
	private static Provider[] providers;
	private static User[] users;
	private static SimulationInfo simulationInfo;
	private static AccountingSystem accountingSystem;
	
	/**
	 * Check if there's a previous checkpoint available to read. Such operation consists in check if there
	 * exists a readable and writable simulation status file.
	 * @return <code>true</code> if there is an available checkpoint to read, <code>false</code> otherwise.
	 */
	public static boolean hasCheckpoint(){
		return new File(CHECKPOINT_FILE).canWrite();
	}
	
	public static void save(SimulationInfo info, User[] users, Provider[] providers,
			LoadBalancer[] application){
		
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new FileOutputStream(CHECKPOINT_FILE));
			out.writeObject(JEEventScheduler.getInstance());
			out.writeObject(info);
			out.writeObject(application);
			out.writeObject(providers);
			out.writeObject(users);
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
	
	
	public static SimulationInfo loadSimulationInfo() {
		if(hasCheckpoint() && simulationInfo == null){
			loadData();
		}
		return simulationInfo;
	}

	public static LoadBalancer[] loadApplication() {
		if(hasCheckpoint() && application == null){
			loadData();
		}
		return application;
	}

	public static Provider[] loadProviders() {
		if(hasCheckpoint() && providers == null){
			loadData();
		}
		return providers;
	}

	public static User[] loadUsers() {
		if(hasCheckpoint() && users == null){
			loadData();
		}
		return users;
	}
	
	/**
	 * It removes all dump files.
	 */
	public static void clear(){
		new File(CHECKPOINT_FILE).delete();
		new File(MACHINE_DATA_DUMP).delete();
		scheduler = null;
		simulationInfo = null;
		application = null;
		providers = null;
		users = null;
	}

	/**
	 * @return
	 */
	public static JEEventScheduler loadScheduler() {
		if(hasCheckpoint() && scheduler == null){
			loadData();
		}
		return scheduler;
	}

	private static void loadData(){
		ObjectInputStream in;
		try {
			in = new ObjectInputStream(new FileInputStream(CHECKPOINT_FILE));
			scheduler = (JEEventScheduler) in.readObject();
			scheduler.prepare();
			simulationInfo = (SimulationInfo) in.readObject(); 
			application = (LoadBalancer[]) in.readObject();
			providers = (Provider[]) in.readObject();
			users = (User[]) in.readObject();
			in.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static AccountingSystem loadAccountingSystem() {
		if(hasCheckpoint() && accountingSystem == null){
			loadData();
		}
		return accountingSystem;
	}
}
