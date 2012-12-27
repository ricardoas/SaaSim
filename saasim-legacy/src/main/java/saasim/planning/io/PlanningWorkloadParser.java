package saasim.planning.io;

import static saasim.planning.io.PlanningWorkloadProperties.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import saasim.config.Validator;
import saasim.planning.util.Summary;


/**
 * This class represents a workload parser adapted for planning. 
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public class PlanningWorkloadParser extends PropertiesConfiguration{
	
	private List<Summary> summariesPerInterval;

	/**
	 * Default constructor.
	 * @param workloadPath the path workload's file
	 * @throws ConfigurationException 
	 */
	public PlanningWorkloadParser(String workloadPath) throws ConfigurationException{
		super(workloadPath);
		this.summariesPerInterval = new ArrayList<Summary>();
		checkProperties();
	}
	
	/**
	 * Check properties about the {@link PlanningWorkloadParser}'s properties.
	 * 
	 * @throws ConfigurationException
	 */
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

	/**
	 * Gets a statistics of workloads.
	 * @return A list containing {@link Summary}s of the workloads.  
	 */
	public List<Summary> getSummaries(){
		return this.summariesPerInterval;
	}

	/**
	 * Read data to adds in list of {@link Summary} of this {@link PlanningWorkloadParser}.
	 */
	public void readData() {
		String[] arrivalRates = getStringArray(ARRIVAL_RATE);
		String[] cpuDemandInHours = getStringArray(CPU_DEMAND_IN_HOURS);
		String[] requestServiceDemandInMillis = getStringArray(SERVICE_DEMAND);
		String[] userThinkTimeInSeconds = getStringArray(USER_THINK_TIME);
		String[] numberOfUsers = getStringArray(NUMBER_OF_USERS);
		for(int i = 0; i < arrivalRates.length; i++){
			this.summariesPerInterval.add(parseSummaryInterval(arrivalRates[i], cpuDemandInHours[i], 
				 requestServiceDemandInMillis[i], userThinkTimeInSeconds[i], numberOfUsers[i]));
		} 
	}

	/**
	 * Parse the information in a {@link Summary}.
	 * @param monthArrivalRate the arrival fee per month
	 * @param monthCpuDemandInHours the cpu demand per month in hours
	 * @param monthRequestServiceDemand the demand of request per month
	 * @param monthUserThinkTime the user think time per month
	 * @param monthNumberOfUsers the numbers of users per month
	 * @return A {@link Summary} encapsulating this information read from workload.
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
