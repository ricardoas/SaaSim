package commons.config;

import static commons.sim.util.IaaSProvidersProperties.*;
import static commons.sim.util.SaaSAppProperties.*;
import static commons.sim.util.SaaSPlanProperties.*;
import static commons.sim.util.SaaSUsersProperties.*;
import static commons.sim.util.SimulatorProperties.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.PropertiesConfiguration;

import planning.heuristic.AGHeuristic;
import provisioning.DynamicProvisioningSystem;
import provisioning.ProfitDrivenProvisioningSystem;
import provisioning.RanjanProvisioningSystem;

import commons.cloud.Contract;
import commons.cloud.MachineType;
import commons.cloud.MachineTypeValue;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.sim.schedulingheuristics.ProfitDrivenHeuristic;
import commons.sim.schedulingheuristics.RanjanHeuristic;
import commons.sim.schedulingheuristics.RoundRobinHeuristic;


/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class Configuration	extends PropertiesConfiguration{
	
	public static final String ARRAY_SEPARATOR = "\\|";

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
	
	public int[] getIntegerArray(String propertyName) {
		String[] stringArray = getStringArray(propertyName);
		int [] values = new int[stringArray.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = Integer.valueOf(stringArray[i]);
		}
		return values;
	}

	public long[] getLongArray(String propertyName) {
		return parseLongArray(getStringArray(propertyName));
	}

	public long[][] getLong2DArray(String propertyName) {
		String[] stringArray = getStringArray(propertyName);
		long [][] values = new long[stringArray.length][];
		for (int i = 0; i < values.length; i++) {
			values[i] = parseLongArray(stringArray[i].split(ARRAY_SEPARATOR));
		}
		return values;
	}

	public double[] getDoubleArray(String propertyName) {
		return parseDoubleArray(getStringArray(propertyName));
	}

	public double[][] getDouble2DArray(String propertyName) {
		String[] stringArray = getStringArray(propertyName);
		double [][] values = new double[stringArray.length][];
		for (int i = 0; i < values.length; i++) {
			values[i] = parseDoubleArray(stringArray[i].split(ARRAY_SEPARATOR));
		}
		return values;
	}

	private double[] parseDoubleArray(String[] stringValues) {
		double [] doubleValues = new double[stringValues.length];
		for (int j = 0; j < doubleValues.length; j++) {
			doubleValues[j] = Double.valueOf(stringValues[j]);
		}
		return doubleValues;
	}

	private long[] parseLongArray(String[] stringValues) {
		long [] doubleValues = new long[stringValues.length];
		for (int j = 0; j < doubleValues.length; j++) {
			doubleValues[j] = Long.valueOf(stringValues[j]);
		}
		return doubleValues;
	}

	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> T[] getEnumArray(String propertyName, Class<T> enumClass) {
		return parseEnum(getStringArray(propertyName), enumClass);
	}
	
	@SuppressWarnings("unchecked")
	private <T extends Enum<T>> T[] parseEnum(String[] stringValues, Class<T> enumClass) {
		T [] enumValues = (T[]) new Enum[stringValues.length];
		for (int j = 0; j < enumValues.length; j++) {
			enumValues[j] = Enum.valueOf(enumClass, stringValues[j]);
		}
		return enumValues;
	}

	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> T[][] getEnum2DArray(String propertyName, Class<T> enumClass) {
		String[] machines = getStringArray(propertyName);
		T[][] machineTypes = (T[][]) new Enum[machines.length][];
		for(int i = 0; i < machines.length; i++){
			machineTypes[i] = parseEnum(machines[i].split(ARRAY_SEPARATOR), enumClass);
		}
		return machineTypes;
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
		String heuristicName = getString(DPS_HEURISTIC);
		try {
			return Class.forName(heuristicName);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Problem loading " + heuristicName, e);
		}
	}

	/**
	 * This method is responsible for reading providers properties and creating the
	 * cloud providers to be used in simulation.
	 * @return
	 * @throws IOException
	 */
	public List<Provider> getProviders() {
		if(this.providers == null){
			this.readProviders();
		}
		
		return this.providers;
	}

	private void readProviders() {
		int numberOfProviders = getInt(IAAS_NUMBER_OF_PROVIDERS);
		MachineTypeValue[] allTypes = getEnumArray(IAAS_TYPES, MachineTypeValue.class);
		double[] power = getDoubleArray(IAAS_POWER);

		String[] names = getStringArray(IAAS_PROVIDER_NAME);
		int[] onDemandLimits = getIntegerArray(IAAS_PROVIDER_ONDEMAND_LIMIT);
		int[] reservedLimits = getIntegerArray(IAAS_PROVIDER_RESERVED_LIMIT);
		double[] monitoringCosts = getDoubleArray(IAAS_PROVIDER_MONITORING);
		long[][] transferInLimits = getLong2DArray(IAAS_PROVIDER_TRANSFER_IN);
		double[][] transferInCosts = getDouble2DArray(IAAS_PROVIDER_COST_TRANSFER_IN);
		long[][] transferOutLimits = getLong2DArray(IAAS_PROVIDER_TRANSFER_OUT);
		double[][] transferOutCosts = getDouble2DArray(IAAS_PROVIDER_COST_TRANSFER_OUT);
		
		MachineTypeValue[][] machinesType = getEnum2DArray(IAAS_PROVIDER_TYPES, MachineTypeValue.class);
		double[][] onDemandCpuCosts = getDouble2DArray(IAAS_PROVIDER_ONDEMAND_CPU_COST);
		double[][] reservedCpuCosts = getDouble2DArray(IAAS_PROVIDER_RESERVED_CPU_COST);
		double[][] reservationOneYearFees = getDouble2DArray(IAAS_PROVIDER_ONE_YEAR_FEE);
		double[][] reservationThreeYearsFees = getDouble2DArray(IAAS_PROVIDER_THREE_YEARS_FEE);

		providers = new ArrayList<Provider>();
		
		for(int i = 0; i < numberOfProviders; i++){
			
			List<MachineType> types = new ArrayList<MachineType>();
			for (int j = 0; j < machinesType[i].length; j++) {
				types.add(new MachineType(machinesType[i][j], onDemandCpuCosts[i][j], reservedCpuCosts[i][j], 
						reservationOneYearFees[i][j], reservationThreeYearsFees[i][j]));
			}
			providers.add(new Provider(names[i], onDemandLimits[i], reservedLimits[i],
							monitoringCosts[i], transferInLimits[i], transferInCosts[i], 
							transferOutLimits[i], transferOutCosts[i], types));
		}
	}

	public Class<?> getPlanningHeuristicClass(){
		String heuristicName = getString(PLANNING_HEURISTIC);
		try {
			return Class.forName(heuristicName);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Problem loading " + heuristicName, e);
		}
	}

	/**
	 * This method is responsible for reading contracts properties and creating the
	 * associations between contracts and users that requested the services of each contract
	 * @return A map containing each contract name and its characterization
	 * @throws IOException
	 */
	public List<User> getUsers() {
		if(this.users == null){
			this.readUsers();
		}
		
		return this.users;
	}

	private void readUsers() {
		
		int numberOfPlans = getInt(NUMBER_OF_PLANS);
		String[] planNames = getStringArray(PLAN_NAME);
		int[] planPriorities = getIntegerArray(PLAN_PRIORITY);
		double[] prices = getDoubleArray(PLAN_PRICE);
		double[] setupCosts = getDoubleArray(PLAN_SETUP);
		long[] cpuLimits = getLongArray(PLAN_CPU_LIMIT);
		double[] extraCpuCosts = getDoubleArray(PLAN_EXTRA_CPU_COST);
		long[][] planTransferLimits = getLong2DArray(PLAN_TRANSFER_LIMIT);
		double[][] planExtraTransferCost = getDouble2DArray(PLAN_EXTRA_TRANSFER_COST);
		
		Map<String, Contract> contractsPerName = new HashMap<String, Contract>();
		for(int i = 0; i < numberOfPlans; i++){
			contractsPerName.put(planNames[i], 
					new Contract(planNames[i], planPriorities[i], setupCosts[i], prices[i], 
							cpuLimits[i], extraCpuCosts[i], planTransferLimits[i], planExtraTransferCost[i]));
		}
		
		String[] plans = getStringArray(SAAS_USER_PLAN);
		users = new ArrayList<User>();
		for (int i = 0; i < plans.length; i++) {
			users.add(new User(contractsPerName.get(plans[i])));
		}
	}

	/**
	 * Private constructor.
	 * 
	 * @param propertiesFileName
	 * @throws ConfigurationException
	 */
	private Configuration(String propertiesFileName) throws ConfigurationException {
		super(propertiesFileName);
		verifyProperties();
	}
	
	private void verifyProperties() throws ConfigurationException{
		verifySimulatorProperties();
		verifySaaSAppProperties();
		verifySaaSUsersProperties();
		verifySaaSPlansProperties();
		verifyIaaSProvidersProperties();
	}
	

	// ************************************* SIMULATOR ************************************/
	
	private void verifySimulatorProperties() throws ConfigurationException {
		checkDPSHeuristic();
		checkPlanningHeuristic();
		Validator.checkPositive(getInt(PLANNING_PERIOD));
	}
	
	private void checkDPSHeuristic() throws ConfigurationException {
		
		String heuristicName = getString(DPS_HEURISTIC);
		Validator.checkNotEmpty(getString(DPS_HEURISTIC));
		
		String customHeuristicClass = getString(DPS_CUSTOM_HEURISTIC);
		try{
			DPSHeuristicValues value = DPSHeuristicValues.valueOf(heuristicName);
			switch (value) {
				case STATIC:
					heuristicName = DynamicProvisioningSystem.class.getCanonicalName();
					break;
				case RANJAN:
					heuristicName = RanjanProvisioningSystem.class.getCanonicalName();
					checkRanjanProperties();
					break;
				case PROFITDRIVEN:
					heuristicName = ProfitDrivenProvisioningSystem.class.getCanonicalName();
					break;
				case CUSTOM:
					heuristicName = Class.forName(customHeuristicClass).getCanonicalName();
					break;
				default:
					throw new ConfigurationException("Unsupported value: " + value + " for DPSHeuristicValues.");
			}
			setProperty(DPS_HEURISTIC, heuristicName);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException("Problem loading " + customHeuristicClass, e);
		}
	}

	private void checkPlanningHeuristic() throws ConfigurationException {
		String heuristicName = getString(PLANNING_HEURISTIC);
		Validator.checkNotEmpty(getString(PLANNING_HEURISTIC));
		
		PlanningHeuristicValues value = PlanningHeuristicValues.valueOf(heuristicName);
		switch (value) {
		case EVOLUTIONARY:
			heuristicName = AGHeuristic.class.getCanonicalName();
			break;
		default:
			throw new ConfigurationException("Unsupported value: " + value + " for PlanningHeuristicValues.");
		}
		setProperty(PLANNING_HEURISTIC, heuristicName);
	}


	private void checkRanjanProperties() {
		Validator.checkPositive(getInt(RANJAN_HEURISTIC_NUMBER_OF_TOKENS));
		Validator.checkNonNegative(getInt(RANJAN_HEURISTIC_BACKLOG_SIZE));
	}

	// ************************************* SaaS APP ************************************/
	
	private void verifySaaSAppProperties() {

		Validator.checkNotEmpty(getString(APPLICATION_FACTORY));
		Validator.checkPositive(getInt(APPLICATION_NUM_OF_TIERS));
		Validator.checkNonNegative(getLong(APPLICATION_SETUP_TIME));
		
		checkSize(APPLICATION_HEURISTIC, APPLICATION_NUM_OF_TIERS);
		checkSize(APPLICATION_INITIAL_SERVER_PER_TIER, APPLICATION_NUM_OF_TIERS);
		
		Validator.checkIsPositiveArray(getStringArray(APPLICATION_INITIAL_SERVER_PER_TIER));

//		checkSize(APPLICATION_MAX_SERVER_PER_TIER, APPLICATION_NUM_OF_TIERS);
//		Validator.checkIsPositiveIntegerArray(getStringArray(APPLICATION_MAX_SERVER_PER_TIER));
		
		checkSchedulingHeuristicNames();
		
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
					throw new ConfigurationRuntimeException("Unsupported value for " + APPLICATION_HEURISTIC + ": " + strings[i]);
			}
		}
		
		setProperty(APPLICATION_HEURISTIC, strings);
	}

	// ************************************* SaaS Users ************************************/
	
	private void verifySaaSUsersProperties() throws ConfigurationException {
		
		Validator.checkPositive(getInt(SAAS_NUMBER_OF_USERS));
		
		String workload = getString(SAAS_WORKLOAD);
		if(workload == null || workload.isEmpty()){
			checkSize(SAAS_USER_WORKLOAD, SAAS_NUMBER_OF_USERS);
			Validator.checkIsNonEmptyStringArray(getStringArray(SAAS_USER_WORKLOAD));
		}else{
			if(getStringArray(SAAS_USER_WORKLOAD).length != 0){
				throw new ConfigurationException("Cannot define user specific workload when " +
						"an unique workload has been already specified.");
			}
		}
	}
	
	private void checkSize(String propertyName, String sizePropertyName) {
		String[] values = getStringArray(propertyName);
		int size = getInt(sizePropertyName);
		if (values.length != size){
			throw new ConfigurationRuntimeException("Check number of values in " + 
					propertyName + ". It must be equals to what is specified at " + 
					sizePropertyName);
		}
	}

	
	
	
	// ******************************** IAAS PROVIDERS ************************************/
	
	private void verifyIaaSProvidersProperties() throws ConfigurationException {
		
		Validator.checkPositive(getInt(IAAS_NUMBER_OF_PROVIDERS));
		
		Validator.checkIsEnumArray(getStringArray(IAAS_TYPES), MachineTypeValue.class);
		Validator.checkIsPositiveDoubleArray(getStringArray(IAAS_POWER));
		
		if(getStringArray(IAAS_TYPES).length != getStringArray(IAAS_POWER).length){
			throw new ConfigurationException(IAAS_TYPES + " must have the same size of " + IAAS_POWER);
		}
		
		checkSize(IAAS_PROVIDER_NAME, IAAS_NUMBER_OF_PROVIDERS);
		checkSize(IAAS_PROVIDER_ONDEMAND_CPU_COST, IAAS_NUMBER_OF_PROVIDERS);
		checkSize(IAAS_PROVIDER_RESERVED_CPU_COST, IAAS_NUMBER_OF_PROVIDERS);
		checkSize(IAAS_PROVIDER_ONE_YEAR_FEE, IAAS_NUMBER_OF_PROVIDERS);
		checkSize(IAAS_PROVIDER_THREE_YEARS_FEE, IAAS_NUMBER_OF_PROVIDERS);
		checkSize(IAAS_PROVIDER_TRANSFER_IN, IAAS_NUMBER_OF_PROVIDERS);
		checkSize(IAAS_PROVIDER_COST_TRANSFER_IN, IAAS_NUMBER_OF_PROVIDERS);
		checkSize(IAAS_PROVIDER_TRANSFER_OUT, IAAS_NUMBER_OF_PROVIDERS);
		checkSize(IAAS_PROVIDER_COST_TRANSFER_OUT, IAAS_NUMBER_OF_PROVIDERS);
		checkSize(IAAS_PROVIDER_ONDEMAND_LIMIT, IAAS_NUMBER_OF_PROVIDERS);
		checkSize(IAAS_PROVIDER_RESERVED_LIMIT, IAAS_NUMBER_OF_PROVIDERS);
		checkSize(IAAS_PROVIDER_MONITORING, IAAS_NUMBER_OF_PROVIDERS);
		
		Validator.checkIsNonEmptyStringArray(getStringArray(IAAS_PROVIDER_NAME));
		Validator.checkIsNonNegativeDouble2DArray(getStringArray(IAAS_PROVIDER_ONDEMAND_CPU_COST), ARRAY_SEPARATOR);
		Validator.checkIsNonNegativeDouble2DArray(getStringArray(IAAS_PROVIDER_RESERVED_CPU_COST), ARRAY_SEPARATOR);
		Validator.checkIsNonNegativeDouble2DArray(getStringArray(IAAS_PROVIDER_ONE_YEAR_FEE), ARRAY_SEPARATOR);
		Validator.checkIsNonNegativeDouble2DArray(getStringArray(IAAS_PROVIDER_THREE_YEARS_FEE), ARRAY_SEPARATOR);
		Validator.checkIsNonNegative2DArray(getStringArray(IAAS_PROVIDER_TRANSFER_IN), ARRAY_SEPARATOR);
		Validator.checkIsNonNegativeDouble2DArray(getStringArray(IAAS_PROVIDER_COST_TRANSFER_IN), ARRAY_SEPARATOR);
		Validator.checkIsNonNegative2DArray(getStringArray(IAAS_PROVIDER_TRANSFER_OUT), ARRAY_SEPARATOR);
		Validator.checkIsNonNegativeDouble2DArray(getStringArray(IAAS_PROVIDER_COST_TRANSFER_OUT), ARRAY_SEPARATOR);
		Validator.checkIsNonNegativeArray(getStringArray(IAAS_PROVIDER_ONDEMAND_LIMIT));
		Validator.checkIsNonNegativeArray(getStringArray(IAAS_PROVIDER_RESERVED_LIMIT));
		Validator.checkIsNonNegativeDoubleArray(getStringArray(IAAS_PROVIDER_MONITORING));
	}
	
	
	
	// ************************************* SAAS ************************************/


	private void verifySaaSPlansProperties() {
		Validator.checkPositive(getInt(NUMBER_OF_PLANS));

		checkSize(PLAN_NAME, NUMBER_OF_PLANS);
		checkSize(PLAN_PRIORITY, NUMBER_OF_PLANS);
		checkSize(PLAN_PRICE, NUMBER_OF_PLANS);
		checkSize(PLAN_SETUP, NUMBER_OF_PLANS);
		checkSize(PLAN_CPU_LIMIT, NUMBER_OF_PLANS);
		checkSize(PLAN_EXTRA_CPU_COST, NUMBER_OF_PLANS);
		checkSize(PLAN_TRANSFER_LIMIT, NUMBER_OF_PLANS);
		checkSize(PLAN_EXTRA_TRANSFER_COST, NUMBER_OF_PLANS);
		
		Validator.checkIsNonNegativeDoubleArray(getStringArray(PLAN_PRICE));
		Validator.checkIsNonNegativeDoubleArray(getStringArray(PLAN_SETUP));
		Validator.checkIsNonNegativeDoubleArray(getStringArray(PLAN_EXTRA_CPU_COST));
		Validator.checkIsNonNegativeArray(getStringArray(PLAN_CPU_LIMIT));
		Validator.checkIsNonNegativeArray(getStringArray(PLAN_PRIORITY));
		Validator.checkIsNonNegativeDouble2DArray(getStringArray(PLAN_EXTRA_CPU_COST), ARRAY_SEPARATOR);
		Validator.checkIsNonNegative2DArray(getStringArray(PLAN_CPU_LIMIT), ARRAY_SEPARATOR);
	}

	public long getMaximumNumberOfThreadsPerMachine() {
		return getLong(RANJAN_HEURISTIC_NUMBER_OF_TOKENS, Long.MAX_VALUE);
	}

	public long getMaximumBacklogSize() {
		return getLong(RANJAN_HEURISTIC_BACKLOG_SIZE, Long.MAX_VALUE);
	}
	
}