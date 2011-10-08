package planning.io;

import static planning.io.PlanningWorkloadProperties.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import planning.util.Summary;

import commons.config.Validator;

public class PlanningWorkloadParser extends PropertiesConfiguration{
	
	private List<Summary> summariesPerInterval;

	public PlanningWorkloadParser(String workloadPath) throws ConfigurationException{
		super(workloadPath);
		this.summariesPerInterval = new ArrayList<Summary>();
		checkProperties();
	}
	
	private void checkProperties() throws ConfigurationException {
		String[] arrivalRates = getStringArray(ARRIVAL_RATE);
		if(arrivalRates != null && arrivalRates.length != 0){
			Validator.checkIsNonEmptyStringArray(ARRIVAL_RATE, arrivalRates);
		}else{
			throw new ConfigurationException("Missing arrival rates!");
		}
		
		String[] cpuDemands = getStringArray(CPU_DEMAND_IN_HOURS);
		if(cpuDemands != null && cpuDemands.length != 0){
			Validator.checkIsNonEmptyStringArray(CPU_DEMAND_IN_HOURS, cpuDemands);
		}else{
			throw new ConfigurationException("Missing cpu demands!");
		}
		
		String[] serviceDemands = getStringArray(SERVICE_DEMAND);
		if(serviceDemands != null && serviceDemands.length != 0){
			Validator.checkIsNonEmptyStringArray(SERVICE_DEMAND, serviceDemands);
		}else{
			throw new ConfigurationException("Missing service demands!");
		}

		String[] userThinkTimes = getStringArray(USER_THINK_TIME);
		if(userThinkTimes != null && userThinkTimes.length != 0){
			Validator.checkIsNonEmptyStringArray(USER_THINK_TIME, userThinkTimes);
		}else{
			throw new ConfigurationException("Missing users think times!");
		}
		
		String[] numberOfUsers = getStringArray(NUMBER_OF_USERS);
		if(numberOfUsers != null && numberOfUsers.length != 0){
			Validator.checkIsNonEmptyStringArray(NUMBER_OF_USERS, numberOfUsers);
		}else{
			throw new ConfigurationException("Missing number of users!");
		}
	}

	public List<Summary> getSummaries(){
		return this.summariesPerInterval;
	}

	/**
	 * @return
	 */
	public void readData() {
		String[] arrivalRates = getStringArray(ARRIVAL_RATE);
		String[] cpuDemandInHours = getStringArray(CPU_DEMAND_IN_HOURS);
		String[] requestServiceDemandInMillis = getStringArray(SERVICE_DEMAND);
		String[] userThinkTimeInSeconds = getStringArray(USER_THINK_TIME);
		String[] numberOfUsers = getStringArray(NUMBER_OF_USERS);
		for(int i = 0; i < arrivalRates.length; i++){
			this.summariesPerInterval.add(parseSummaryInterval(arrivalRates[i], cpuDemandInHours[i], requestServiceDemandInMillis[i],
							userThinkTimeInSeconds[i], numberOfUsers[i]));
		}
	}

	/**
	 * @param line
	 * @param numberOfUsers 
	 * @param userThinkTime2 
	 * @param requestServiceDemand2 
	 * @param cpuDemandInHours 
	 * @return
	 */
	protected Summary parseSummaryInterval(String monthArrivalRate, String monthCpuDemandInHours, String monthRequestServiceDemand, String monthUserThinkTime, String monthNumberOfUsers){
		double arrivalRate = Double.parseDouble(monthArrivalRate);
		double totalCpuHrs = Double.parseDouble(monthCpuDemandInHours);
		double serviceDemandInMillis = Double.parseDouble(monthRequestServiceDemand);
		double userThinkTimeInSeconds = Double.parseDouble(monthUserThinkTime);
		long numberOfUsers = Long.parseLong(monthNumberOfUsers);
		
		return new Summary(arrivalRate, totalCpuHrs, serviceDemandInMillis,
				userThinkTimeInSeconds, numberOfUsers);
		
	}
}
