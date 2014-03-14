package saasim.core.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;


/**
 * Event checkpointer saves all SaaSim state in a single file named ".saasim.dat". Once this file is
 * loaded in a consequent execution it is automatically renamed to include current time in its name.
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public final class EventCheckpointer {

	public static final String CHECKPOINT_FILE = ".saasim.ckp";
	
	/**
	 * Check if there's a previous checkpoint available to read. Such operation consists in check if there
	 * exists a readable and writable simulation status file.
	 * @return <code>true</code> if there is an available checkpoint to read, <code>false</code> otherwise.
	 */
	public static boolean hasCheckpoint(){
		return new File(CHECKPOINT_FILE).canWrite();
	}
	
	/**
	 * It removes the checkpoint file.
	 */
	public static void clear(){
		new File(CHECKPOINT_FILE).delete();
	}

	/**
	 * It renames the checkpoint file.
	 */
	private static void rename(){
		new File(CHECKPOINT_FILE).renameTo(new File(CHECKPOINT_FILE + "." + new Date().toString().replaceAll(" ", "_") ));
	}

	/**
	 * @return Load all saved objects. All possible exceptions were encapsulated in an {@link RuntimeException}.
	 * This is a horrible programming practice, we know. But we'd like a fast working solution. 
	 */
	public static Serializable[] load(){
		Logger.getLogger(EventCheckpointer.class).debug("CHKP LOAD-in");
		ObjectInputStream objectInputStream = null;
		try {
			objectInputStream = new ObjectInputStream(new FileInputStream(CHECKPOINT_FILE));
			Serializable[] objects = (Serializable[]) objectInputStream.readObject();
			objectInputStream.close();
			rename();
			Logger.getLogger(EventCheckpointer.class).debug("CHKP LOAD-out");
			return objects;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param objects Save all objects. All possible exceptions were encapsulated in an {@link RuntimeException}.
	 * This is a horrible programming practice, we know. But we'd like a fast working solution. 
	 */
	public static void save(Serializable... objects){
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
}
