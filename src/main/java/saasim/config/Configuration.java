package saasim.config;

import static saasim.sim.util.IaaSPlanProperties.*;
import static saasim.sim.util.IaaSProvidersProperties.*;
import static saasim.sim.util.SaaSAppProperties.*;
import static saasim.sim.util.SaaSPlanProperties.*;
import static saasim.sim.util.SaaSUsersProperties.*;
import static saasim.sim.util.SimulatorProperties.*;
import static saasim.util.DataUnit.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.log4j.Logger;

import saasim.cloud.Contract;
import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.RunTimeSLAContract;
import saasim.cloud.TypeProvider;
import saasim.cloud.User;
import saasim.io.ParserIdiom;
import saasim.planning.heuristic.OverProvisionHeuristic;
import saasim.provisioning.util.DPSInfo;
import saasim.sim.AccountingSystem;
import saasim.sim.SimpleMultiTierApplication;
import saasim.sim.Simulator;
import saasim.sim.components.LoadBalancer;
import saasim.sim.components.TimeSharedMachine;
import saasim.sim.core.EventCheckpointer;
import saasim.sim.core.EventScheduler;
import saasim.sim.util.SimulatorFactory;
import saasim.sim.util.SimulatorProperties;
import saasim.util.DataUnit;
import saasim.util.SimulationInfo;
import saasim.util.TimeUnit;


/**
 * Simulator configuration loader.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Configuration extends ComplexPropertiesConfiguration{
	
	private static final String DEFAULT_PROPERTIES_FILE_NAME = "saasim.properties";

	static{
		try {
			String customPropertyFile = System.getProperty("saasim.configuration");
			if(customPropertyFile != null && !customPropertyFile.isEmpty()){
				buildInstance(customPropertyFile);
			}else{
				buildInstance(DEFAULT_PROPERTIES_FILE_NAME);
			}
		} catch (ConfigurationException e) {
			throw new ConfigurationRuntimeException(e);
		}
	}
	
	/**
	 * Unique instance.
	 */
	private static Configuration instance;
	
	private int[] priorities;
	
	private EventScheduler scheduler;
	private SimulationInfo simulationInfo;
	private Simulator application;
	private Provider[] providers;
	private Contract[] contracts;
	private User[] users;
	private AccountingSystem accountingSystem;
	private DPSInfo dpsInfo;

	
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
		Locale.setDefault(Locale.US);
		Logger.getLogger(EventCheckpointer.class).debug("CHKP LOAD-in");
		instance = new Configuration(propertiesFileName);

		if(EventCheckpointer.hasCheckpoint()){
			Object[] objects = EventCheckpointer.load();
			instance.scheduler = (EventScheduler) objects[0];
			instance.simulationInfo = (SimulationInfo) objects[1]; 
			instance.simulationInfo.addDay();
			instance.scheduler.reset(instance.simulationInfo.getCurrentDayInMillis(), instance.simulationInfo.getCurrentDayInMillis() + TimeUnit.DAY.getMillis());
			instance.providers = (Provider[]) objects[2];
			instance.contracts = (Contract[]) objects[3];
			instance.users = (User[]) objects[4];
			instance.accountingSystem = (AccountingSystem) objects[5];
			instance.priorities = (int []) objects[6];
			instance.dpsInfo = (DPSInfo) objects[7];
			instance.application = (Simulator) objects[8];
			EventCheckpointer.clear();
		}else{
			instance.simulationInfo = new SimulationInfo();
			instance.scheduler = new EventScheduler(TimeUnit.DAY.getMillis());
			instance.providers = Configuration.getInstance().readProviders();
			instance.contracts = Configuration.getInstance().readContracts();
			instance.users = Configuration.getInstance().readUsers();
			instance.accountingSystem = new AccountingSystem(instance.users, instance.providers);
			instance.priorities = new int[instance.users.length];
			for (int i = 0; i < instance.priorities.length; i++) {
				instance.priorities[i] = instance.users[i].getContract().getPriority(); //FIXME Ricardo: don't know if we need this anymore
			}
			instance.dpsInfo = new DPSInfo();
			instance.application = SimulatorFactory.buildSimulator(Configuration.getInstance().getScheduler());
		}
		
		instance.scheduler.registerHandlerClass(LoadBalancer.class).
					registerHandlerClass(SimpleMultiTierApplication.class).
					registerHandlerClass(TimeSharedMachine.class).
					registerHandlerClass(OverProvisionHeuristic.class);
		
		Logger.getLogger(EventCheckpointer.class).debug("CHKP LOAD-out " + instance.simulationInfo);
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
	 */
	public void save(){
		assert instance != null;
		
		EventCheckpointer.save(scheduler, simulationInfo, application, providers, contracts, users, accountingSystem, priorities, dpsInfo);
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
	public Provider[] readProviders() throws ConfigurationException{
		int numberOfProviders = getInt(IAAS_NUMBER_OF_PROVIDERS);
		
		String[] names = getStringArray(IAAS_PROVIDER_NAME);
		int[] onDemandLimits = getIntegerArray(IAAS_PROVIDER_ONDEMAND_LIMIT);
		int[] reservedLimits = getIntegerArray(IAAS_PROVIDER_RESERVED_LIMIT);
		double[] monitoringCosts = getDoubleArray(IAAS_PROVIDER_MONITORING);
		long[][] transferInLimits = DataUnit.convert(getLong2DArray(IAAS_PROVIDER_TRANSFER_IN), GB, B);
		double[][] transferInCosts = DataUnit.convert(getDouble2DArray(IAAS_PROVIDER_COST_TRANSFER_IN), B, GB);
		long[][] transferOutLimits = DataUnit.convert(getLong2DArray(IAAS_PROVIDER_TRANSFER_OUT), GB, B);
		double[][] transferOutCosts = DataUnit.convert(getDouble2DArray(IAAS_PROVIDER_COST_TRANSFER_OUT), B, GB);
		
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
		PlanningHeuristicValues value = PlanningHeuristicValues.valueOf(getNonEmptyString(PLANNING_HEURISTIC).toUpperCase());
		
		switch (value) {
		case OVERPROVISIONING:
			try {
				Validator.checkNotEmpty(PLANNING_TYPE, getString(PLANNING_TYPE));
			} catch (ConfigurationException e) {
				throw new ConfigurationRuntimeException(e);
			}
			//$FALL-THROUGH$
		default:
			return value.getClazz();
		}
	}

	/**
	 * This method is responsible for reading contracts properties and creating the
	 * associations between contracts and users that requested the services of each contract
	 * @return A map containing each contract name and its characterization
	 * @throws ConfigurationException 
	 * @throws IOException
	 */
	public User[] readUsers() throws ConfigurationException{
		int numberOfPlans = getInt(NUMBER_OF_PLANS);
		
		readContracts();
		
		Map<String, Contract> contractsPerName = new HashMap<String, Contract>();
		for(int i = 0; i < numberOfPlans; i++){
			contractsPerName.put(contracts[i].getName(),contracts[i]);
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

	private Contract[] readContracts() throws ConfigurationException {
		
		int numberOfPlans = getInt(NUMBER_OF_PLANS);
		
		Contract[] contracts = new Contract[numberOfPlans];
		
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

		for(int i = 0; i < numberOfPlans; i++){
			if(planTransferLimitsInBytes[i].length != transferCostsPerBytes[i].length - 1){
				throw new ConfigurationException("Check values of " + PLAN_TRANSFER_LIMIT + " and " + PLAN_EXTRA_TRANSFER_COST);
			}
			Contract plan = new RunTimeSLAContract(planNames[i], priorities[i], setupCosts[i], prices[i], 
					cpuLimitsInHours[i], extraCpuCostsPerHour[i], planTransferLimitsInBytes[i], transferCostsPerBytes[i],
					storageLimitsInBytes[i], storageCostsPerBytes[i]);
			contracts [i] = plan;
		}
		return contracts;
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
			case CUSTOM:
				heuristicName = Class.forName(customHeuristicClass)
						.getCanonicalName();
				break;
			case RANJAN:
			case RANJAN_HET:
				Validator.checkPositive(MACHINE_NUMBER_OF_TOKENS, getString(MACHINE_NUMBER_OF_TOKENS));
				Validator.checkNonNegative(MACHINE_BACKLOG_SIZE, getString(MACHINE_BACKLOG_SIZE));
				//$FALL-THROUGH$
			default:
				heuristicName = value.getClassName();
				break;
			}
			setProperty(DPS_HEURISTIC, heuristicName);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationException("Problem loading " + customHeuristicClass, e);
		}
	}

	// ************************************* SaaS APP ************************************/
	
	private void verifySaaSAppProperties() throws ConfigurationException {

		Validator.checkNotEmpty(APPLICATION_FACTORY, getString(APPLICATION_FACTORY));
		Validator.checkPositive(APPLICATION_NUM_OF_TIERS, getString(APPLICATION_NUM_OF_TIERS));
		Validator.checkNonNegative(APPLICATION_SETUP_TIME, getString(APPLICATION_SETUP_TIME));
		
		checkSize(APPLICATION_HEURISTIC, APPLICATION_NUM_OF_TIERS);
		checkSize(APPLICATION_INITIAL_SERVER_PER_TIER, APPLICATION_NUM_OF_TIERS);
		
		Validator.checkIsPositiveArray(APPLICATION_INITIAL_SERVER_PER_TIER, getStringArray(APPLICATION_INITIAL_SERVER_PER_TIER));

		String[] strings = getStringArray(APPLICATION_MAX_SERVER_PER_TIER);
		
		if(strings.length == 0){
			strings = new String[getInt(APPLICATION_NUM_OF_TIERS)];
			Arrays.fill(strings, "");
			setProperty(APPLICATION_MAX_SERVER_PER_TIER, strings);
		}
		
		checkSize(APPLICATION_MAX_SERVER_PER_TIER, APPLICATION_NUM_OF_TIERS);
		
		for (int i = 0; i < strings.length; i++) {
			if(strings[i].trim().isEmpty()){
				strings[i] = Integer.toString(Integer.MAX_VALUE); 
			}
		}
		
		setProperty(APPLICATION_MAX_SERVER_PER_TIER, strings);

		Validator.checkIsPositiveArray(APPLICATION_MAX_SERVER_PER_TIER, getStringArray(APPLICATION_MAX_SERVER_PER_TIER));
		
		checkSchedulingHeuristicNames();
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

		checkSize(SAAS_USER_STORAGE, SAAS_NUMBER_OF_USERS);
		checkSize(SAAS_USER_PLAN, SAAS_NUMBER_OF_USERS);
		
		Validator.checkIsNonEmptyStringArray(SAAS_USER_PLAN, getStringArray(SAAS_USER_PLAN));
		Validator.checkIsNonNegativeArray(SAAS_USER_STORAGE, getStringArray(SAAS_USER_STORAGE));
		
		checkSize(SAAS_USER_WORKLOAD, SAAS_NUMBER_OF_USERS);
		Validator.checkIsNonEmptyStringArray(SAAS_USER_WORKLOAD, getStringArray(SAAS_USER_WORKLOAD));
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
		
		if(getBoolean(SimulatorProperties.PLANNING_USE_ERROR, false)){
			int workloadSize = (int) Math.round(workloads.length * (1+getDouble(SimulatorProperties.PLANNING_ERROR, 0.0)));
			String[] workloadFilesWithErrors = Arrays.copyOf(workloads, workloadSize);
			for (int i = 0; i < workloadFilesWithErrors.length; i++) {
				if(workloadFilesWithErrors[i] == null){
					workloadFilesWithErrors[i] = workloads[(i%workloadSize) - 1];
				}
			}
			workloads = workloadFilesWithErrors;
		}
		return workloads;
	}

	public int[] getPriorities() {
		return priorities;
	}

	public void enableParserError() {
		setProperty(SimulatorProperties.PLANNING_USE_ERROR, true);
	}

	public Simulator getSimulator() {
		return this.application;
	}

	public SimulationInfo getSimulationInfo() {
		return this.simulationInfo;
	}

	public Provider[] getProviders() {
		return this.providers;
	}

	public User[] getUsers() {
		return this.users;
	}

	public EventScheduler getScheduler() {
		return this.scheduler;
	}

	public DPSInfo getProvisioningInfo() {
		return this.dpsInfo;
	}

	public AccountingSystem getAccountingSystem() {
		return this.accountingSystem;
	}

	public Contract[] getContracts() {
		return this.contracts;
	}
}