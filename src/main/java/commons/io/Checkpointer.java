package commons.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import planning.util.MachineUsageData;
import provisioning.util.DPSInfo;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.config.Configuration;
import commons.sim.AccountingSystem;
import commons.sim.Simulator;
import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.SimulatorFactory;
import commons.util.SimulationInfo;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class Checkpointer {

	public static final String MACHINE_DATA_DUMP = "machineData.txt";
	public static final String CHECKPOINT_FILE = ".je.dat";
	public static final String PROVISIONING_FILE = ".prov.dat";
	
	public static final long INTERVAL = 24 * 60 * 60 * 1000;
	
	private static JEEventScheduler scheduler;
	private static SimulationInfo simulationInfo;
	private static Simulator application;
	private static Provider[] providers;
	private static User[] users;
	private static AccountingSystem accountingSystem;
	private static int[] priorities;
	private static DPSInfo dpsInfo;
	
	/**
	 * Check if there's a previous checkpoint available to read. Such operation consists in check if there
	 * exists a readable and writable simulation status file.
	 * @return <code>true</code> if there is an available checkpoint to read, <code>false</code> otherwise.
	 */
	public static boolean hasCheckpoint(){
		return new File(CHECKPOINT_FILE).canWrite();
	}
	
	/**
	 * 
	 */
	public static void save() {
		ObjectOutputStream out;
		try {
			long now = System.currentTimeMillis();
			Logger.getLogger(Checkpointer.class).debug("CHKP SAVE-in");
			out = new ObjectOutputStream(new FileOutputStream(CHECKPOINT_FILE));
			out.writeObject(scheduler);
			out.writeObject(simulationInfo);
			out.writeObject(application);
			out.writeObject(providers);
			out.writeObject(users);
			out.writeObject(accountingSystem);
			out.writeObject(priorities);
			out.writeObject(dpsInfo);
			out.close();
			Logger.getLogger(Checkpointer.class).debug("CHKP SAVE-out " + (System.currentTimeMillis()-now) + " " + simulationInfo);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Deprecated
	public static void save(SimulationInfo info, User[] users, Provider[] providers,
			LoadBalancer[] application){
		
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new FileOutputStream(CHECKPOINT_FILE));
			out.writeObject(Checkpointer.loadScheduler());
			out.writeObject(info);
			out.writeObject(application);
			out.writeObject(providers);
			out.writeObject(users);
			out.writeObject(accountingSystem);
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
		return simulationInfo;
	}

	public static Simulator loadApplication() {
		return application;
	}

	public static Provider[] loadProviders() {
		return providers;
	}

	public static User[] loadUsers() {
		return users;
	}
	
	/**
	 * It removes all dump files.
	 */
	public static void clear(){
		new File(CHECKPOINT_FILE).delete();
		new File(MACHINE_DATA_DUMP).delete();
		new File(PROVISIONING_FILE).delete();
	}

	/**
	 * @return
	 */
	public static JEEventScheduler loadScheduler() {
		return scheduler;
	}

	/**
	 * @return
	 */
	public static DPSInfo loadProvisioningInfo() {
		return dpsInfo;
	}

	public static void loadData() throws ConfigurationException{
		long now = System.currentTimeMillis();
		Logger.getLogger(Checkpointer.class).debug("CHKP LOAD-in");
		if(hasCheckpoint()){
			ObjectInputStream in;
			try {
				in = new ObjectInputStream(new FileInputStream(CHECKPOINT_FILE));
				scheduler = (JEEventScheduler) in.readObject();
				simulationInfo = (SimulationInfo) in.readObject(); 
				simulationInfo.addDay();
				scheduler.reset(simulationInfo.getCurrentDayInMillis(), simulationInfo.getCurrentDayInMillis() + INTERVAL);
				application = (Simulator) in.readObject();
				providers = (Provider[]) in.readObject();
				users = (User[]) in.readObject();
				accountingSystem = (AccountingSystem) in.readObject();
				priorities = (int []) in.readObject();
				dpsInfo = (DPSInfo) in.readObject();
				in.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			Checkpointer.clear();
		}else{
			simulationInfo = new SimulationInfo();
			scheduler = new JEEventScheduler(INTERVAL);
			application = SimulatorFactory.buildSimulator(Checkpointer.loadScheduler());
			providers = Configuration.getInstance().getProviders();
			users = Configuration.getInstance().getUsers();
			accountingSystem = new AccountingSystem(users, providers);
			priorities = new int[users.length];
			for (int i = 0; i < priorities.length; i++) {
				priorities[i] = 100;//users[i].getContract().getPriority(); FIXME Ricardo: don't know if we need this anymore
			}
			dpsInfo = new DPSInfo();
		}
		Logger.getLogger(Checkpointer.class).debug("CHKP LOAD-out " + (System.currentTimeMillis() - now) + " " + simulationInfo);
	}

	public static AccountingSystem loadAccountingSystem() {
		return accountingSystem;
	}
	
	public static int[] loadPriorities() {
		return priorities;
	}
	
	
}
