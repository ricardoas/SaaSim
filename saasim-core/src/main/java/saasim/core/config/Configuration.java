package saasim.core.config;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.PropertiesConfiguration;

import saasim.core.event.EventCheckpointer;
import saasim.core.event.EventScheduler;

public class Configuration extends PropertiesConfiguration{

	private static Configuration instance;

	private EventScheduler scheduler;

	/**
	 * Builds the single instance of this configuration.
	 * @param propertiesFileName
	 * @throws ConfigurationException
	 */
	public static void buildInstance(String propertiesFileName) throws ConfigurationException{
		instance = new Configuration(propertiesFileName);

		if(EventCheckpointer.hasCheckpoint()){
			Iterator<Object> iterator = Arrays.asList(EventCheckpointer.load()).iterator();
			instance.scheduler = (EventScheduler) iterator.next();
		}else{
			instance.scheduler = new EventScheduler(instance.getLong("random.seed", 31));
		}
	}

	/**
	 * Returns the single instance of this configuration.
	 * @return
	 */
	public static Configuration getInstance(){
		if(instance == null){
			throw new ConfigurationRuntimeException();
		}
		return instance;
	}

	/**
	 * Private constructor.
	 * 
	 * @param propertiesFileName
	 * @throws ConfigurationException
	 */
	private Configuration(String propertiesFileName) throws ConfigurationException {
		super(propertiesFileName);
	}

	@Override
	public void save(){
		assert instance != null;
		
		EventCheckpointer.clear();
		EventCheckpointer.save(
				scheduler
				);
	}


	public EventScheduler getScheduler() {
		return scheduler;
	}
}
