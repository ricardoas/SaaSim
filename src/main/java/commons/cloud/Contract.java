package commons.cloud;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Contract implements Comparable<Contract>{
	
	private final String name;
	private final int priority;
	private final double price;//in $
	private final double setupCost;//in $
	private final long cpuLimit;// in hours
	private final double extraCpuCost;// in $/hour
	private final long[] transferenceLimits;
	private final double[] transferenceCosts;
	
	public Contract(String planName, int priority, double setupCost, double price,
			long cpuLimit, double extraCpuCost, long[] transferenceLimits, double[] transferenceCosts) {
		this.name = planName;
		this.priority = priority;
		this.setupCost = setupCost;
		this.price = price;
		this.cpuLimit = cpuLimit;
		this.extraCpuCost = extraCpuCost;
		this.transferenceLimits = transferenceLimits;
		this.transferenceCosts = transferenceCosts;
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
	 * @return the cpuLimit
	 */
	public long getCpuLimit() {
		return cpuLimit;
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
	public long[] getTransferenceLimits() {
		return transferenceLimits;
	}

	/**
	 * @return the transferenceCosts
	 */
	public double[] getTransferenceCosts() {
		return transferenceCosts;
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
				+ setupCost + ", cpuLimit=" + cpuLimit + ", extraCpuCost="
				+ extraCpuCost + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(Contract o) {
		return o.priority - this.priority;
	}
	
}
