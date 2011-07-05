package config;

import commons.config.Configuration;


public class MainConfiguration extends Configuration{

	public static String WORKLOAD = "workload.file";
	public static String USERS = "users.file";
	public static String CONTRACTS = "contracts.file";
	public static String IAAS = "iaas.file";

	public static String HEURISTIC = "heuristic";
	public static String SLA = "sla.tr";
	public static String PLANNING_PERIOD = "planning.period";
	
	@Override
	public boolean verifyPropertiesExist(){
		return currentProperties.containsKey(WORKLOAD) && currentProperties.containsKey(HEURISTIC) &&
		//currentProperties.containsKey(USERS) && 
		currentProperties.containsKey(CONTRACTS) && 
		currentProperties.containsKey(SLA) && currentProperties.containsKey(IAAS) && currentProperties.containsKey(PLANNING_PERIOD) &&
		!getProperty(WORKLOAD).isEmpty() && !getProperty(HEURISTIC).isEmpty() &&
		//!getProperty(USERS).isEmpty() && 
		!getProperty(CONTRACTS).isEmpty() && 
		!getProperty(SLA).isEmpty() && !getProperty(IAAS).isEmpty() &&
		!getProperty(PLANNING_PERIOD).isEmpty();
	}
	
	public String getHeuristic() {
		return getProperty(HEURISTIC);
	}
	
	public String getWorkloadFile(){
		return getProperty(WORKLOAD);
	}
	
	public String getContractsFile(){
		return getProperty(CONTRACTS);
	}
	
	public String getIAASFile(){
		return getProperty(IAAS);
	}
	
	public double getSLA(){
		return Double.valueOf(getProperty(SLA));
	}
	
	public String getPlanningPeriod(){
		return getProperty(PLANNING_PERIOD);
	}
}
