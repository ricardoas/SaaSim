package commons.cloud;

import java.io.Serializable;
import java.util.Arrays;

import commons.util.CostCalculus;


/**
 * SaaS client contract. Most frequent information, based on research.
 *  
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.2
 */
public class Contract implements Comparable<Contract>, Serializable{
	
	/**
	 * Version 1.2 - limits in Bytes 
	 */
	private static final long serialVersionUID = -242235177646945721L;

	private final String name;
	private final int priority;
	private final double price;
	private final double setupCost;
	private final long cpuLimitInMillis;
	private final double extraCpuCostPerMillis;
	private final long[] transferenceLimitsInBytes;
	private final double[] transferenceCostsInBytes;
	private final long storageLimitInBytes;
	private final double extraStorageCostPerByte;
	
	/**
	 * Default constructor.
	 * @param planName the name of plan for this {@link Contract}.
	 * @param priority the priority 
	 * @param setupCost the setup cost
	 * @param price the price defined for this {@link Contract}.
	 * @param cpuLimitInMillis the limit of cpu
	 * @param extraCpuCostPerMillis the extra cost of cpu per millis
	 * @param transferenceLimitsInBytes the transference limits in bytes
	 * @param transferenceCostsPerBytes the transference costs per bytes
	 * @param storageLimitInBytes the storage limit in bytes
	 * @param storageCostPerBytes the storage cost per bytes
	 */
	public Contract(String planName, int priority, double setupCost, double price,
			long cpuLimitInMillis, double extraCpuCostPerMillis, long[] transferenceLimitsInBytes, double[] transferenceCostsPerBytes,
			long storageLimitInBytes, double storageCostPerBytes) {
		this.name = planName;
		this.priority = priority;
		this.price = price;
		this.setupCost = setupCost;
		this.cpuLimitInMillis = cpuLimitInMillis;
		this.extraCpuCostPerMillis = extraCpuCostPerMillis;
		this.transferenceLimitsInBytes = transferenceLimitsInBytes;
		this.transferenceCostsInBytes = transferenceCostsPerBytes;
		this.storageLimitInBytes = storageLimitInBytes;
		this.extraStorageCostPerByte = storageCostPerBytes;
	}

	/**
	 * Calculate the receipt for this {@link Contract}. 
	 * @param userID the id of user assign to this {@link Contract}.
	 * @param consumedCpuInMillis the consumed cpu in millis 
	 * @param consumedInTransferenceInBytes the consumed input transference in bytes
	 * @param consumedOutTransferenceInBytes the consumed output transference in bytes
	 * @param consumedStorageInBytes the consumed storage in bytes
	 * @param numOfFinishedRequests TODO number represents a finished requests
	 * @param numOfLostRequests TODO number represents a lost requests
	 * @param numOfFinishedRequestsAfterSLA TODO number represents a finished requests after SLA defines.
	 * @return A {@link UserEntry} encapsulating the calculated receipt.
	 */
	public UserEntry calculateReceipt(int userID, long consumedCpuInMillis, long consumedInTransferenceInBytes, long consumedOutTransferenceInBytes,
			long consumedStorageInBytes, long numOfFinishedRequests, long numOfLostRequests, long numOfFinishedRequestsAfterSLA) {
		
		long extraConsumedCPUInMillis = Math.max(0, consumedCpuInMillis - cpuLimitInMillis);
		long consumedTransferenceInBytes = consumedInTransferenceInBytes + consumedOutTransferenceInBytes;
		
		double CPUCost = extraCpuCostPerMillis * extraConsumedCPUInMillis;
		double transferenceCost = CostCalculus.calcTransferenceCost(consumedTransferenceInBytes , transferenceLimitsInBytes, transferenceCostsInBytes);
		double storageCost = Math.max(0, consumedStorageInBytes - storageLimitInBytes) * extraStorageCostPerByte;
		
		double percentageLost = (1.0*numOfLostRequests + numOfFinishedRequestsAfterSLA)/(numOfFinishedRequests + numOfFinishedRequestsAfterSLA + numOfLostRequests);
		
		return new UserEntry(userID, name, price, extraConsumedCPUInMillis, CPUCost, consumedTransferenceInBytes, transferenceCost, storageCost, calculatePenalty(percentageLost), numOfFinishedRequests, numOfLostRequests, numOfFinishedRequestsAfterSLA);
	}
	
	/**
	 * Gets the value of setup cost for this {@link Contract}.
	 * @return The value of setup cost. 
	 */
	public double calculateOneTimeFees() {
		return setupCost;
	}
	
	/**
	 * According to https://signin.crm.dynamics.com/portal/static/1046/sla.htm
	 * @param totalLoss a double represents the total percent of requests loss.
	 * @return The calculated penalty.
	 */
	public double calculatePenalty(double totalLoss) {
		if(totalLoss <= 0.001 || Double.isInfinite(totalLoss) || Double.isNaN(totalLoss)){
			return 0;
		}else if(totalLoss > 0.001 && totalLoss <= 0.01){
			return price * 0.25;
		}else if(totalLoss > 0.01 && totalLoss <= 0.05){
			return price * 0.5;
		}else{
			return price  * 1;
		}
	}

	/**
	 * Compare two {@link Contract}.
	 * <code>true</code> if them name are equals, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		assert obj != null: "Can't compare with a null object!";
		assert obj.getClass() == getClass(): "Can't compare with another class objects!";
		if (this == obj)
			return true;
		return name.equals(((Contract) obj).name);
	}
	
	@Override
	public int compareTo(Contract o) {
		return this.name.compareTo(o.name);
	}
	
	@Override
	public String toString() {
		// Extremely inefficient but used just for debugging purposes.
		return "Contract [name=" + name + ", priority=" + priority + ", price="
				+ price + ", setupCost=" + setupCost + ", cpuLimitInMillis="
				+ cpuLimitInMillis + ", extraCpuCostPerMillis="
				+ extraCpuCostPerMillis + ", transferenceLimitsInBytes="
				+ Arrays.toString(transferenceLimitsInBytes)
				+ ", transferenceCostsInBytes="
				+ Arrays.toString(transferenceCostsInBytes)
				+ ", storageLimitInBytes=" + storageLimitInBytes
				+ ", extraStorageCostPerByte=" + extraStorageCostPerByte + "]";
	}
	
	@Override
	public int hashCode() {
		assert name != null: "Null names are not allowed! Check your code.";
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		return result;
	}
	
}
