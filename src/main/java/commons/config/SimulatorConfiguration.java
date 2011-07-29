package commons.config;

import static commons.sim.util.ProviderProperties.*;
import static commons.sim.util.SimulatorProperties.*;
import static commons.sim.util.ContractProperties.*;

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
		return getString(APPLICATION_FACTORY, 
				SimpleApplicationFactory.class.getCanonicalName());
	}
	
	/**
	 * 
	 * @return
	 */
	public int getApplicationNumOfTiers() {
		return Math.max(getInt(APPLICATION_NUM_OF_TIERS, 1), 1);
	}
	
	/**
	 * @return
	 */
	public Class<?>[] getApplicationHeuristics() {
		String[] strings = getStringArray(APPLICATION_HEURISTIC);
		String customHeuristic = getString(APPLICATION_CUSTOM_HEURISTIC);
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
					heuristicClasses[i] = RanjanHeuristic.class;
					break;
				case PROFITDRIVEN:
					//FIXME: This heuristic needs a SLA in its construction!
					heuristicClasses[i] = ProfitDrivenHeuristic.class;
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
		String[] stringArray = getStringArray(APPLICATION_INITIAL_SERVER_PER_TIER);
		if(getApplicationNumOfTiers() != stringArray.length){
			throw new ConfigurationRuntimeException("Check number of values in " + 
					APPLICATION_INITIAL_SERVER_PER_TIER + ". It must be equals to what is specified at" + 
					APPLICATION_NUM_OF_TIERS);
		}
		int [] serversPerTier = new int[stringArray.length];
		for (int i = 0; i < serversPerTier.length; i++) {
			serversPerTier[i] = Math.max(1, Integer.valueOf(stringArray[i]));
		}
		return serversPerTier;
	}

	public int[] getApplicationMaxServersPerTier() {
		String[] stringArray = getStringArray(APPLICATION_MAX_SERVER_PER_TIER);
		if(stringArray.length == 0){
			stringArray = new String[getApplicationNumOfTiers()];
			Arrays.fill(stringArray, "");
		}
		if(getApplicationNumOfTiers() != stringArray.length){
			throw new ConfigurationRuntimeException("Check number of values in " + 
					APPLICATION_INITIAL_SERVER_PER_TIER + ". It must be equals to what is specified at" + 
					APPLICATION_NUM_OF_TIERS);
		}
		int [] serversPerTier = new int[stringArray.length];
		for (int i = 0; i < serversPerTier.length; i++) {
			serversPerTier[i] = Math.max(1, Integer.valueOf(stringArray[i].isEmpty()? "1": stringArray[i]));
		}
		return serversPerTier;
	}

	public Class<?> getDPSHeuristicClass() {
		String heuristicName = getString(DPS_HEURISTIC);
		String customHeuristicClass = getString(DPS_CUSTOM_HEURISTIC);
		try{
			DPSHeuristicValues value = DPSHeuristicValues.valueOf(heuristicName);
			switch (value) {
				case STATIC:
					return StaticProvisioningSystem.class;
				case RANJAN:
					return RanjanProvisioningSystem.class;
				case PROFITDRIVEN:
					return ProfitDrivenProvisioningSystem.class;
				case CUSTOM:
					return Class.forName(customHeuristicClass);
			}
			
		} catch (IllegalArgumentException iae) {
			throw new ConfigurationRuntimeException("Unsupported value for " + 
						DPS_HEURISTIC + ": " + heuristicName, iae);
		} catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Problem loading " + customHeuristicClass, e);
		}
		return null;
		
	}

	public long getSetUpTime() {
		return Math.max(0, getLong(SETUP_TIME, 0));
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
		int numberOfPlans = getInt(NUM_OF_PROVIDERS);
		for(int i = 1; i <= numberOfPlans; i++){
			valid = valid && containsKey(PROVIDER+i+PROVIDER_NAME) && containsKey(PROVIDER+i+ONDEMAND_CPU_COST) && 
			containsKey(PROVIDER+i+ON_DEMAND_LIMIT) && containsKey(PROVIDER+i+RESERVED_CPU_COST) &&
			containsKey(PROVIDER+i+RESERVATION_LIMIT) && containsKey(PROVIDER+i+ONE_YEAR_FEE) && 
			containsKey(PROVIDER+i+THREE_YEARS_FEE) && containsKey(PROVIDER+i+MONITORING) && 
			containsKey(PROVIDER+i+TRANSFER_IN) && containsKey(PROVIDER+i+COST_TRANSFER_IN) && 
			containsKey(PROVIDER+i+TRANSFER_OUT) && containsKey(PROVIDER+i+COST_TRANSFER_OUT) &&
			!getString(PROVIDER+i+ONDEMAND_CPU_COST).isEmpty() && !getString(PROVIDER+i+PROVIDER_NAME).isEmpty() &&
			!getString(PROVIDER+i+RESERVED_CPU_COST).isEmpty()
			&& !getString(PROVIDER+i+ON_DEMAND_LIMIT).isEmpty() && !getString(PROVIDER+i+RESERVATION_LIMIT).isEmpty() 
			&& !getString(PROVIDER+i+ONE_YEAR_FEE).isEmpty() &&	!getString(PROVIDER+i+THREE_YEARS_FEE).isEmpty() 
			&& !getString(PROVIDER+i+MONITORING).isEmpty() &&	!getString(PROVIDER+i+TRANSFER_IN).isEmpty()
			&& !getString(PROVIDER+i+COST_TRANSFER_IN).isEmpty() &&	!getString(PROVIDER+i+TRANSFER_OUT).isEmpty()
			&& !getString(PROVIDER+i+COST_TRANSFER_OUT).isEmpty(); 
		}
		return valid;
	}
	
	private void buildProvider() {
		int numberOfPlans = Integer.valueOf(getInt(NUM_OF_PROVIDERS));
		providers = new HashMap<String, Provider>();
		for(int i = 1; i <= numberOfPlans; i++){
			providers.put(getString(PROVIDER+i+PROVIDER_NAME), 
					new Provider(getString(PROVIDER+i+PROVIDER_NAME), getDouble(PROVIDER+i+ONDEMAND_CPU_COST), getInt(PROVIDER+i+ON_DEMAND_LIMIT),
					getInt(PROVIDER+i+RESERVATION_LIMIT), getDouble(PROVIDER+i+RESERVED_CPU_COST),
					getDouble(PROVIDER+i+ONE_YEAR_FEE), getDouble(PROVIDER+i+THREE_YEARS_FEE), 
					getDouble(PROVIDER+i+MONITORING), getString(PROVIDER+i+TRANSFER_IN), getString(PROVIDER+i+COST_TRANSFER_IN), 
					getString(PROVIDER+i+TRANSFER_OUT), getString(PROVIDER+i+COST_TRANSFER_OUT))
			);
		}
	}
	
	/**
	 * This method is responsible for reading contracts properties and creating the
	 * associations between contracts and users that requested the services of each contract
	 * @return A map containing each contract name and its characterization
	 * @throws IOException
	 */
	public Map<User, Contract> getContractsPerUser() throws IOException{
		if(this.usersContracts == null){
			if(this.verifyContractPropertiesExist()){
				this.buildPlans();
			}else{
				throw new IOException("Missing data in contracts file!");
			}
		}
		
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
			if(this.verifyContractPropertiesExist()){
				this.buildPlans();
			}else{
				throw new IOException("Missing data in contracts file!");
			}
		}
		
		return this.contractsPerName;
	}
	
	private void buildPlans() {
		
		//Extract plans
		int numberOfPlans = getInt(PLANS_NUM);
		contractsPerName = new HashMap<String, Contract>();
		for(int i = 1; i <= numberOfPlans; i++){
			contractsPerName.put(getString(PLAN+i+PLAN_NAME), new Contract( getString(PLAN+i+PLAN_NAME),
			getDouble(PLAN+i+PLAN_SETUP), getDouble(PLAN+i+PLAN_PRICE),
			getDouble(PLAN+i+CPU_LIMIT), getDouble(PLAN+i+EXTRA_CPU_COST) ));
		}
		
		//Extract users associations
		String value = getString(ASSOCIATIONS);
		boolean valid = containsKey(ASSOCIATIONS) && !value.isEmpty();
		String[] usersPlans = value.split(";");
		valid = valid && usersPlans.length > 0;
		usersContracts = new HashMap<User, Contract>();
		for(String userPlan : usersPlans){
			String[] split = userPlan.split("\\s+");
			usersContracts.put(new User(split[0]), contractsPerName.get(split[1]));
		}
	}
	
	private boolean verifyContractPropertiesExist(){
		boolean valid = true; 
		int numberOfPlans = getInt(PLANS_NUM);
		for(int i = 1; i <= numberOfPlans; i++){
			valid = valid && containsKey(PLAN+i+PLAN_NAME) && containsKey(PLAN+i+PLAN_PRICE) &&
			containsKey(PLAN+i+PLAN_SETUP) && containsKey(PLAN+i+CPU_LIMIT) && 
			containsKey(PLAN+i+EXTRA_CPU_COST) && //currentProperties.containsKey(IAAS) && currentProperties.containsKey(PLANNING_PERIOD) &&
			!getString(PLAN+i+PLAN_NAME).isEmpty() && !getString(PLAN+i+PLAN_PRICE).isEmpty() &&
			!getString(PLAN+i+PLAN_SETUP).isEmpty() && !getString(PLAN+i+CPU_LIMIT).isEmpty() && 
			!getString(PLAN+i+EXTRA_CPU_COST).isEmpty(); //&& !currentProperties.getProperty(IAAS).isEmpty() &&
			//!currentProperties.getProperty(PLANNING_PERIOD).isEmpty();
		}
		return valid;
	}
}
