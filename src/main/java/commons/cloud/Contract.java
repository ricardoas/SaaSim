package commons.cloud;

import java.io.Serializable;

import commons.util.CostCalculus;


/**
 * SaaS client contract. Most frequent information, based on research.
 *  
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.0
 */
public class Contract implements Comparable<Contract>, Serializable{
	
	/**
	 * Version 1.0
	 */
	private static final long serialVersionUID = 6035889541983334272L;
	private static final long HOUR_IN_MILLIS = 3600000;
	private static final long MB_IN_BYTES = 1024 * 1024;

	private final String name;
	private final int priority;
	private final double price;//in $
	private final double setupCost;//in $
	private final long cpuLimitInMillis;// in hours
	private final double extraCpuCost;// in $/hour
	private final long[] transferenceLimitsInBytes;
	private final double[] transferenceCosts;
	private final long storageLimitInMB;
	private final double storageCostPerMB;
	
	/**
	 * Default constructor.
	 * @param planName
	 * @param priority
	 * @param setupCost
	 * @param price
	 * @param cpuLimitInMillis
	 * @param extraCpuCost
	 * @param transferenceLimitsInBytes
	 * @param transferenceCosts
	 * @param storageLimitInMB
	 * @param storageCostPerMB
	 */
	public Contract(String planName, int priority, double setupCost, double price,
			long cpuLimitInMillis, double extraCpuCost, long[] transferenceLimitsInBytes, double[] transferenceCosts,
			long storageLimitInMB, double storageCostPerMB) {
		this.name = planName;
		this.priority = priority;
		this.price = price;
		this.setupCost = setupCost;
		this.cpuLimitInMillis = cpuLimitInMillis;
		this.extraCpuCost = extraCpuCost;
		this.transferenceLimitsInBytes = transferenceLimitsInBytes;
		this.transferenceCosts = transferenceCosts;
		this.storageLimitInMB = storageLimitInMB;
		this.storageCostPerMB = storageCostPerMB;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * @return the setupCost
	 */
	public double getSetupCost() {
		return setupCost;
	}

	/**
	 * @return the cpuLimitInMillis
	 */
	public long getCpuLimitInMillis() {
		return cpuLimitInMillis;
	}

	/**
	 * @return the extraCpuCost
	 */
	public double getExtraCpuCost() {
		return extraCpuCost;
	}
	
	/**
	 * @return the transferenceLimits
	 */
	public long[] getTransferenceLimitsInBytes() {
		return transferenceLimitsInBytes;
	}

	/**
	 * @return the transferenceCosts
	 */
	public double[] getTransferenceCosts() {
		return transferenceCosts;
	}
	
	/**
	 * @return the storageLimitInMB
	 */
	public long getStorageLimitInMB() {
		return storageLimitInMB;
	}

	/**
	 * @return storageStorageCostPerMB
	 */
	public double getStorageCostPerMB() {
		return storageCostPerMB;
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
		Contract other = (Contract) obj;
		return name.equals(other.name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Contract [name=" + name + ", price=" + price + ", setupCost="
				+ setupCost + ", cpuLimit=" + cpuLimitInMillis + ", extraCpuCost="
				+ extraCpuCost + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Contract o) {
		return o.priority - this.priority;
	}

	/**
	 * @param entry
	 * @param consumedCpu
	 * @param consumedInTransferenceInBytes
	 * @param consumedOutTransferenceInBytes
	 * @param consumedStorageInBytes
	 */
	public void calculateReceipt(UtilityResultEntry entry, int userID, long consumedCpu, long consumedInTransferenceInBytes,
			long consumedOutTransferenceInBytes, long consumedStorageInBytes) {
		long extraConsumedCPU = Math.max(0, consumedCpu - cpuLimitInMillis);
		double costOfCPU = price + (1.0*extraConsumedCPU)/HOUR_IN_MILLIS * extraCpuCost;
		
		long consumedTransference = consumedInTransferenceInBytes + consumedOutTransferenceInBytes;
		double transferenceCost = CostCalculus.calcTransferenceCost(consumedTransference , transferenceLimitsInBytes, transferenceCosts, CostCalculus.MB_IN_BYTES);
		
		double storageCost = Math.max(0, (1.0*consumedStorageInBytes)/MB_IN_BYTES - storageLimitInMB) * storageCostPerMB;
		
		entry.addToReceipt(userID, getName(), extraConsumedCPU, costOfCPU, consumedTransference, transferenceCost, storageCost);
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
