package saasim.sim.provisioningheuristics;

import java.io.Serializable;

import saasim.util.TimeUnit;


/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class MachineStatistics implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5595741138385801795L;
	public double averageUtilisation;
	public int totalNumberOfServers;
	public int warmingDownMachines;
	
	public double arrivalRate;
	
	public int tier;
	
	public long observationPeriod;
	public long totalBusyTime;
	
	public long requestArrivals;
	public long requestArrivalsPerTier;
	
	public long requestsArrivalInLastInterval;
	public long requestsArrivalsInLastIntervalPerTier;
	
	public long requestCompletions;
	public long requestsCompletionsPerTier;
	
	private long lastArrivalTime;
	private double averageIAT;
	private double SSD_IAT;
	
	public double averageST;
	private double SSD_ST;
	public double peakArrivalRate;
	
	
	

	/**
	 * @param averageUtilization
	 * @param totalRequestsArrivals
	 * @param totalRequestsCompletions
	 * @param totalNumberOfServers
	 */
	public MachineStatistics(double averageUtilization, long totalRequestsArrivals, long totalRequestsCompletions, int totalNumberOfServers) {
		this.averageUtilisation = averageUtilization;
		this.requestArrivals = totalRequestsArrivals;
		this.requestCompletions = totalRequestsCompletions;
		this.totalNumberOfServers = totalNumberOfServers;
		this.lastArrivalTime = 0;
	}

	public MachineStatistics() {
		
	}
	
	public MachineStatistics(MachineStatistics statistics) {
		this.averageUtilisation = statistics.averageUtilisation;
		this.totalNumberOfServers = statistics.totalNumberOfServers;
		this.warmingDownMachines = statistics.warmingDownMachines;
		this.arrivalRate = statistics.arrivalRate;
		this.tier = statistics.tier;
		this.observationPeriod = statistics.observationPeriod;
		this.totalBusyTime = statistics.totalBusyTime;
		this.requestArrivals = statistics.requestArrivals;
		this.requestArrivalsPerTier = statistics.requestArrivalsPerTier;
		this.requestCompletions = statistics.requestCompletions;
		this.requestsCompletionsPerTier = statistics.requestsCompletionsPerTier;
		this.lastArrivalTime = statistics.lastArrivalTime;
		this.averageIAT = statistics.averageIAT;
		this.averageST = statistics.averageST;
		
		// Not reusable statistics
		this.requestsArrivalInLastInterval = 0;
		this.requestsArrivalsInLastIntervalPerTier = 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ (int) (requestArrivals ^ (requestArrivals >>> 32));
		result = prime
				* result
				+ (int) (requestCompletions ^ (requestCompletions >>> 32));
		result = prime * result
				+ (int) (totalNumberOfServers ^ (totalNumberOfServers >>> 32));
		long temp;
		temp = Double.doubleToLongBits(averageUtilisation);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MachineStatistics other = (MachineStatistics) obj;
		if (requestArrivals != other.requestArrivals)
			return false;
		if (requestCompletions != other.requestCompletions)
			return false;
		if (totalNumberOfServers != other.totalNumberOfServers)
			return false;
		if (Double.doubleToLongBits(averageUtilisation) != Double
				.doubleToLongBits(other.averageUtilisation))
			return false;
		return true;
	}

	/**
	 * Knuth's algorithm
	 * @param arrivalTimeInMillis
	 */
	public void updateInterarrivalTime(long arrivalTimeInMillis) {
		
		requestArrivalsPerTier++;
		requestsArrivalsInLastIntervalPerTier++;
		
		double iat = 1.0*(arrivalTimeInMillis - (lastArrivalTime==0?arrivalTimeInMillis:lastArrivalTime))/TimeUnit.SECOND.getMillis();
		lastArrivalTime = arrivalTimeInMillis;
		
		
		double delta = iat - averageIAT;
		averageIAT += delta/requestArrivalsPerTier;
		SSD_IAT += delta*(iat - averageIAT);
	}
	
	@Override
	public String toString() {
		return "MachineStatistics [U= " + averageUtilisation
				+ " , N= " + totalNumberOfServers
				+ " , N_warmingDown= " + warmingDownMachines
				+ " , A_0= " + arrivalRate + " , tier= " + tier
				+ " , T= " + observationPeriod
				+ " , B= " + totalBusyTime
				+ " , A_0= "
				+ requestArrivals
				+ " , A_i= "
				+ requestArrivalsPerTier
				+ " , C_0= "
				+ requestCompletions
				+ " , C_i= "
				+ requestsCompletionsPerTier
				+ " , averageIAT= "
				+ averageIAT + " , var_IAT= " + calcVarIAT() + " , averageST= "
				+ averageST + " , var_ST= " + calcVarST() + " ]";
	}

	/**
	 * Knuth's algorithm
	 * @param serviceTimeInMillis
	 */
	public void updateServiceTime(long serviceTimeInMillis) {
		double serviceTime = (1.0*serviceTimeInMillis) / TimeUnit.SECOND.getMillis();

		requestsCompletionsPerTier++;
		
		double delta = serviceTime - averageST;
		averageST += delta/requestsCompletionsPerTier;
		SSD_ST += delta*(serviceTime - averageST);
	}

	public double calcVarST() {
		return SSD_ST/requestsCompletionsPerTier;
	}

	public double calcVarIAT() {
		return SSD_IAT/requestArrivalsPerTier;
	}
	
	public double getArrivalRate(long timeIntervalInSeconds){
		return 1.0*requestArrivals/timeIntervalInSeconds;
	}

	public double getArrivalRateInTier(long timeIntervalInSeconds){
		return 1.0*requestArrivalsPerTier/timeIntervalInSeconds;
	}

	public double getArrivalRateInLastIntervalInTier(long timeIntervalInSeconds){
		return 1.0*requestsArrivalsInLastIntervalPerTier/timeIntervalInSeconds;
	}

	public void resetPerTickStatistics() {
		// TODO Auto-generated method stub
		
	}

	public double getPeakArrivalRateInTier() {
		return peakArrivalRate;
	}
}
