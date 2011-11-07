package commons.config;

import static commons.sim.util.IaaSPlanProperties.*;
import static commons.sim.util.IaaSProvidersProperties.*;
import static commons.sim.util.SaaSAppProperties.*;
import static commons.sim.util.SaaSPlanProperties.*;
import static commons.sim.util.SaaSUsersProperties.*;
import static commons.sim.util.SimulatorProperties.*;
import static commons.util.DataUnit.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import commons.cloud.Contract;
import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.TypeProvider;
import commons.cloud.User;
import commons.io.Checkpointer;
import commons.io.ParserIdiom;
import commons.util.DataUnit;
import commons.util.TimeUnit;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class Configuration extends ComplexPropertiesConfiguration{
	
	/**
	 * Unique instance.
	 */
	private static Configuration instance;
	
	private int[] priorities;
	
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

	/**
	 * Builds the single instance of this configuration.
	 * @param propertiesFileName
	 * @throws ConfigurationException
	 */
	public static void buildInstance(String propertiesFileName) throws ConfigurationException{
		instance = new Configuration(propertiesFileName);
		Checkpointer.loadData();
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
	
	public ParserIdiom getParserIdiom(){
		return parseEnum(getString(PARSER_IDIOM), ParserIdiom.class);
	}
	
	public TimeUnit getParserPageSize(){
		return parseEnum(getString(PARSER_PAGE_SIZE), TimeUnit.class);
	}

	/**
	 * This method is responsible for reading providers properties and creating the
	 * cloud providers to be used in simulation.
	 * @return
	 * @throws ConfigurationException 
	 * @throws IOException
	 */
	public Provider[] getProviders() throws ConfigurationException{
		int numberOfProviders = getInt(IAAS_NUMBER_OF_PROVIDERS);
		
		String[] names = getStringArray(IAAS_PROVIDER_NAME);
		int[] onDemandLimits = getIntegerArray(IAAS_PROVIDER_ONDEMAND_LIMIT);
		int[] reservedLimits = getIntegerArray(IAAS_PROVIDER_RESERVED_LIMIT);
		double[] monitoringCosts = getDoubleArray(IAAS_PROVIDER_MONITORING);
		long[][] transferInLimits = getLong2DArray(IAAS_PROVIDER_TRANSFER_IN);
		double[][] transferInCosts = getDouble2DArray(IAAS_PROVIDER_COST_TRANSFER_IN);
		long[][] transferOutLimits = getLong2DArray(IAAS_PROVIDER_TRANSFER_OUT);
		double[][] transferOutCosts = getDouble2DArray(IAAS_PROVIDER_COST_TRANSFER_OUT);
		
		MachineType[][] machinesType = getEnum2DArray(IAAS_PROVIDER_TYPES, MachineType.class);
		double[][] onDemandCpuCosts = getDouble2DArray(IAAS_PROVIDER_ONDEMAND_CPU_COST);
		double[][] reservedCpuCosts = getDouble2DArray(IAAS_PROVIDER_RESERVED_CPU_COST);
		double[][] reservationOneYearFees = getDouble2DArray(IAAS_PROVIDER_ONE_YEAR_FEE);
		double[][] reservationThreeYearsFees = getDouble2DArray(IAAS_PROVIDER_THREE_YEARS_FEE);
		
		List<String> providersWithPlan = Arrays.asList(getStringArray(IAAS_PLAN_PROVIDER_NAME));
		MachineType[][] machines = getEnum2DArray(IAAS_PLAN_PROVIDER_TYPES, MachineType.class);
		long[][] reservations = getLong2DArray(IAAS_PLAN_PROVIDER_RESERVATION);
		
		Provider[] providers = new Provider[numberOfProviders];
		
		for(int i = 0; i < numberOfProviders; i++){
			
			List<TypeProvider> types = new ArrayList<TypeProvider>();
			
			int providerIndex = providersWithPlan.indexOf(names[i]);
			List<MachineType> typeList = null;
			
			if(providerIndex != -1){
				typeList = Arrays.asList(machines[providerIndex]);
			}
			
			if(machinesType[i].length != onDemandCpuCosts[i].length){
				throw new ConfigurationException("Check values of " + IAAS_PROVIDER_TYPES + " and " + IAAS_PROVIDER_ONDEMAND_CPU_COST);
			}
		
			if(machinesType[i].length != reservedCpuCosts[i].length){
				throw new ConfigurationException("Check values of " + IAAS_PROVIDER_TYPES + " and " + IAAS_PROVIDER_RESERVED_CPU_COST);
			}
		
			if(machinesType[i].length != reservationOneYearFees[i].length){
				throw new ConfigurationException("Check values of " + IAAS_PROVIDER_TYPES + " and " + IAAS_PROVIDER_ONE_YEAR_FEE);
			}
		
			if(machinesType[i].length != reservationThreeYearsFees[i].length){
				throw new ConfigurationException("Check values of " + IAAS_PROVIDER_TYPES + " and " + IAAS_PROVIDER_THREE_YEARS_FEE);
			}
		
			for (int j = 0; j < machinesType[i].length; j++) {
				long reservation = 0;
				if(typeList != null){
					int index = typeList.indexOf(machinesType[i][j]);
					reservation = (index == -1)? 0: reservations[providerIndex][index];
				}
				types.add(new TypeProvider(i, machinesType[i][j], onDemandCpuCosts[i][j], reservedCpuCosts[i][j], 
						reservationOneYearFees[i][j], reservationThreeYearsFees[i][j], reservation));
			}
			
			if(transferInLimits[i].length != transferInCosts[i].length - 1){
				throw new ConfigurationException("Check values of " + IAAS_PROVIDER_TRANSFER_IN + " and " + IAAS_PROVIDER_COST_TRANSFER_IN);
			}
		
			if(transferOutLimits[i].length != transferOutCosts[i].length - 1){
				throw new ConfigurationException("Check values of " + IAAS_PROVIDER_TRANSFER_OUT + " and " + IAAS_PROVIDER_COST_TRANSFER_OUT);
			}
			
			providers[i] = new Provider(i, names[i], onDemandLimits[i],
							reservedLimits[i], monitoringCosts[i], transferInLimits[i], 
							transferInCosts[i], transferOutLimits[i], transferOutCosts[i], types);
		}
		
		return providers;
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
	 * @throws ConfigurationException 
	 * @throws IOException
	 */
	public User[] getUsers() throws ConfigurationException{
		int numberOfPlans = getInt(NUMBER_OF_PLANS);
		String[] planNames = getStringArray(PLAN_NAME);
		int[] priorities = getIntegerArray(PLAN_PRIORITY);
		double[] prices = getDoubleArray(PLAN_PRICE);
		double[] setupCosts = getDoubleArray(PLAN_SETUP);
		long[] cpuLimitsInHours = getLongArray(PLAN_CPU_LIMIT);
		double[] extraCpuCostsPerHour = getDoubleArray(PLAN_EXTRA_CPU_COST);
		long[][] planTransferLimitsInBytes = DataUnit.convert(getLong2DArray(PLAN_TRANSFER_LIMIT), MB, B);
		double[][] transferCostsPerBytes = DataUnit.convert(getDouble2DArray(PLAN_EXTRA_TRANSFER_COST), B, MB);
		long[] storageLimitsInBytes = DataUnit.convert(getLongArray(PLAN_STORAGE_LIMIT), MB, B);
		double[] storageCostsPerBytes = DataUnit.convert(getDoubleArray(PLAN_EXTRA_STORAGE_COST), B, MB);
		
		Map<String, Contract> contractsPerName = new HashMap<String, Contract>();
		for(int i = 0; i < numberOfPlans; i++){
			if(planTransferLimitsInBytes[i].length != transferCostsPerBytes[i].length - 1){
				throw new ConfigurationException("Check values of " + PLAN_TRANSFER_LIMIT + " and " + PLAN_EXTRA_TRANSFER_COST);
			}
			contractsPerName.put(planNames[i], 
					new Contract(planNames[i], priorities[i], setupCosts[i], prices[i], 
							cpuLimitsInHours[i], extraCpuCostsPerHour[i], planTransferLimitsInBytes[i], transferCostsPerBytes[i],
							storageLimitsInBytes[i], storageCostsPerBytes[i]));
		}
		
		int numberOfUsers = getInt(SAAS_NUMBER_OF_USERS);
		
		String[] plans = getStringArray(SAAS_USER_PLAN);
		long[] storageInBytes = DataUnit.convert(getLongArray(SAAS_USER_STORAGE), MB, B);
		
		User[] users = new User[numberOfUsers];
		for (int i = 0; i < numberOfUsers; i++) {
			if(!contractsPerName.containsKey(plans[i])){
				throw new ConfigurationException("Cannot find configuration for plan " + plans[i] + ". Check contracts file.");
			}
			
			users[i] = new User(i, contractsPerName.get(plans[i]), storageInBytes[i]);
		}
		return users;
	}

	private void verifyProperties() throws ConfigurationException{
		verifySimulatorProperties();
		verifySaaSAppProperties();
		verifySaaSUsersProperties();
		verifySaaSPlansProperties();
		verifyIaaSProvidersProperties();
		verifyIaaSPlanProperties();
	}
	
	// ************************************* SIMULATOR ************************************/
	
	private void verifySimulatorProperties() throws ConfigurationException {
		checkDPSHeuristic();
		checkPlanningHeuristic();
		
		String value = getString(PLANNING_RISK);
		if(value != null && value.length() > 0){
			Validator.checkNonNegativeDouble(PLANNING_RISK, value);
		}
		value = getString(PLANNING_ERROR);
		if(value != null && value.length() > 0){
			Validator.checkNonNegativeDouble(PLANNING_ERROR, value);
		}
		value = getString(PLANNING_INTERVAL_SIZE);
		if(value != null && value.length() > 0){
			Validator.checkPositive(PLANNING_INTERVAL_SIZE, value);
		}
		
		Validator.checkPositive(PLANNING_PERIOD, getString(PLANNING_PERIOD));
		Validator.checkEnum(PARSER_IDIOM, getString(PARSER_IDIOM), ParserIdiom.class);
		Validator.checkEnum(PARSER_PAGE_SIZE, getString(PARSER_PAGE_SIZE), TimeUnit.class);
	}
	
	private void checkDPSHeuristic() throws ConfigurationException {
		
		String heuristicName = getNonEmptyString(DPS_HEURISTIC);
		
		String customHeuristicClass = getString(DPS_CUSTOM_HEURISTIC);
		
		try{
			DPSHeuristicValues value = DPSHeuristicValues.valueOf(heuristicName.toUpperCase());
			switch (value) {
				// FIXME checkRanjanProperties();
				case CUSTOM:
					heuristicName = Class.forName(customHeuristicClass).getCanonicalName();
					break;
				default:
					heuristicName = value.getClassName();
					break;
			}
			setProperty(DPS_HEURISTIC, heuristicName);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException("Problem loading " + customHeuristicClass, e);
		}
	}

	private void checkPlanningHeuristic() throws ConfigurationException {
		String heuristicName = getNonEmptyString(PLANNING_HEURISTIC);
		PlanningHeuristicValues value = PlanningHeuristicValues.valueOf(heuristicName.toUpperCase());
		
		switch (value) {
			case OVERPROVISIONING:
				checkPlanningType();
			default:
				heuristicName = value.getClassName();
				break;
		}
		setProperty(PLANNING_HEURISTIC, heuristicName);
	}


	private void checkPlanningType() throws ConfigurationException {
		Validator.checkNotEmpty(PLANNING_TYPE, getString(PLANNING_TYPE));
	}

	private void checkRanjanProperties() throws ConfigurationException {
		Validator.checkPositive(RANJAN_HEURISTIC_NUMBER_OF_TOKENS, getString(RANJAN_HEURISTIC_NUMBER_OF_TOKENS));
		Validator.checkNonNegative(RANJAN_HEURISTIC_BACKLOG_SIZE, getString(RANJAN_HEURISTIC_BACKLOG_SIZE));
	}

	// ************************************* SaaS APP ************************************/
	
	private void verifySaaSAppProperties() throws ConfigurationException {

		Validator.checkNotEmpty(APPLICATION_FACTORY, getString(APPLICATION_FACTORY));
		Validator.checkPositive(APPLICATION_NUM_OF_TIERS, getString(APPLICATION_NUM_OF_TIERS));
		Validator.checkNonNegative(APPLICATION_SETUP_TIME, getString(APPLICATION_SETUP_TIME));
		
		checkSize(APPLICATION_HEURISTIC, APPLICATION_NUM_OF_TIERS);
		checkSize(APPLICATION_INITIAL_SERVER_PER_TIER, APPLICATION_NUM_OF_TIERS);
		
		Validator.checkIsPositiveArray(APPLICATION_INITIAL_SERVER_PER_TIER, getStringArray(APPLICATION_INITIAL_SERVER_PER_TIER));

		checkSize(APPLICATION_MAX_SERVER_PER_TIER, APPLICATION_NUM_OF_TIERS);
		String[] strings = getStringArray(APPLICATION_MAX_SERVER_PER_TIER);
		for (int i = 0; i < strings.length; i++) {
			if(strings[i].trim().isEmpty()){
				strings[i] = Integer.toString(Integer.MAX_VALUE); 
			}
		}
		setProperty(APPLICATION_MAX_SERVER_PER_TIER, strings);

		Validator.checkIsPositiveArray(APPLICATION_MAX_SERVER_PER_TIER, getStringArray(APPLICATION_MAX_SERVER_PER_TIER));
		
		checkSchedulingHeuristicNames();
	}
	
	/**
	 * Used for investigations ...
	 */
	private void changeNumberOfStartUpServers(long[][] reservations) {
		long totalReserved = 0;
		for(int providerID = 0; providerID < reservations.length; providerID++){
			for(long reserved : reservations[providerID]){
				totalReserved += reserved;
			}
		}
		
//		if(totalReserved > 1){
//			setProperty(APPLICATION_INITIAL_SERVER_PER_TIER, totalReserved);
//		}else{
			if(getInt(APPLICATION_INITIAL_SERVER_PER_TIER) <= 0){
				setProperty(APPLICATION_INITIAL_SERVER_PER_TIER, 1);
			}
//		}
		
	}
	
	private void checkSchedulingHeuristicNames() throws ConfigurationException {
		String[] strings = getNonEmptyStringArray(APPLICATION_HEURISTIC);
		String customHeuristic = getString(APPLICATION_CUSTOM_HEURISTIC);
		for (int i = 0; i < strings.length; i++) {
			AppHeuristicValues value = AppHeuristicValues.valueOf(strings[i]);
			switch (value) {
				case CUSTOM:
					try {
						strings[i] = Class.forName(customHeuristic).getCanonicalName();
					} catch (ClassNotFoundException e) {
						throw new ConfigurationException("Problem loading " + customHeuristic, e);
					}
					break;
				default:
					strings[i] = value.getClassName();
					break;
			}
		}
		
		setProperty(APPLICATION_HEURISTIC, strings);
	}

	// ************************************* SaaS Users ************************************/
	
	private void verifySaaSUsersProperties() throws ConfigurationException {
		
		Validator.checkPositive(SAAS_NUMBER_OF_USERS, getString(SAAS_NUMBER_OF_USERS));

//		String[] peakPeriod = getStringArray(SAAS_PEAK_PERIOD);
//		if(peakPeriod != null && peakPeriod.length != 0){
//			Validator.checkIsPositiveArray(SAAS_PEAK_PERIOD, peakPeriod);
//		}
//		Validator.checkPositive(SAAS_USER_STORAGE, getString(SAAS_USER_STORAGE));
		
//		checkSize(SAAS_USER_ID, SAAS_NUMBER_OF_USERS);
		checkSize(SAAS_USER_STORAGE, SAAS_NUMBER_OF_USERS);
		checkSize(SAAS_USER_PLAN, SAAS_NUMBER_OF_USERS);
		
//		Validator.checkIsNonEmptyStringArray(SAAS_USER_ID, getStringArray(SAAS_USER_ID));
		Validator.checkIsNonEmptyStringArray(SAAS_USER_PLAN, getStringArray(SAAS_USER_PLAN));
		Validator.checkIsNonNegativeArray(SAAS_USER_STORAGE, getStringArray(SAAS_USER_STORAGE));
		
//		Validator.checkNotEmpty(SAAS_WORKLOAD_NORMAL, getString(SAAS_WORKLOAD_NORMAL));
//		Validator.checkNotEmpty(SAAS_WORKLOAD_TRANSITION, getString(SAAS_WORKLOAD_TRANSITION));
//		Validator.checkNotEmpty(SAAS_WORKLOAD_PEAK, getString(SAAS_WORKLOAD_PEAK));
		String workload = getString(SAAS_WORKLOAD);
		if(workload == null || workload.isEmpty()){
			checkSize(SAAS_USER_WORKLOAD, SAAS_NUMBER_OF_USERS);
			
			Validator.checkIsNonEmptyStringArray(SAAS_USER_WORKLOAD, getStringArray(SAAS_USER_WORKLOAD));
		}else{
			if(getStringArray(SAAS_USER_WORKLOAD).length != 0){
				throw new ConfigurationException("Cannot define user specific workload when " +
						"an unique workload has been already specified.");
			}
		}
	}
	
	// ******************************** IAAS PROVIDERS ************************************/
	
	private void verifyIaaSProvidersProperties() throws ConfigurationException {
		
		Validator.checkPositive(IAAS_NUMBER_OF_PROVIDERS, getString(IAAS_NUMBER_OF_PROVIDERS));
		
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
		checkSize(IAAS_PROVIDER_TYPES, IAAS_NUMBER_OF_PROVIDERS);
		
		Validator.checkIsNonEmptyStringArray(IAAS_PROVIDER_NAME, getStringArray(IAAS_PROVIDER_NAME));
		Validator.checkIsNonNegativeDouble2DArray(IAAS_PROVIDER_ONDEMAND_CPU_COST, getStringArray(IAAS_PROVIDER_ONDEMAND_CPU_COST), ARRAY_SEPARATOR);
		Validator.checkIsNonNegativeDouble2DArray(IAAS_PROVIDER_RESERVED_CPU_COST, getStringArray(IAAS_PROVIDER_RESERVED_CPU_COST), ARRAY_SEPARATOR);
		Validator.checkIsNonNegativeDouble2DArray(IAAS_PROVIDER_ONE_YEAR_FEE, getStringArray(IAAS_PROVIDER_ONE_YEAR_FEE), ARRAY_SEPARATOR);
		Validator.checkIsNonNegativeDouble2DArray(IAAS_PROVIDER_THREE_YEARS_FEE, getStringArray(IAAS_PROVIDER_THREE_YEARS_FEE), ARRAY_SEPARATOR);
		Validator.checkIsNonNegative2DArray(IAAS_PROVIDER_TRANSFER_IN, getStringArray(IAAS_PROVIDER_TRANSFER_IN), ARRAY_SEPARATOR);
		Validator.checkIsNonNegativeDouble2DArray(IAAS_PROVIDER_COST_TRANSFER_IN, getStringArray(IAAS_PROVIDER_COST_TRANSFER_IN), ARRAY_SEPARATOR);
		Validator.checkIsNonNegative2DArray(IAAS_PROVIDER_TRANSFER_OUT, getStringArray(IAAS_PROVIDER_TRANSFER_OUT), ARRAY_SEPARATOR);
		Validator.checkIsNonNegativeDouble2DArray(IAAS_PROVIDER_COST_TRANSFER_OUT, getStringArray(IAAS_PROVIDER_COST_TRANSFER_OUT), ARRAY_SEPARATOR);
		Validator.checkIsNonNegativeArray(IAAS_PROVIDER_ONDEMAND_LIMIT, getStringArray(IAAS_PROVIDER_ONDEMAND_LIMIT));
		Validator.checkIsNonNegativeArray(IAAS_PROVIDER_RESERVED_LIMIT, getStringArray(IAAS_PROVIDER_RESERVED_LIMIT));
		Validator.checkIsNonNegativeDoubleArray(IAAS_PROVIDER_MONITORING, getStringArray(IAAS_PROVIDER_MONITORING));
		Validator.checkIsEnum2DArray(IAAS_PROVIDER_TYPES, getStringArray(IAAS_PROVIDER_TYPES), MachineType.class, ARRAY_SEPARATOR);
	}
	
	
	
	// ************************************* SAAS ************************************/


	private void verifySaaSPlansProperties() throws ConfigurationException {
		Validator.checkPositive(NUMBER_OF_PLANS, getString(NUMBER_OF_PLANS));

		checkSize(PLAN_NAME, NUMBER_OF_PLANS);
		checkSize(PLAN_PRIORITY, NUMBER_OF_PLANS);
		checkSize(PLAN_PRICE, NUMBER_OF_PLANS);
		checkSize(PLAN_SETUP, NUMBER_OF_PLANS);
		checkSize(PLAN_CPU_LIMIT, NUMBER_OF_PLANS);
		checkSize(PLAN_EXTRA_CPU_COST, NUMBER_OF_PLANS);
		checkSize(PLAN_TRANSFER_LIMIT, NUMBER_OF_PLANS);
		checkSize(PLAN_EXTRA_TRANSFER_COST, NUMBER_OF_PLANS);
		checkSize(PLAN_STORAGE_LIMIT, NUMBER_OF_PLANS);
		checkSize(PLAN_EXTRA_STORAGE_COST, NUMBER_OF_PLANS);
		
		Validator.checkIsNonNegativeDoubleArray(PLAN_PRICE, getStringArray(PLAN_PRICE));
		Validator.checkIsNonNegativeDoubleArray(PLAN_SETUP, getStringArray(PLAN_SETUP));
		Validator.checkIsNonNegativeDoubleArray(PLAN_EXTRA_CPU_COST, getStringArray(PLAN_EXTRA_CPU_COST));
		Validator.checkIsNonNegativeArray(PLAN_CPU_LIMIT, getStringArray(PLAN_CPU_LIMIT));
		Validator.checkIsNonNegativeArray(PLAN_PRIORITY, getStringArray(PLAN_PRIORITY));
		Validator.checkIsNonNegativeDouble2DArray(PLAN_EXTRA_CPU_COST, getStringArray(PLAN_EXTRA_CPU_COST), ARRAY_SEPARATOR);
		Validator.checkIsNonNegative2DArray(PLAN_CPU_LIMIT, getStringArray(PLAN_CPU_LIMIT), ARRAY_SEPARATOR);
	}
	
	// ******************************** IAAS PLAN ************************************/
	
	private void verifyIaaSPlanProperties() throws ConfigurationException {
		String[] providersWithPlan = getStringArray(IAAS_PLAN_PROVIDER_NAME);
		String[][] machines = getString2DArray(IAAS_PLAN_PROVIDER_TYPES);
		long[][] reservation = getLong2DArray(IAAS_PLAN_PROVIDER_RESERVATION);
		
		if(providersWithPlan.length != machines.length){
			throw new ConfigurationException("Number of values in " + IAAS_PLAN_PROVIDER_NAME + " must be the same of " + IAAS_PLAN_PROVIDER_TYPES);
		}
		
		if(providersWithPlan.length != reservation.length){
			throw new ConfigurationException("Number of values in " + IAAS_PLAN_PROVIDER_NAME + " must be the same of " + IAAS_PLAN_PROVIDER_RESERVATION);
		}
		
		for (int i = 0; i < machines.length; i++) {
			if(machines[i].length != reservation[i].length){
				throw new ConfigurationException("Number of values in " + IAAS_PLAN_PROVIDER_TYPES + " must be the same of " + IAAS_PLAN_PROVIDER_RESERVATION);
			}
		}
		
		HashSet<String> set = new HashSet<String>(Arrays.asList(getStringArray(IAAS_PROVIDER_NAME)));
		for (String provider : providersWithPlan) {
			if(!set.contains(provider)){
				throw new ConfigurationException("Provider " + provider + " need to be defined at providers configuration file.");
			}
		}
		
	}

	public String[] getWorkloads() {
		String[] workloads = getStringArray(SAAS_USER_WORKLOAD);
		if(workloads != null && workloads.length != 0){
			return workloads;
		}
		return new String[]{getString(SAAS_WORKLOAD)};
	}

	public int[] getPriority() {
		return priorities;
	}
}