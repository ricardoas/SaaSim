package commons.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import commons.sim.util.SimpleApplicationFactory;
import commons.sim.util.SimulatorProperties;


/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class SimulatorConfiguration	extends PropertiesConfiguration{
	
	
	/**
	 * Unique instance.
	 */
	private static SimulatorConfiguration instance;
	
	/**
	 * Builds the single instance of this configuration.
	 * @param propertiesFileName
	 * @throws ConfigurationException
	 */
	public static void buildInstance(String propertiesFileName) throws ConfigurationException{
		instance = new SimulatorConfiguration(propertiesFileName);
	}

	/**
	 * Returns the single instance of this configuration.
	 * @return
	 */
	public static SimulatorConfiguration getInstance(){
		return instance;
	}
	
	/**
	 * Private constructor.
	 * 
	 * @param propertiesFileName
	 * @throws ConfigurationException
	 */
	private SimulatorConfiguration(String propertiesFileName) throws ConfigurationException {
		super(propertiesFileName);
	}
	
	/**
	 * @return
	 */
	public String getApplicationFactoryClassName() {
		return getString(SimulatorProperties.APPLICATION_FACTORY, 
				SimpleApplicationFactory.class.getCanonicalName());
	}
	
	/**
	 * 
	 * @return
	 */
	public int getApplicationNumOfTiers() {
		return Math.max(getInt(SimulatorProperties.APPLICATION_NUM_OF_TIERS, 1), 1);
	}
	
}
