package commons.sim.provisioningheuristics;

import java.io.Serializable;

import commons.util.TimeUnit;

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
	
	public long numberOfRequestsArrivalInLastInterval;
	public long numberOfRequestsArrivalInLastIntervalInTier;
	
	public long numberOfRequestsCompletionsInLastInterval;
	public long numberOfRequestsCompletionInLastIntervalInTier;
	
	private long lastArrivalTime;
	private double averageIAT;
	private double SSD_IAT;
	
	public double averageST;
	private double SSD_ST;
	
	
	

	/**
	 * @param averageUtilization
	 * @param totalRequestsArrivals
	 * @param totalRequestsCompletions
	 * @param totalNumberOfServers
	 */
	public MachineStatistics(double averageUtilization, long totalRequestsArrivals, long totalRequestsCompletions, int totalNumberOfServers) {
		this.averageUtilisation = averageUtilization;
		this.numberOfRequestsArrivalInLastInterval = totalRequestsArrivals;
		this.numberOfRequestsCompletionsInLastInterval = totalRequestsCompletions;
		this.totalNumberOfServers = totalNumberOfServers;
		this.lastArrivalTime = 0;
	}

	public MachineStatistics() {
		
	}

	public MachineStatistics(MachineStatistics statistics) {
		this.lastArrivalTime = statistics.lastArrivalTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ (int) (numberOfRequestsArrivalInLastInterval ^ (numberOfRequestsArrivalInLastInterval >>> 32));
		result = prime
				* result
				+ (int) (numberOfRequestsCompletionsInLastInterval ^ (numberOfRequestsCompletionsInLastInterval >>> 32));
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
		if (numberOfRequestsArrivalInLastInterval != other.numberOfRequestsArrivalInLastInterval)
			return false;
		if (numberOfRequestsCompletionsInLastInterval != other.numberOfRequestsCompletionsInLastInterval)
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
		
		numberOfRequestsArrivalInLastIntervalInTier++;
		
		double iat = 1.0*(arrivalTimeInMillis - (lastArrivalTime==0?arrivalTimeInMillis:lastArrivalTime))/TimeUnit.SECOND.getMillis();
		lastArrivalTime = arrivalTimeInMillis;
		
		
		double delta = iat - averageIAT;
		averageIAT += delta/numberOfRequestsArrivalInLastIntervalInTier;
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
				+ numberOfRequestsArrivalInLastInterval
				+ " , A_i= "
				+ numberOfRequestsArrivalInLastIntervalInTier
				+ " , C_0= "
				+ numberOfRequestsCompletionsInLastInterval
				+ " , C_i= "
				+ numberOfRequestsCompletionInLastIntervalInTier
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

		numberOfRequestsCompletionInLastIntervalInTier++;
		
		double delta = serviceTime - averageST;
		averageST += delta/numberOfRequestsCompletionInLastIntervalInTier;
		SSD_ST += delta*(serviceTime - averageST);
	}

	public double calcVarST() {
		return SSD_ST/numberOfRequestsCompletionInLastIntervalInTier;
	}

	public double calcVarIAT() {
		return SSD_IAT/numberOfRequestsArrivalInLastIntervalInTier;
	}
	
	public double getArrivalRate(long timeIntervalInSeconds){
		return 1.0*numberOfRequestsArrivalInLastInterval/timeIntervalInSeconds;
	}

	public double getArrivalRateInTier(long timeIntervalInSeconds){
		return 1.0*numberOfRequestsArrivalInLastIntervalInTier/timeIntervalInSeconds;
	}
}
