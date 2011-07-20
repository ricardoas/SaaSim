package commons.config;

import java.util.Arrays;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.PropertiesConfiguration;

import provisioning.StaticProvisioningSystem;

import commons.sim.ProfitDrivenScheduler;
import commons.sim.RanjanScheduler;
import commons.sim.schedulingheuristics.RoundRobinHeuristic;
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

	/**
	 * @return
	 */
	public Class<?>[] getApplicationHeuristics() {
		String[] strings = getStringArray(SimulatorProperties.APPLICATION_HEURISTIC);
		String customHeuristic = getString(SimulatorProperties.APPLICATION_CUSTOM_HEURISTIC);
		Class<?> [] heuristicClasses = new Class<?>[strings.length]; 
		for (int i = 0; i < strings.length; i++) {
			if(strings[i].isEmpty()){
				strings[i] = AppHeuristicValues.ROUNDROBIN.name();
			}
			AppHeuristicValues value = AppHeuristicValues.valueOf(strings[i]);
			switch (value) {
			case ROUNDROBIN:
				heuristicClasses[i] = RoundRobinHeuristic.class;
				break;
			case RANJAN:
				heuristicClasses[i] = RanjanScheduler.class;
				break;
			case PROFITDRIVEN:
				heuristicClasses[i] = ProfitDrivenScheduler.class;
				break;
			case CUSTOM:
				try {
					heuristicClasses[i] = Class.forName(customHeuristic);
				} catch (ClassNotFoundException e) {
					throw new ConfigurationRuntimeException("Problem loading " + customHeuristic, e);
				}
				break;
			default:
				throw new ConfigurationRuntimeException("Unsupported value for " + SimulatorProperties.APPLICATION_HEURISTIC + ": " + strings[i]);
			}
		}
		return heuristicClasses;
	}
	
	public int[] getApplicationInitialServersPerTier() {
		String[] stringArray = getStringArray(SimulatorProperties.APPLICATION_INITIAL_SERVER_PER_TIER);
		if(getApplicationNumOfTiers() != stringArray.length){
			throw new ConfigurationRuntimeException("Check number of values in " + 
					SimulatorProperties.APPLICATION_INITIAL_SERVER_PER_TIER + ". It must be equals to what is specified at" + 
					SimulatorProperties.APPLICATION_NUM_OF_TIERS);
		}
		int [] serversPerTier = new int[stringArray.length];
		for (int i = 0; i < serversPerTier.length; i++) {
			serversPerTier[i] = Math.max(1, Integer.valueOf(stringArray[i]));
		}
		return serversPerTier;
	}

	public int[] getApplicationMaxServersPerTier() {
		String[] stringArray = getStringArray(SimulatorProperties.APPLICATION_MAX_SERVER_PER_TIER);
		if(stringArray.length == 0){
			stringArray = new String[getApplicationNumOfTiers()];
			Arrays.fill(stringArray, "");
		}
		if(getApplicationNumOfTiers() != stringArray.length){
			throw new ConfigurationRuntimeException("Check number of values in " + 
					SimulatorProperties.APPLICATION_INITIAL_SERVER_PER_TIER + ". It must be equals to what is specified at" + 
					SimulatorProperties.APPLICATION_NUM_OF_TIERS);
		}
		int [] serversPerTier = new int[stringArray.length];
		for (int i = 0; i < serversPerTier.length; i++) {
			serversPerTier[i] = Math.max(1, Integer.valueOf(stringArray[i].isEmpty()? "1": stringArray[i]));
		}
		return serversPerTier;
	}

	public Class<?> getDPSHeuristicClass() {
		String heuristicName = getString(SimulatorProperties.DPS_HEURISTIC);
		String customHeuristicClass = getString(SimulatorProperties.DPS_CUSTOM_HEURISTIC);
		try{
			DPSHeuristicValues value = DPSHeuristicValues.valueOf(heuristicName);
			switch (value) {
			case STATIC:
				return StaticProvisioningSystem.class;
//			case RANJAN:
//				return StaticProvisioningSystem.class;//FIXME
//			case PROFITDRIVEN:
//				return StaticProvisioningSystem.class;//FIXME
			case CUSTOM:
				return Class.forName(customHeuristicClass);
			}
			
		} catch (IllegalArgumentException iae) {
			throw new ConfigurationRuntimeException("Unsupported value for " + 
						SimulatorProperties.DPS_HEURISTIC + ": " + heuristicName, iae);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Problem loading " + customHeuristicClass, e);
		}
		return null;
		
	}
}
