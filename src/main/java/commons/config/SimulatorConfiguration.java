package commons.config;

import static commons.sim.util.ContractProperties.*;
import static commons.sim.util.ProviderProperties.*;
import static commons.sim.util.SimulatorProperties.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.PropertiesConfiguration;

import provisioning.ProfitDrivenProvisioningSystem;
import provisioning.RanjanProvisioningSystem;
import provisioning.StaticProvisioningSystem;

import commons.cloud.Contract;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.sim.schedulingheuristics.ProfitDrivenHeuristic;
import commons.sim.schedulingheuristics.RanjanHeuristic;
import commons.sim.schedulingheuristics.RoundRobinHeuristic;
import commons.sim.util.SimpleApplicationFactory;
import commons.sim.util.SimulatorProperties;


/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class SimulatorConfiguration	extends PropertiesConfiguration{
	
	/**
	 * Unique instance.
	 */
	private static SimulatorConfiguration instance;
	
	private Map<String, Provider> providers;//Map containing providers to be simulated
	
	//Users SaaS plans
	public Map<String, Contract> contractsPerName;
	
	public Map<User, Contract> usersContracts;
	
	/**
	 * Builds the single instance of this configuration.
	 * @param propertiesFileName
	 * @throws ConfigurationException
	 */
	public static void buildInstance(String propertiesFileName) throws ConfigurationException{
		instance = new SimulatorConfiguration(propertiesFileName);
		instance.verifyProperties();
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
	
	private void verifyProperties() throws ConfigurationException{
		verifySimulatorProperties();
		verifyIaaSProperties();
		verifySaaSProperties();
		
		
		if(!verifyProviderPropertiesExist()){
			throw new ConfigurationException();
		}
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
		
		changeHeuristicNames();
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
			if(value.trim().isEmpty()){
				throw new ConfigurationRuntimeException("Mandatory property " + 
						propertyName + " has no value associated with it.");
			}
		}
	}
	
	private void checkSize(String propertyName, int size, String sizePropertyName) {
		String[] values = getStringArray(propertyName);
		if (values.length == size){
			throw new ConfigurationRuntimeException("Check number of values in " + 
					propertyName + ". It must be equals to what is specified at" + 
					sizePropertyName);
		}
	}

	private void changeHeuristicNames() {
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
					heuristicName = StaticProvisioningSystem.class.getCanonicalName();
				case RANJAN:
					heuristicName = RanjanProvisioningSystem.class.getCanonicalName();
				case PROFITDRIVEN:
					heuristicName = ProfitDrivenProvisioningSystem.class.getCanonicalName();
				case CUSTOM:
					heuristicName = Class.forName(customHeuristicClass).getCanonicalName();
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
		int numberOfProviders = getInt(IAAS_NUMBER_OF_PROVIDERS);
		
		checkSizeAndContent(IAAS_NAME, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_ONDEMAND_CPU_COST, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_ONDEMAND_LIMIT, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_RESERVED_CPU_COST, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_RESERVED_LIMIT, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_ONE_YEAR_FEE, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_THREE_YEARS_FEE, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_MONITORING, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_TRANSFER_IN, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_COST_TRANSFER_IN, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_TRANSFER_OUT, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);
		checkSizeAndContent(IAAS_COST_TRANSFER_OUT, numberOfProviders, IAAS_NUMBER_OF_PROVIDERS);

	}

	// ************************************* SAAS ************************************/

	private void verifySaaSProperties() {
		int numberOfPlans = getInt(NUMBER_OF_PLANS);
		
		checkSizeAndContent(PLAN_NAME, numberOfPlans,NUMBER_OF_PLANS);
		checkSizeAndContent(PLAN_PRICE, numberOfPlans, NUMBER_OF_PLANS);
		checkSizeAndContent(PLAN_SETUP, numberOfPlans, NUMBER_OF_PLANS);
		checkSizeAndContent(PLAN_CPU_LIMIT, numberOfPlans, NUMBER_OF_PLANS);
		checkSizeAndContent(PLAN_EXTRA_CPU_COST, numberOfPlans, NUMBER_OF_PLANS);
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
	 * This method is responsible for reading providers properties and creating the
	 * cloud providers to be used in simulation.
	 * @return
	 * @throws IOException
	 */
	public Map<String, Provider> getProviders() throws IOException{
		if(this.providers == null){
			if(this.verifyProviderPropertiesExist()){
				this.buildProvider();
			}else{
				throw new IOException("Missing data in providers file!");
			}
		}
		
		return this.providers;
	}
	
	private boolean verifyProviderPropertiesExist(){
		boolean valid = true; 
//		int numberOfPlans = getInt(NUM_OF_PROVIDERS);
//		for(int i = 1; i <= numberOfPlans; i++){
//			valid = valid && containsKey(PROVIDER+i+PROVIDER_NAME) && containsKey(PROVIDER+i+ONDEMAND_CPU_COST) && 
//			containsKey(PROVIDER+i+ON_DEMAND_LIMIT) && containsKey(PROVIDER+i+RESERVED_CPU_COST) &&
//			containsKey(PROVIDER+i+RESERVATION_LIMIT) && containsKey(PROVIDER+i+ONE_YEAR_FEE) && 
//			containsKey(PROVIDER+i+THREE_YEARS_FEE) && containsKey(PROVIDER+i+MONITORING) && 
//			containsKey(PROVIDER+i+TRANSFER_IN) && containsKey(PROVIDER+i+COST_TRANSFER_IN) && 
//			containsKey(PROVIDER+i+TRANSFER_OUT) && containsKey(PROVIDER+i+COST_TRANSFER_OUT) &&
//			!getString(PROVIDER+i+ONDEMAND_CPU_COST).isEmpty() && !getString(PROVIDER+i+PROVIDER_NAME).isEmpty() &&
//			!getString(PROVIDER+i+RESERVED_CPU_COST).isEmpty()
//			&& !getString(PROVIDER+i+ON_DEMAND_LIMIT).isEmpty() && !getString(PROVIDER+i+RESERVATION_LIMIT).isEmpty() 
//			&& !getString(PROVIDER+i+ONE_YEAR_FEE).isEmpty() &&	!getString(PROVIDER+i+THREE_YEARS_FEE).isEmpty() 
//			&& !getString(PROVIDER+i+MONITORING).isEmpty() &&	!getString(PROVIDER+i+TRANSFER_IN).isEmpty()
//			&& !getString(PROVIDER+i+COST_TRANSFER_IN).isEmpty() &&	!getString(PROVIDER+i+TRANSFER_OUT).isEmpty()
//			&& !getString(PROVIDER+i+COST_TRANSFER_OUT).isEmpty(); 
//		}
		return valid;
	}
	
	private void buildProvider() {
//		int numberOfPlans = Integer.valueOf(getInt(NUM_OF_PROVIDERS));
//		providers = new HashMap<String, Provider>();
//		for(int i = 1; i <= numberOfPlans; i++){
//			providers.put(getString(PROVIDER+i+PROVIDER_NAME), 
//					new Provider(getString(PROVIDER+i+PROVIDER_NAME), getDouble(PROVIDER+i+ONDEMAND_CPU_COST), getInt(PROVIDER+i+ON_DEMAND_LIMIT),
//					getInt(PROVIDER+i+RESERVATION_LIMIT), getDouble(PROVIDER+i+RESERVED_CPU_COST),
//					getDouble(PROVIDER+i+ONE_YEAR_FEE), getDouble(PROVIDER+i+THREE_YEARS_FEE), 
//					getDouble(PROVIDER+i+MONITORING), getString(PROVIDER+i+TRANSFER_IN), getString(PROVIDER+i+COST_TRANSFER_IN), 
//					getString(PROVIDER+i+TRANSFER_OUT), getString(PROVIDER+i+COST_TRANSFER_OUT))
//			);
//		}
	}
	
	/**
	 * This method is responsible for reading contracts properties and creating the
	 * associations between contracts and users that requested the services of each contract
	 * @return A map containing each contract name and its characterization
	 * @throws IOException
	 */
	public Map<User, Contract> getContractsPerUser() throws IOException{
//		if(this.usersContracts == null){
//			if(this.verifyContractPropertiesExist()){
//				this.buildPlans();
//			}else{
//				throw new IOException("Missing data in contracts file!");
//			}
//		}
		
		return this.usersContracts;
	}
	
	/**
	 * This method is responsible for reading contracts properties and creating the
	 * associations between contracts and users that requested the services of each contract
	 * @return A map containing each user in the system and its signed contract
	 * @throws IOException
	 */
	public Map<String, Contract> getContractsPerName() throws IOException{
		if(this.contractsPerName == null){
			this.buildPlans();
		}
		
		return this.contractsPerName;
	}
	
	private void buildPlans() {
		
		contractsPerName = new HashMap<String, Contract>();
		
		int numberOfPlans = getInt(NUMBER_OF_PLANS);
		String[] planNames = getStringArray(PLAN_NAME);
		String[] prices = getStringArray(PLAN_PRICE);
		String[] setupCosts = getStringArray(PLAN_SETUP);
		String[] cpuLimits = getStringArray(PLAN_CPU_LIMIT);
		String[] extraCpuCosts = getStringArray(PLAN_EXTRA_CPU_COST);
		String[] planTransferLimits = getStringArray(PLAN_TRANSFER_LIMIT);
		String[] planExtraTransferCost = getStringArray(PLAN_EXTRA_TRANSFER_COST);
		String[] users = getStringArray(PLAN_USERS);
		
		for(int i = 1; i <= numberOfPlans; i++){
			contractsPerName.put(planNames[i], 
					new Contract(planNames[i], Double.valueOf(setupCosts[i]), Double.valueOf(prices[i]), 
							Double.valueOf(cpuLimits[i]), Double.valueOf(extraCpuCosts[i])));
		}
		
		//Extract users associations
//		usersContracts = new HashMap<User, Contract>();
//		for(String userPlan : users){
//			String[] split = userPlan.split("\\s+");
//			usersContracts.put(new User(split[0]), contractsPerName.get(split[1]));
//		}
	}
}


