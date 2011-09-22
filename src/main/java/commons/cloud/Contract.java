package commons.cloud;

import commons.util.CostCalculus;


/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Contract implements Comparable<Contract>{
	
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
	
	public Contract(String planName, int priority, double setupCost, double price,
			long cpuLimitInMillis, double extraCpuCost, long[] transferenceLimitsInBytes, double[] transferenceCosts,
			long storageLimitInMB, double storageCostPerMB) {
		this.name = planName;
		this.priority = priority;
		this.setupCost = setupCost;
		this.price = price;
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
	
	public long getStorageLimitInMB() {
		return storageLimitInMB;
	}

	public double getStorageCostPerMB() {
		return storageCostPerMB;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Contract other = (Contract) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

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
	
	public double calculateOneTimeFees() {
		return setupCost;
	}
	
	//According to https://signin.crm.dynamics.com/portal/static/1046/sla.htm
	public double calculatePenalty(double totalLoss) {
		if(totalLoss <= 0.001){
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
