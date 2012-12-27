package saasim.sim.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import saasim.cloud.Provider;
import saasim.cloud.User;
import saasim.planning.util.MachineUsageData;
import saasim.sim.components.LoadBalancer;
import saasim.util.SimulationInfo;


/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class EventCheckpointer {

	public static final String MACHINE_DATA_DUMP = "machineData.txt";
	public static final String CHECKPOINT_FILE = ".je.dat";
	public static final String PROVISIONING_FILE = ".prov.dat";
	
	public static final long INTERVAL = 24 * 60 * 60 * 1000;
	
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
	@Deprecated
	public static void save() {
		ObjectOutputStream out;
		try {
			long now = System.currentTimeMillis();
			Logger.getLogger(EventCheckpointer.class).debug("CHKP SAVE-in");
			out = new ObjectOutputStream(new FileOutputStream(CHECKPOINT_FILE));
			out.close();
			Logger.getLogger(EventCheckpointer.class).debug("CHKP SAVE-out " + (System.currentTimeMillis()-now));
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
	
	
	/**
	 * It removes all dump files.
	 */
	public static void clear(){
		new File(CHECKPOINT_FILE).delete();
		new File(MACHINE_DATA_DUMP).delete();
		new File(PROVISIONING_FILE).delete();
	}

	public static Object[] load(){
		try {
			ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(CHECKPOINT_FILE));
		
			return (Object[]) objectInputStream.readObject();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void save(Object... objects){
		ObjectOutputStream out;
		try {
			Logger.getLogger(EventCheckpointer.class).debug("CHKP SAVE-in");
			out = new ObjectOutputStream(new FileOutputStream(CHECKPOINT_FILE));
			out.writeObject(objects);
			out.close();
			Logger.getLogger(EventCheckpointer.class).debug("CHKP SAVE-out");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Deprecated
	public static void loadData() throws ConfigurationException{
		long now = System.currentTimeMillis();
		Logger.getLogger(EventCheckpointer.class).debug("CHKP LOAD-in");
		Logger.getLogger(EventCheckpointer.class).debug("CHKP LOAD-out " + (System.currentTimeMillis() - now));
	}
	
}
