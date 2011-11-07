package commons.cloud;

import java.io.Serializable;

import commons.io.TickSize;
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

	private final String name;
	private final int priority;
	private final double price;
	private final double setupCost;
	private final long cpuLimitInMillis;
	private final double extraCpuCostPerMillis;
	private final long[] transferenceLimitsInMB;
	private final double[] transferenceCostsPerMB;
	private final long storageLimitInMB;
	private final double extraStorageCostPerMB;
	
	/**
	 * Default constructor.
	 * @param planName
	 * @param priority
	 * @param setupCost
	 * @param price
	 * @param cpuLimitInHours
	 * @param extraCpuCostPerHour
	 * @param transferenceLimitsInMB
	 * @param transferenceCostsPerMB
	 * @param storageLimitInMB
	 * @param storageCostPerMB
	 */
	public Contract(String planName, int priority, double setupCost, double price,
			long cpuLimitInHours, double extraCpuCostPerHour, long[] transferenceLimitsInMB, double[] transferenceCostsPerMB,
			long storageLimitInMB, double storageCostPerMB) {
		this.name = planName;
		this.priority = priority;
		this.price = price;
		this.setupCost = setupCost;
		this.cpuLimitInMillis = cpuLimitInHours * TickSize.HOUR.getTickInMillis();
		this.extraCpuCostPerMillis = extraCpuCostPerHour / TickSize.HOUR.getTickInMillis();
		this.transferenceLimitsInMB = transferenceLimitsInMB;
		this.transferenceCostsPerMB = transferenceCostsPerMB;
		this.storageLimitInMB = storageLimitInMB;
		this.extraStorageCostPerMB = storageCostPerMB;
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
	public double getExtraCpuCostPerMillis() {
		return extraCpuCostPerMillis;
	}
	
	/**
	 * @return the transferenceLimits
	 */
	public long[] getTransferenceLimitsInMB() {
		return transferenceLimitsInMB;
	}

	/**
	 * @return the transferenceCosts
	 */
	public double[] getTransferenceCostsPerMB() {
		return transferenceCostsPerMB;
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
		return extraStorageCostPerMB;
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
	 * @param consumedCpu
	 * @param consumedInTransferenceInBytes
	 * @param consumedOutTransferenceInBytes
	 * @param consumedStorageInMB
	 */
	public void calculateReceipt(UtilityResultEntry entry, int userID, long consumedCpu, long consumedInTransferenceInBytes,
			long consumedOutTransferenceInBytes, long consumedStorageInMB) {
		long extraConsumedCPU = Math.max(0, consumedCpu - cpuLimitInMillis);
		double costOfCPU = price + (1.0*extraConsumedCPU)/TickSize.HOUR.getTickInMillis() * extraCpuCostPerMillis;
		
		long consumedTransference = consumedInTransferenceInBytes + consumedOutTransferenceInBytes;
		double transferenceCost = CostCalculus.calcTransferenceCost(consumedTransference , transferenceLimitsInMB, transferenceCostsPerMB, CostCalculus.MB_IN_BYTES);
		
		double storageCost = Math.max(0, consumedStorageInMB - storageLimitInMB) * extraStorageCostPerMB;
		
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
