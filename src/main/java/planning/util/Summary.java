package planning.util;

public class Summary{
	
	double arrivalRate;
	double totalCpuHrs;
	double requestServiceDemand;
	double userThinkTime;
	long numberOfUsers;
	
	public Summary(double arrivalRate, double totalCpuHrs,
			double serviceDemand, double userThinkTime, long numberOfUsers) {
		this.arrivalRate = arrivalRate;
		this.totalCpuHrs = totalCpuHrs;
		this.requestServiceDemand = serviceDemand;
		this.userThinkTime = userThinkTime;
		this.numberOfUsers = numberOfUsers;
	}

	public double getArrivalRate() {
		return arrivalRate;
	}

	public void setArrivalRate(double arrivalRate) {
		this.arrivalRate = arrivalRate;
	}

	public double getTotalCpuHrs() {
		return totalCpuHrs;
	}

	public void setTotalCpuHrs(double totalCpuHrs) {
		this.totalCpuHrs = totalCpuHrs;
	}

	public double getRequestServiceDemand() {
		return requestServiceDemand;
	}

	public void setRequestServiceDemand(double requestServiceDemand) {
		this.requestServiceDemand = requestServiceDemand;
	}

	public double getUserThinkTime() {
		return userThinkTime;
	}

	public void setUserThinkTime(double userThinkTime) {
		this.userThinkTime = userThinkTime;
	}

	public long getNumberOfUsers() {
		return numberOfUsers;
	}

	public void setNumberOfUsers(long numberOfUsers) {
		this.numberOfUsers = numberOfUsers;
	}
	
	
}