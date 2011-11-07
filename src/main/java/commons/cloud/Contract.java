package commons.cloud;

import java.io.Serializable;

import commons.util.CostCalculus;


/**
 * SaaS client contract. Most frequent information, based on research.
 *  
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.1
 */
public class Contract implements Comparable<Contract>, Serializable{
	
	/**
	 * Version 1.1 - limits in MB 
	 */
	private static final long serialVersionUID = 5890271348794166938L;

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
	 * @return the extraCpuCostPerMillis
	 */
	public double getExtraCpuCostPerMillis() {
		return extraCpuCostPerMillis;
	}

	/**
	 * @return the transferenceLimitsInBytes
	 */
	public long[] getTransferenceLimitsInBytes() {
		return transferenceLimitsInBytes;
	}

	/**
	 * @return the transferenceCostsInBytes
	 */
	public double[] getTransferenceCostsPerByte() {
		return transferenceCostsInBytes;
	}

	/**
	 * @return the storageLimitInBytes
	 */
	public long getStorageLimitInBytes() {
		return storageLimitInBytes;
	}

	/**
	 * @return the extraStorageCostPerByte
	 */
	public double getExtraStorageCostPerByte() {
		return extraStorageCostPerByte;
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
		return "Contract [name=" + name + ", price=" + price + ", setupCost="
				+ setupCost + ", cpuLimit=" + cpuLimitInMillis + ", extraCpuCost="
				+ extraCpuCostPerMillis + "]";
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
	 * @param consumedCpuInMillis
	 * @param consumedInTransferenceInBytes
	 * @param consumedOutTransferenceInBytes
	 * @param consumedStorageInBytes
	 */
	public void calculateReceipt(UtilityResultEntry entry, int userID, long consumedCpuInMillis, long consumedInTransferenceInBytes,
			long consumedOutTransferenceInBytes, long consumedStorageInBytes) {
		
		long extraConsumedCPUInMillis = Math.max(0, consumedCpuInMillis - cpuLimitInMillis);
		long consumedTransferenceInBytes = consumedInTransferenceInBytes + consumedOutTransferenceInBytes;
		
		double CPUCost = price + extraConsumedCPUInMillis * extraCpuCostPerMillis;
		double transferenceCost = CostCalculus.calcTransferenceCost(consumedTransferenceInBytes , transferenceLimitsInBytes, transferenceCostsInBytes);
		double storageCost = Math.max(0, consumedStorageInBytes - storageLimitInBytes) * extraStorageCostPerByte;
		
		entry.addToReceipt(userID, name, extraConsumedCPUInMillis, CPUCost, consumedTransferenceInBytes, transferenceCost, storageCost);
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
