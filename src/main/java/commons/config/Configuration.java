package commons.config;

import static commons.sim.util.ContractProperties.NUMBER_OF_PLANS;
import static commons.sim.util.ContractProperties.PLAN_CPU_LIMIT;
import static commons.sim.util.ContractProperties.PLAN_EXTRA_CPU_COST;
import static commons.sim.util.ContractProperties.PLAN_EXTRA_TRANSFER_COST;
import static commons.sim.util.ContractProperties.PLAN_NAME;
import static commons.sim.util.ContractProperties.PLAN_PRICE;
import static commons.sim.util.ContractProperties.PLAN_PRIORITY;
import static commons.sim.util.ContractProperties.PLAN_SETUP;
import static commons.sim.util.ContractProperties.PLAN_TRANSFER_LIMIT;
import static commons.sim.util.ProviderProperties.IAAS_COST_TRANSFER_IN;
import static commons.sim.util.ProviderProperties.IAAS_COST_TRANSFER_OUT;
import static commons.sim.util.ProviderProperties.IAAS_MONITORING;
import static commons.sim.util.ProviderProperties.IAAS_NAME;
import static commons.sim.util.ProviderProperties.IAAS_NUMBER_OF_PROVIDERS;
import static commons.sim.util.ProviderProperties.IAAS_ONDEMAND_CPU_COST;
import static commons.sim.util.ProviderProperties.IAAS_ONDEMAND_LIMIT;
import static commons.sim.util.ProviderProperties.IAAS_ONE_YEAR_FEE;
import static commons.sim.util.ProviderProperties.IAAS_RESERVED_CPU_COST;
import static commons.sim.util.ProviderProperties.IAAS_RESERVED_LIMIT;
import static commons.sim.util.ProviderProperties.IAAS_THREE_YEARS_FEE;
import static commons.sim.util.ProviderProperties.IAAS_TRANSFER_IN;
import static commons.sim.util.ProviderProperties.IAAS_TRANSFER_OUT;
import static commons.sim.util.SimulatorProperties.APPLICATION_CUSTOM_HEURISTIC;
import static commons.sim.util.SimulatorProperties.APPLICATION_FACTORY;
import static commons.sim.util.SimulatorProperties.APPLICATION_HEURISTIC;
import static commons.sim.util.SimulatorProperties.APPLICATION_INITIAL_SERVER_PER_TIER;
import static commons.sim.util.SimulatorProperties.APPLICATION_MAX_SERVER_PER_TIER;
import static commons.sim.util.SimulatorProperties.APPLICATION_NUM_OF_TIERS;
import static commons.sim.util.SimulatorProperties.DEFAULT_PLANNING_HEURISTIC;
import static commons.sim.util.SimulatorProperties.DPS_CUSTOM_HEURISTIC;
import static commons.sim.util.SimulatorProperties.DPS_HEURISTIC;
import static commons.sim.util.SimulatorProperties.MAX_BACKLOG_SIZE;
import static commons.sim.util.SimulatorProperties.MAX_NUM_OF_THREADS_PER_SERVER;
import static commons.sim.util.SimulatorProperties.PLANNING_HEURISTIC;
import static commons.sim.util.SimulatorProperties.PLANNING_PERIOD;
import static commons.sim.util.SimulatorProperties.SETUP_TIME;
import static commons.sim.util.SimulatorProperties.SLA;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.PropertiesConfiguration;

import provisioning.DynamicProvisioningSystem;
import provisioning.ProfitDrivenProvisioningSystem;
import provisioning.RanjanProvisioningSystem;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.sim.schedulingheuristics.ProfitDrivenHeuristic;
import commons.sim.schedulingheuristics.RanjanHeuristic;
import commons.sim.schedulingheuristics.RoundRobinHeuristic;
import commons.sim.util.SimpleApplicationFactory;
import commons.sim.util.SimulatorProperties;
import commons.sim.util.UsersProperties;


/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class Configuration	extends PropertiesConfiguration{
	
	/**
	 * Unique instance.
	 */
	private static Configuration instance;
	
	private List<Provider> providers;
	
	public List<User> users;
	
	/**
	 * Builds the single instance of this configuration.
	 * @param propertiesFileName
	 * @throws ConfigurationException
	 */
	public static void buildInstance(String propertiesFileName) throws ConfigurationException{
		instance = new Configuration(propertiesFileName);
		instance.verifyProperties();
	}

	/**
	 * Returns the single instance of this configuration.
	 * @return
	 */
	public static Configuration getInstance(){
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
	
	private void verifyProperties(){
		verifySimulatorProperties();
		verifyIaaSProperties();
		verifySaaSProperties();
	}
	
	// ************************************* SIMULATOR ************************************/
	
	
	private void verifySimulatorProperties() {
		setProperty(APPLICATION_FACTORY, 
				getString(APPLICATION_FACTORY, SimpleApplicationFactory.class.getCanonicalName()));
		
		int numOfTiers = Math.max(getInt(APPLICATION_NUM_OF_TIERS, 1), 1);
		setProperty(APPLICATION_NUM_OF_TIERS, numOfTiers);
		
		checkSizeAndContent(APPLICATION_INITIAL_SERVER_PER_TIER, numOfTiers, APPLICATION_NUM_OF_TIERS, "1");
		checkSizeAndContent(APPLICATION_MAX_SERVER_PER_TIER, numOfTiers, APPLICATION_NUM_OF_TIERS, Integer.MAX_VALUE+"");
		checkSizeAndContent(APPLICATION_HEURISTIC, numOfTiers, APPLICATION_NUM_OF_TIERS, AppHeuristicValues.ROUNDROBIN.name());
		
		checkSchedulingHeuristicNames();
		checkDPSHeuristic();
		
		setProperty(SETUP_TIME, Math.max(getLong(SETUP_TIME, 0), 0));
	}

	/**
	 * @param propertyName
	 * @param size
	 * @param sizeProperty TODO
	 */
	private void checkSizeAndContent(String propertyName, int size, String sizeProperty, String defaultValue) {
		String[] values = getStringArray(propertyName);
		checkSize(propertyName, size, sizeProperty);
		if(values.length == 0){
			values = new String[size];
			Arrays.fill(values, defaultValue);
			setProperty(propertyName, values);
		}else{
			for (int i = 0; i < values.length; i++) {
				values[i] = values[i].trim().isEmpty()? defaultValue: values[i];
			}
		}
	}

	/**
	 * @param propertyName
	 * @param size
	 */
	private void checkSizeAndContent(String propertyName, int size, String sizeProperty) {
		String[] values = getStringArray(propertyName);
		checkSize(propertyName, size, sizeProperty);
		for (String value : values) {
			checkIsNotEmpty(propertyName, value);
		}
	}

	/**
	 * @param propertyName
	 * @param size
	 */
	private void checkSizeAndIntegerContent(String propertyName, int size, String sizeProperty) {
		String[] values = getStringArray(propertyName);
		checkSize(propertyName, size, sizeProperty);
		for (String value : values) {
			checkIsInteger(propertyName, value);
		}
	}

	/**
	 * @param propertyName
	 * @param size
	 */
	private void checkSizeAndDoubleContent(String propertyName, int size, String sizeProperty) {
		String[] values = getStringArray(propertyName);
		checkSize(propertyName, size, sizeProperty);
		for (String value : values) {
			checkIsDouble(propertyName, value);
		}
	}

	private void checkIsNotEmpty(String propertyName, String value) {
		if(value.trim().isEmpty()){
			throw new ConfigurationRuntimeException("Mandatory property " + 
					propertyName + " has no value associated with it.");
		}
	}

	private void checkIsInteger(String propertyName, String value) {
		checkIsNotEmpty(propertyName, value);
		Integer.valueOf(value);
	}

	private void checkIsDouble(String propertyName, String value) {
		checkIsNotEmpty(propertyName, value);
		Double.valueOf(value);
	}

	private void checkSize(String propertyName, int size, String sizePropertyName) {
		String[] values = getStringArray(propertyName);
		if (values.length != size){
			throw new ConfigurationRuntimeException("Check number of values in " + 
					propertyName + ". It must be equals to what is specified at " + 
					sizePropertyName);
		}
	}

	private void checkSchedulingHeuristicNames() {
		String[] strings = getStringArray(APPLICATION_HEURISTIC);
		String customHeuristic = getString(APPLICATION_CUSTOM_HEURISTIC);
		for (int i = 0; i < strings.length; i++) {
			AppHeuristicValues value = AppHeuristicValues.valueOf(strings[i]);
			switch (value) {
				case ROUNDROBIN:
					strings[i] = RoundRobinHeuristic.class.getCanonicalName();
					break;
				case RANJAN:
					strings[i] = RanjanHeuristic.class.getCanonicalName();
					break;
				case PROFITDRIVEN:
					strings[i] = ProfitDrivenHeuristic.class.getCanonicalName();
					break;
				case CUSTOM:
					try {
						strings[i] = Class.forName(customHeuristic).getCanonicalName();
					} catch (ClassNotFoundException e) {
						throw new ConfigurationRuntimeException("Problem loading " + customHeuristic, e);
					}
					break;
				default:
					throw new ConfigurationRuntimeException("Unsupported value for " + SimulatorProperties.APPLICATION_HEURISTIC + ": " + strings[i]);
			}
		}
		
		setProperty(APPLICATION_HEURISTIC, strings);
	}
	
	public int[] getApplicationInitialServersPerTier() {
		String[] stringArray = getStringArray(APPLICATION_INITIAL_SERVER_PER_TIER);
		int [] serversPerTier = new int[stringArray.length];
		for (int i = 0; i < serversPerTier.length; i++) {
			serversPerTier[i] = Integer.valueOf(stringArray[i]);
		}
		return serversPerTier;
	}

	public int[] getApplicationMaxServersPerTier() {
		String[] stringArray = getStringArray(APPLICATION_MAX_SERVER_PER_TIER);
		int [] serversPerTier = new int[stringArray.length];
		for (int i = 0; i < serversPerTier.length; i++) {
			serversPerTier[i] = Integer.valueOf(stringArray[i]);
		}
		return serversPerTier;
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
	
	private void checkDPSHeuristic() {
		String heuristicName = getString(DPS_HEURISTIC);
		String customHeuristicClass = getString(DPS_CUSTOM_HEURISTIC);
		try{
			DPSHeuristicValues value = DPSHeuristicValues.valueOf(heuristicName);
			switch (value) {
				case STATIC:
					heuristicName = DynamicProvisioningSystem.class.getCanonicalName();
					break;
				case RANJAN:
					heuristicName = RanjanProvisioningSystem.class.getCanonicalName();
					break;
				case PROFITDRIVEN:
					heuristicName = ProfitDrivenProvisioningSystem.class.getCanonicalName();
					break;
				case CUSTOM:
					heuristicName = Class.forName(customHeuristicClass).getCanonicalName();
					break;
			}
			setProperty(DPS_HEURISTIC, heuristicName);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Problem loading " + customHeuristicClass, e);
		}
	}

	public Class<?> getDPSHeuristicClass() {
		String heuristicName = getString(DPS_HEURISTIC);
		try {
			return Class.forName(heuristicName);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Problem loading " + heuristicName, e);
		}
	}
	
	
	// ************************************* PROVIDERS ************************************/
	
	private void verifyIaaSProperties() {
		
		checkIsInteger(IAAS_NUMBER_OF_PROVIDERS,getString(IAAS_NUMBER_OF_PROVIDERS));
		
		int numberOfProviders = getInt(IAAS_NUMBER_OF_PROVIDERS);
		
		checkSizeAndContent(IAAS_NAME, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndDoubleContent(IAAS_ONDEMAND_CPU_COST, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndIntegerContent(IAAS_ONDEMAND_LIMIT, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndDoubleContent(IAAS_RESERVED_CPU_COST, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndIntegerContent(IAAS_RESERVED_LIMIT, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndIntegerContent(IAAS_ONE_YEAR_FEE, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndIntegerContent(IAAS_THREE_YEARS_FEE, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndDoubleContent(IAAS_MONITORING, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		
		checkSizeAndContent(IAAS_TRANSFER_IN, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_COST_TRANSFER_IN, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_TRANSFER_OUT, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_COST_TRANSFER_OUT, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
	}
	
	/**
	 * This method is responsible for reading providers properties and creating the
	 * cloud providers to be used in simulation.
	 * @return
	 * @throws IOException
	 */
	public List<Provider> getProviders() {
		if(this.providers == null){
			this.buildProvider();
		}
		
		return this.providers;
	}
	
	private void buildProvider() {
		int numberOfProviders = getInt(IAAS_NUMBER_OF_PROVIDERS);
		String[] names = getStringArray(IAAS_NAME);
		
		String[] cpuCosts = getStringArray(IAAS_ONDEMAND_CPU_COST);
		String[] onDemandLimits = getStringArray(IAAS_ONDEMAND_LIMIT);
		String[] reservedCpuCosts = getStringArray(IAAS_RESERVED_CPU_COST);
		String[] reservedLimits = getStringArray(IAAS_RESERVED_LIMIT);
		String[] reservationOneYearFees = getStringArray(IAAS_ONE_YEAR_FEE);
		String[] reservationThreeYearsFees = getStringArray(IAAS_THREE_YEARS_FEE);
		String[] monitoringCosts = getStringArray(IAAS_MONITORING);
		String[] transferInLimits = getStringArray(IAAS_TRANSFER_IN);
		String[] transferInCosts = getStringArray(IAAS_COST_TRANSFER_IN);
		String[] transferOutLimits = getStringArray(IAAS_TRANSFER_OUT);
		String[] transferOutCosts = getStringArray(IAAS_COST_TRANSFER_OUT);
		
		providers = new ArrayList<Provider>();
		for(int i = 0; i < numberOfProviders; i++){

			long [] inLimits = convertToLongArray(transferInLimits[i]);
			double [] inCosts = convertToDoubleArray(transferInCosts[i]);
			long [] outLimits = convertToLongArray(transferOutLimits[i]);
			double [] outCosts = convertToDoubleArray(transferOutCosts[i]);

			providers.add(new Provider(names[i], Double.valueOf(cpuCosts[i]), Integer.valueOf(onDemandLimits[i]),
							Integer.valueOf(reservedLimits[i]), Double.valueOf(reservedCpuCosts[i]),
							Double.valueOf(reservationOneYearFees[i]), Double.valueOf(reservationThreeYearsFees[i]),
							Double.valueOf(monitoringCosts[i]), inLimits, inCosts, 
							outLimits, outCosts));
		}
	}


	// ************************************* SAAS ************************************/

	private void verifySaaSProperties() {
		checkIsInteger(NUMBER_OF_PLANS,getString(NUMBER_OF_PLANS));

		int numberOfPlans = getInt(NUMBER_OF_PLANS);
		
		checkSizeAndContent(PLAN_NAME, numberOfPlans,NUMBER_OF_PLANS);
		checkSizeAndDoubleContent(PLAN_PRICE, numberOfPlans, NUMBER_OF_PLANS);
		checkSizeAndDoubleContent(PLAN_SETUP, numberOfPlans, NUMBER_OF_PLANS);
		checkSizeAndDoubleContent(PLAN_CPU_LIMIT, numberOfPlans, NUMBER_OF_PLANS);
		checkSizeAndDoubleContent(PLAN_EXTRA_CPU_COST, numberOfPlans, NUMBER_OF_PLANS);
		checkSizeAndContent(PLAN_TRANSFER_LIMIT, numberOfPlans, NUMBER_OF_PLANS);
		checkSizeAndContent(PLAN_EXTRA_TRANSFER_COST, numberOfPlans, NUMBER_OF_PLANS);
	}

	public String getPlanningHeuristic(){
		return getString(PLANNING_HEURISTIC, DEFAULT_PLANNING_HEURISTIC);
	}
	
	public double getSLA(){
		return getDouble(SLA, Double.MAX_VALUE);
	}
	
	public long getPlanningPeriod(){
		return getLong(PLANNING_PERIOD);
	}
	
	
	
	/**
	 * This method is responsible for reading contracts properties and creating the
	 * associations between contracts and users that requested the services of each contract
	 * @return A map containing each contract name and its characterization
	 * @throws IOException
	 */
	public List<User> getUsers() {
		if(this.users == null){
			this.buildPlans();
		}
		
		return this.users;
	}
	
	private void buildPlans() {
		
		Map<String, Contract> contractsPerName = new HashMap<String, Contract>();
		
		int numberOfPlans = getInt(NUMBER_OF_PLANS);
		String[] planNames = getStringArray(PLAN_NAME);
		String[] planPriorities = getStringArray(PLAN_PRIORITY);
		String[] prices = getStringArray(PLAN_PRICE);
		String[] setupCosts = getStringArray(PLAN_SETUP);
		String[] cpuLimits = getStringArray(PLAN_CPU_LIMIT);
		String[] extraCpuCosts = getStringArray(PLAN_EXTRA_CPU_COST);
		String[] planTransferLimits = getStringArray(PLAN_TRANSFER_LIMIT);
		String[] planExtraTransferCost = getStringArray(PLAN_EXTRA_TRANSFER_COST);
		
		for(int i = 0; i < numberOfPlans; i++){
			long[] limits = convertToLongArray(planTransferLimits[i]);
			double[] costs = convertToDoubleArray(planExtraTransferCost[i]);

			contractsPerName.put(planNames[i], 
					new Contract(planNames[i], Integer.valueOf(planPriorities[i]), Double.valueOf(setupCosts[i]), Double.valueOf(prices[i]), 
							Long.valueOf(cpuLimits[i]), Double.valueOf(extraCpuCosts[i]), limits, costs));
		}
		
		//Extract users associations
		users = new ArrayList<User>();
		String[] plans = Configuration.getInstance().getStringArray(UsersProperties.USER_PLAN);
		for (int i = 0; i < plans.length; i++) {
			users.add(new User(contractsPerName.get(plans[i])));
		}
	}

	private double[] convertToDoubleArray(String pipeSeparatedString) {
		String[] stringValues = pipeSeparatedString.split("\\|");
		double [] doubleValues = new double[stringValues.length];
		for (int j = 0; j < doubleValues.length; j++) {
			doubleValues[j] = Double.valueOf(stringValues[j]);
		}
		return doubleValues;
	}

	private long[] convertToLongArray(String pipeSeparatedString) {
		String[] stringValues = pipeSeparatedString.split("\\|");
		long [] doubleValues = new long[stringValues.length];
		for (int j = 0; j < doubleValues.length; j++) {
			doubleValues[j] = Long.valueOf(stringValues[j]);
		}
		return doubleValues;
	}

	
	public long getMaximumNumberOfThreadsPerMachine() {
		return getLong(MAX_NUM_OF_THREADS_PER_SERVER, Long.MAX_VALUE);
	}

	public long getMaximumBacklogSize() {
		return getLong(MAX_BACKLOG_SIZE, Long.MAX_VALUE);
	}
}
