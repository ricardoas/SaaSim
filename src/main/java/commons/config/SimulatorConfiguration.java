package commons.config;

import java.util.Arrays;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

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
	public String[] getApplicationHeuristics() {
		String[] strings = getStringArray(SimulatorProperties.APPLICATION_HEURISTIC);
		String customHeuristic = getString(SimulatorProperties.APPLICATION_CUSTOM_HEURISTIC);
		for (int i = 0; i < strings.length; i++) {
			if(strings[i].isEmpty() || strings[i].equalsIgnoreCase("ROUNDROBIN")){
				strings[i] = RoundRobinHeuristic.class.getCanonicalName();
			} else if(strings[i].equalsIgnoreCase("RANJAN")){
				strings[i] = RanjanScheduler.class.getCanonicalName();
			} else if(strings[i].equalsIgnoreCase("PROFITDRIVEN")){
				strings[i] = ProfitDrivenScheduler.class.getCanonicalName();
			} else if(strings[i].equalsIgnoreCase("CUSTOMIZED")){
				strings[i] = customHeuristic;
			} else {
				throw new RuntimeException("Unsupported value for " + SimulatorProperties.APPLICATION_HEURISTIC + ": " + strings[i]);
			}
		}
		return strings;
	}
	
	public int[] getApplicationInitialServersPerTier() {
		String[] stringArray = getStringArray(SimulatorProperties.APPLICATION_INITIAL_SERVER_PER_TIER);
		if(getApplicationNumOfTiers() != stringArray.length){
			throw new RuntimeException("Check number of values in " + 
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
			throw new RuntimeException("Check number of values in " + 
					SimulatorProperties.APPLICATION_INITIAL_SERVER_PER_TIER + ". It must be equals to what is specified at" + 
					SimulatorProperties.APPLICATION_NUM_OF_TIERS);
		}
		int [] serversPerTier = new int[stringArray.length];
		for (int i = 0; i < serversPerTier.length; i++) {
			serversPerTier[i] = Math.max(1, Integer.valueOf(stringArray[i].isEmpty()? "1": stringArray[i]));
		}
		return serversPerTier;
	}
}
