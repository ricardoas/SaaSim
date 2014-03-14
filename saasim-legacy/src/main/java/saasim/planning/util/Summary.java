package saasim.planning.util;

/**
 * This class encapsulating the information about planning's properties, like the arrival rate, 
 * request service demand, and others.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br 
 */
public class Summary{
	
	double arrivalRate;
	double totalCpuHrs;
	double requestServiceDemandInMillis;
	double userThinkTimeInSeconds;
	long numberOfUsers;
	
	/**
	 * Default constructor.
	 * @param arrivalRate the arrival rate
	 * @param totalCpuHrs the total hours spent for cpu
	 * @param serviceDemandInMillis the request service demand in milliseconds
	 * @param userThinkTimeInSeconds the think time by user in seconds
	 * @param numberOfUsers the number of users
	 */
	public Summary(double arrivalRate, double totalCpuHrs,
			double serviceDemandInMillis, double userThinkTimeInSeconds, long numberOfUsers) {
		this.arrivalRate = arrivalRate;
		this.totalCpuHrs = totalCpuHrs;
		this.requestServiceDemandInMillis = serviceDemandInMillis;
		this.userThinkTimeInSeconds = userThinkTimeInSeconds;
		this.numberOfUsers = numberOfUsers;
	}

	/**
	 * Gets the arrival rate.
	 * @return The arrival rate.
	 */
	public double getArrivalRate() {
		return arrivalRate;
	}

	/**
	 * Sets the arrival rate.
	 * @param arrivalRate New arrival rate to set.
	 */
	public void setArrivalRate(double arrivalRate) {
		this.arrivalRate = arrivalRate;
	}

	/**
	 * Gets the total hours spent by the cpu.
	 * @return The total hours spent by the cpu.
	 */
	public double getTotalCpuHrs() {
		return totalCpuHrs;
	}

	/**
	 * Sets the total hours spent by the cpu.
	 * @param totalCpuHrs New total hours.
	 */
	public void setTotalCpuHrs(double totalCpuHrs) {
		this.totalCpuHrs = totalCpuHrs;
	}

	/**
	 * Gets the request service demand in milliseconds.
	 * @return The request service demand in milliseconds.
	 */
	public double getRequestServiceDemandInMillis() {
		return requestServiceDemandInMillis;
	}

	/**
	 * Sets the request service demand.
	 * @param requestServiceDemand New request service demand.
	 */
	public void setRequestServiceDemand(double requestServiceDemand) {
		this.requestServiceDemandInMillis = requestServiceDemand;
	}

	/**
	 * Gets the think time by the user in seconds.
	 * @return The think time by user.
	 */
	public double getUserThinkTimeInSeconds() {
		return userThinkTimeInSeconds;
	}

	/**
	 * Sets the think time by the user.
	 * @param userThinkTime New think time.
	 */
	public void setUserThinkTime(double userThinkTime) {
		this.userThinkTimeInSeconds = userThinkTime;
	}

	/**
	 * Gets the number of users in the application.
	 * @return The number of users in the application.
	 */
	public long getNumberOfUsers() {
		return numberOfUsers;
	}

	/**
	 * Sets the number of users in the application.
	 * @param numberOfUsers New number of users.
	 */
	public void setNumberOfUsers(long numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}
	
}