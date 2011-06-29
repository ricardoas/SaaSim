package config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import cloud.Contract;
import cloud.User;

public class ContractConfiguration extends Configuration{
	
	public static String PLANS_NUM = "plans.number";
	public static String ASSOCIATIONS = "associations";
	
	public static String PLAN = "plan";
	public static String PLAN_NAME = ".name";
	public static String PLAN_PRICE = ".price";
	public static String PLAN_SETUP = ".setup";
	public static String CPU_LIMIT = ".cpu_lim";
	public static String EXTRA_CPU_COST = ".ex_cpu";
	public static String TRANSFER_LIMIT = ".transfer_lim";
	public static String EXTRA_TRANSFER_COST = ".ex_transfer";
	
	//Users SaaS plans
	public HashMap<String, Contract> contracts;
	public HashMap<User, Contract> usersContracts;
	
	@Override
	public void loadPropertiesFromFile( String file ) throws FileNotFoundException, IOException {
		super.loadPropertiesFromFile(file);
		buildPlans();
	}
	
	@Override
	public boolean verifyPropertiesExist(){
		boolean valid = true; 
		int numberOfPlans = Integer.valueOf(currentProperties.getProperty(PLANS_NUM));
		for(int i = 1; i <= numberOfPlans; i++){
			valid = valid && currentProperties.containsKey(PLAN+i+PLAN_NAME) && currentProperties.containsKey(PLAN+i+PLAN_PRICE) &&
			currentProperties.containsKey(PLAN+i+PLAN_SETUP) && currentProperties.containsKey(PLAN+i+CPU_LIMIT) && 
			currentProperties.containsKey(PLAN+i+EXTRA_CPU_COST) && //currentProperties.containsKey(IAAS) && currentProperties.containsKey(PLANNING_PERIOD) &&
			!currentProperties.getProperty(PLAN+i+PLAN_NAME).isEmpty() && !currentProperties.getProperty(PLAN+i+PLAN_PRICE).isEmpty() &&
			!currentProperties.getProperty(PLAN+i+PLAN_SETUP).isEmpty() && !currentProperties.getProperty(PLAN+i+CPU_LIMIT).isEmpty() && 
			!currentProperties.getProperty(PLAN+i+EXTRA_CPU_COST).isEmpty(); //&& !currentProperties.getProperty(IAAS).isEmpty() &&
			//!currentProperties.getProperty(PLANNING_PERIOD).isEmpty();
		}
		return valid;
	}

	private void buildPlans() {
		//Extract plans
		int numberOfPlans = Integer.valueOf(currentProperties.getProperty(PLANS_NUM));
		contracts = new HashMap<String, Contract>();
		for(int i = 1; i <= numberOfPlans; i++){
			contracts.put(currentProperties.getProperty(PLAN+i+PLAN_NAME), new Contract( currentProperties.getProperty(PLAN+i+PLAN_NAME),
			Double.valueOf(currentProperties.getProperty(PLAN+i+PLAN_SETUP)),
			Double.valueOf(currentProperties.getProperty(PLAN+i+PLAN_PRICE)),
			Double.valueOf(currentProperties.getProperty(PLAN+i+CPU_LIMIT)),
			Double.valueOf(currentProperties.getProperty(PLAN+i+EXTRA_CPU_COST)) ));
		}
		
		//Extract users associations
		String value = currentProperties.getProperty(ASSOCIATIONS);
		boolean valid = currentProperties.containsKey(ASSOCIATIONS) && !value.isEmpty();
		String[] usersPlans = value.split(";");
		valid = valid && usersPlans.length > 0;
		usersContracts = new HashMap<User, Contract>();
		for(String userPlan : usersPlans){
			String[] split = userPlan.split("\\s+");
			usersContracts.put(new User(split[0]), contracts.get(split[1]));
		}
	}
}
