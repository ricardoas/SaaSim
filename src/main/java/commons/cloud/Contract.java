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
	 * @param planName
	 * @param priority
	 * @param setupCost
	 * @param price
	 * @param cpuLimitInMillis
	 * @param extraCpuCostPerMillis
	 * @param transferenceLimitsInBytes
	 * @param transferenceCostsPerBytes
	 * @param storageLimitInBytes
	 * @param storageCostPerBytes
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
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		assert name != null: "Null names are not allowed! Check your code.";
		final int prime = 31;
		int result = 1;
		result = prime * result + name.hashCode();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		assert obj != null: "Can't compare with a null object!";
		assert obj.getClass() == getClass(): "Can't compare with another class objects!";
		if (this == obj)
			return true;
		return name.equals(((Contract) obj).name);
	}
	
	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Contract o) {
		return this.name.compareTo(o.name);
	}

	/**
	 * @param consumedCpuInMillis
	 * @param consumedInTransferenceInBytes
	 * @param consumedOutTransferenceInBytes
	 * @param consumedStorageInBytes
	 * @param numOfFinishedRequests TODO
	 * @param numOfLostRequests TODO
	 * @param numOfFinishedRequestsAfterSLA TODO
	 * @return 
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
	 * @return
	 */
	public double calculateOneTimeFees() {
		return setupCost;
	}
	
	/**
	 * According to https://signin.crm.dynamics.com/portal/static/1046/sla.htm
	 * @param totalLoss
	 * @return
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
	
}
