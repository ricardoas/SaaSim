package config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import commons.cloud.Provider;

public class ProviderConfiguration extends Configuration{
	
	public static String NUM_OF_PROVIDERS = "providers.number";
	public static String PROVIDER = "prov";
	
	public static String NAME = ".name";
	public static String CPU_COST = ".cpu_cost";
	public static String ON_DEMAND_LIMIT = ".ondemandLimit";
	public static String RESERVATION_LIMIT = ".reservationLimit";
	public static String ONE_YEAR_FEE = ".oneYearFee";
	public static String THREE_YEARS_FEE = ".threeYearsFee";
	public static String MONITORING = ".monit";
	public static String TRANSFER_IN = ".transIn";
	public static String COST_TRANSFER_IN = ".costsTransIn";
	public static String TRANSFER_OUT = ".transOut";
	public static String COST_TRANSFER_OUT = ".costsTransOut";
	
	public Map<String, Provider> providers;//Map containing providers to be simulated
	
	@Override
	public void loadPropertiesFromFile( String file ) throws FileNotFoundException, IOException {
		super.loadPropertiesFromFile(file);
		buildProvider();
	}
	
	@Override
	public boolean verifyPropertiesExist(){
		boolean valid = true; 
		int numberOfPlans = Integer.valueOf(currentProperties.getProperty(NUM_OF_PROVIDERS));
		for(int i = 1; i <= numberOfPlans; i++){
			valid = valid && currentProperties.containsKey(PROVIDER+i+NAME) && currentProperties.containsKey(PROVIDER+i+CPU_COST) && currentProperties.containsKey(PROVIDER+i+ON_DEMAND_LIMIT) &&
			currentProperties.containsKey(PROVIDER+i+RESERVATION_LIMIT) && currentProperties.containsKey(PROVIDER+i+ONE_YEAR_FEE) && 
			currentProperties.containsKey(PROVIDER+i+THREE_YEARS_FEE) && currentProperties.containsKey(PROVIDER+i+MONITORING) && 
			currentProperties.containsKey(PROVIDER+i+TRANSFER_IN) && currentProperties.containsKey(PROVIDER+i+COST_TRANSFER_IN) && 
			currentProperties.containsKey(PROVIDER+i+TRANSFER_OUT) && currentProperties.containsKey(PROVIDER+i+COST_TRANSFER_OUT) &&
			!currentProperties.getProperty(PROVIDER+i+CPU_COST).isEmpty() && !currentProperties.getProperty(PROVIDER+i+NAME).isEmpty()
			&& !currentProperties.getProperty(PROVIDER+i+ON_DEMAND_LIMIT).isEmpty() && !currentProperties.getProperty(PROVIDER+i+RESERVATION_LIMIT).isEmpty() 
			&& !currentProperties.getProperty(PROVIDER+i+ONE_YEAR_FEE).isEmpty() &&	!currentProperties.getProperty(PROVIDER+i+THREE_YEARS_FEE).isEmpty() 
			&& !currentProperties.getProperty(PROVIDER+i+MONITORING).isEmpty() &&	!currentProperties.getProperty(PROVIDER+i+TRANSFER_IN).isEmpty()
			&& !currentProperties.getProperty(PROVIDER+i+COST_TRANSFER_IN).isEmpty() &&	!currentProperties.getProperty(PROVIDER+i+TRANSFER_OUT).isEmpty()
			&& !currentProperties.getProperty(PROVIDER+i+COST_TRANSFER_OUT).isEmpty(); 
		}
		return valid;
	}
	
	public void buildProvider() {
		int numberOfPlans = Integer.valueOf(currentProperties.getProperty(NUM_OF_PROVIDERS));
		providers = new HashMap<String, Provider>();
		for(int i = 1; i <= numberOfPlans; i++){
			providers.put(currentProperties.getProperty(PROVIDER+i+NAME), new Provider(currentProperties.getProperty(PROVIDER+i+NAME), Double.valueOf(currentProperties.getProperty(PROVIDER+i+CPU_COST)), Double.valueOf(currentProperties.getProperty(PROVIDER+i+ON_DEMAND_LIMIT)),
					Double.valueOf(currentProperties.getProperty(PROVIDER+i+RESERVATION_LIMIT)), Double.valueOf(currentProperties.getProperty(PROVIDER+i+ONE_YEAR_FEE)), 
					Double.valueOf(currentProperties.getProperty(PROVIDER+i+THREE_YEARS_FEE)), Double.valueOf(currentProperties.getProperty(PROVIDER+i+MONITORING)),
					currentProperties.getProperty(PROVIDER+i+TRANSFER_IN), currentProperties.getProperty(PROVIDER+i+COST_TRANSFER_IN), currentProperties.getProperty(PROVIDER+i+TRANSFER_OUT), 
					currentProperties.getProperty(PROVIDER+i+COST_TRANSFER_OUT)
			));
		}
	}
	
}
