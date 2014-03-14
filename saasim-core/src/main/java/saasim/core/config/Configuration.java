package saasim.core.config;

import static saasim.core.config.SaaSAppProperties.APPLICATION_HEURISTIC;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import saasim.core.cloud.IaaSProvider;
import saasim.core.cloud.SaaSClient;
import saasim.core.event.EventCheckpointer;
import saasim.core.event.EventScheduler;
import saasim.core.sim.Simulator;

public class Configuration extends ComplexPropertiesConfiguration{

	private static final String DEFAULT_CONFIG_FILEPATH = "saasim.properties";

	/**
	 * Unique instance.
	 */
	private static Configuration instance;
	
	private EventScheduler scheduler;
	private Simulator simulator;
	private IaaSProvider[] providers;
	private SaaSClient[] clients;

	
	/**
	 * Default empty constructor
	 * @throws ConfigurationException 
	 */
	public Configuration() throws ConfigurationException {
		this(DEFAULT_CONFIG_FILEPATH);
	}
	
	/**
	 * @param propertiesFilepath
	 * @throws ConfigurationException
	 */
	public Configuration(String propertiesFilepath) throws ConfigurationException {
		super(propertiesFilepath);
	}
	
	

	@Override
	public void save(){
		assert instance != null;
		
		EventCheckpointer.clear();
		EventCheckpointer.save(
				scheduler, 
				providers, 
				clients, 
				simulator
				);
	}
	
	public void clean() {
		EventCheckpointer.clear();
	}

	/**
	 * @return
	 */
	public Class<?>[] getApplicationHeuristics() {
		String[] strings = getStringArray(APPLICATION_HEURISTIC);
		Class<?> [] heuristicClasses = new Class<?>[strings.length]; 
		
		for (int i = 0; i < strings.length; i++) {
			try {
				heuristicClasses[i] = Class.forName(strings[i]);
			} catch (ClassNotFoundException e) {
				throw new ConfigurationRuntimeException("Problem loading " + strings[i], e);
			}
		}
		return heuristicClasses;
	}

	public Class<?> getDPSHeuristicClass() {
		String heuristicName = getString(SimulatorProperties.DPS_HEURISTIC);
		try {
			return Class.forName(heuristicName);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Problem loading " + heuristicName, e);
		}
	}
	
	public <T extends IaaSProvider> Class<T> getIaaSProviderFactoryClass() {
		String heuristicName = getString("");
		try {
			return (Class<T>) Class.forName(heuristicName);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Problem loading " + heuristicName, e);
		}
	}
	
	public EventScheduler getScheduler() {
		return this.scheduler;
	}

}
