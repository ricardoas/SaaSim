package planning.io;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import planning.util.Summary;
import static planning.io.PlanningWorkloadProperties.*;

public class PlanningWorkloadParser extends PropertiesConfiguration{
	
	private List<Summary> summariesPerMonth;

	public PlanningWorkloadParser(String workloadPath) throws ConfigurationException{
		super(workloadPath);
		this.summariesPerMonth = new ArrayList<Summary>();
	}
	
	public List<Summary> getSummaries(){
		return this.summariesPerMonth;
	}

	/**
	 * @return
	 */
	public void readData() {
		String[] arrivalRates = getStringArray(ARRIVAL_RATE);
		String[] cpuDemandInHours = getStringArray(CPU_DEMAND_IN_HOURS);
		String[] requestServiceDemand = getStringArray(SERVICE_DEMAND);
		String[] userThinkTime = getStringArray(USER_THINK_TIME);
		String[] numberOfUsers = getStringArray(NUMBER_OF_USERS);
		for(int i = 0; i < arrivalRates.length; i++){
			this.summariesPerMonth.add(parseMonthSummary(arrivalRates[i], cpuDemandInHours[i], requestServiceDemand[i],
							userThinkTime[i], numberOfUsers[i]));
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
	protected Summary parseMonthSummary(String monthArrivalRate, String monthCpuDemandInHours, String monthRequestServiceDemand, String monthUserThinkTime, String monthNumberOfUsers){
		double arrivalRate = Double.parseDouble(monthArrivalRate);
		double totalCpuHrs = Double.parseDouble(monthCpuDemandInHours);
		double serviceDemand = Double.parseDouble(monthRequestServiceDemand);
		double userThinkTime = Double.parseDouble(monthUserThinkTime);
		long numberOfUsers = Long.parseLong(monthNumberOfUsers);
		
		return new Summary(arrivalRate, totalCpuHrs, serviceDemand,
				userThinkTime, numberOfUsers);
		
	}
}
