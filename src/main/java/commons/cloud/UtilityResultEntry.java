package commons.cloud;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class UtilityResultEntry implements Comparable<UtilityResultEntry>{
	
	/**
	 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
	 *
	 */
	private static class UserEntry{

		private final int userID;
		private String contractName;
		private double totalReceipt;
		private long extraConsumedCPU;
		private double cpuCost;
		private long consumedTransference;
		private double transferenceCost;
		private double storageCost;

		/**
		 * Default constructor.
		 * @param userID
		 */
		public UserEntry(int userID) {
			this.userID = userID;
		}

		/**
		 * @param contractName
		 * @param total 
		 * @param storageCost 
		 * @param transferenceCost 
		 * @param consumedTransference 
		 * @param cpuCost 
		 * @param result
		 */
		public void add(String contractName, long extraConsumedCPU, double cpuCost, long consumedTransference, double transferenceCost, double storageCost, double total) {
			this.contractName = contractName;
			this.extraConsumedCPU = extraConsumedCPU;
			this.cpuCost = cpuCost;
			this.consumedTransference = consumedTransference;
			this.transferenceCost = transferenceCost;
			this.storageCost = storageCost;
			this.totalReceipt = total;
		}

		/**
		 * {@inheritDoc}
		 */
		
		@Override
		public String toString() {
			return userID 
					+ "\t" + contractName 
					+ "\t" + totalReceipt
					+ "\t" + extraConsumedCPU 
					+ "\t" + cpuCost
					+ "\t" + consumedTransference
					+ "\t" + transferenceCost
					+ "\t" + storageCost;
		}
	}
	
	/**
	 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
	 */
	private static class TypeEntry{
		
		private final MachineType type;
		private long onDemandCPUHours;
		private double onDemandCost;
		private long reservedCPUHours;
		private double reservedCost;

		/**
		 * Default constructor.
		 * @param type
		 * @param onDemandCPUHours
		 * @param onDemandCost
		 * @param reservedCPUHours
		 * @param reservedCost
		 */
		public TypeEntry(MachineType type) {
			this.type = type;
		}

		/**
		 * @param onDemandCPUHours
		 * @param onDemandCost
		 * @param reservedCPUHours
		 * @param reservedCost
		 */
		public void update(long onDemandCPUHours, double onDemandCost,
				long reservedCPUHours, double reservedCost) {
			this.onDemandCPUHours = onDemandCPUHours;
			this.onDemandCost = onDemandCost;
			this.reservedCPUHours = reservedCPUHours;
			this.reservedCost = reservedCost;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return type 
					+ "\t" + onDemandCPUHours 
					+ "\t" + onDemandCost
					+ "\t" + reservedCPUHours
					+ "\t" + reservedCost;
		}
		
		
	}
	
	/**
	 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
	 *
	 */
	private static class ProviderEntry{
		
		private final String name;
		private double cost;
		private double inCost;
		private double outCost;
		private double onDemandCost;
		private double reservedCost;
		
		private long inTransference;
		private long outTransference;
		private long onDemandCPUHours;
		private long reservedCPUHours;
		
		private Map<MachineType, TypeEntry> types;
		private double monitoringCost;
		
		/**
		 * Default constructor.
		 * @param machineTypes 
		 */
		public ProviderEntry(String name, MachineType[] machineTypes) {
			this.name = name;
			this.cost = 0;
			this.inCost = 0;
			this.outCost = 0;
			this.onDemandCost = 0;
			this.reservedCost = 0;
			this.inTransference = 0;
			this.outTransference = 0;
			this.onDemandCPUHours = 0;
			this.reservedCPUHours = 0;
			this.monitoringCost = 0;
			this.types = new TreeMap<MachineType, TypeEntry>();
			for (MachineType machineType : machineTypes) {
				this.types.put(machineType, new TypeEntry(machineType));
			}
		}

 		/**
		 * @param inTransference
		 * @param inCost
		 * @param outTransference
		 * @param outCost
		 */
		public void addTransference(long inTransference, double inCost,
				long outTransference, double outCost) {
			this.inTransference = inTransference;
			this.inCost = inCost;
			this.outTransference = outTransference;
			this.outCost = outCost;
			this.cost += (inCost + outCost);
		}

		/**
		 * @param type 
		 * @param onDemandCPUHours
		 * @param onDemandCost
		 * @param reservedCPUHours
		 * @param reservedCost
		 * @param monitoringCost 
		 */
		public void addUsage(MachineType type, long onDemandCPUHours, double onDemandCost,
				long reservedCPUHours, double reservedCost, double monitoringCost) {
			this.onDemandCPUHours += onDemandCPUHours;
			this.onDemandCost += onDemandCost;
			this.reservedCPUHours += reservedCPUHours;
			this.reservedCost += reservedCost;
			this.monitoringCost += monitoringCost;
			this.cost += (onDemandCost + reservedCost + monitoringCost);
			this.types.get(type).update(onDemandCPUHours, onDemandCost,
				reservedCPUHours, reservedCost);
		}

		/**
		 * {@inheritDoc}
		 */
		
		@Override
		public String toString() {
			return name 
					+ "\t" + cost
					+ "\t" + inCost 
					+ "\t" + outCost
					+ "\t" + onDemandCost 
					+ "\t" + reservedCost 
					+ "\t" + inTransference
					+ "\t" + outTransference
					+ "\t" + onDemandCPUHours
					+ "\t" + reservedCPUHours 
					+ "\t" + monitoringCost
					+ "\t" + format(types);
		}
	}

	private final long time;
	private double receipt;
	private double cost;
	private double penalty;
	
	private UserEntry[] users;
	private ProviderEntry[] providers;
	
	/**
	 * Default constructor
	 * @param time 
	 * @param providers2 
	 * @param users 
	 */
	public UtilityResultEntry(long time, User[] users, Provider[] providers) {
		this.time = time;
		this.receipt = 0;
		this.cost = 0;
		this.penalty = 0;
		this.users = new UserEntry[users.length];
		for (int i = 0; i < users.length; i++) {
			this.users[i] = new UserEntry(i);
		}
		this.providers = new ProviderEntry[providers.length];
		for (int i = 0; i < providers.length; i++) {
			this.providers[i] = new ProviderEntry(providers[i].getName(), providers[i].getAvailableTypes());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public int compareTo(UtilityResultEntry o) {
		if(this.time == o.time){
			return 0;
		}
		return this.time < o.time? -1: 1;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (time ^ (time >>> 32));
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UtilityResultEntry other = (UtilityResultEntry) obj;
		if (time != other.time)
			return false;
		return true;
	}

	/**
	 * @param contractName
	 * @param total
	 * @param transferenceCost 
	 * @param consumedTransference 
	 * @param storageCost 
	 * @param costOfCPU 
	 */
	public void addToReceipt(int userID, String contractName, long extraConsumedCPU, double cpuCost, long consumedTransference, double transferenceCost, double storageCost) {
		double total = cpuCost + transferenceCost + storageCost;
		users[userID].add(contractName, extraConsumedCPU, cpuCost, consumedTransference, transferenceCost, storageCost, total);
		receipt += total;
	}

	public void addTransferenceToCost(int provider, long inTransference, double inCost, long outTransference, double outCost) {
		providers[provider].addTransference(inTransference, inCost, outTransference, outCost);
		this.cost += (inCost + outCost);
	}
	
	public void addUsageToCost(int provider, MachineType type, long onDemandCPUHours, double onDemandCost, long reservedCPUHours, double reservedCost, double monitoringCost) {
		providers[provider].addUsage(type, onDemandCPUHours, onDemandCost, reservedCPUHours, reservedCost, monitoringCost);
		this.cost += (onDemandCost + reservedCost + monitoringCost);
	}

	/**
	 * @return The utility value of this entry.
	 */
	public double getUtility() {
		return receipt - cost - penalty;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @return the receipt
	 */
	public double getReceipt() {
		return receipt;
	}

	/**
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * @return the penalty
	 */
	public double getPenalty() {
		return penalty;
	}

	@Override
	public String toString() {
		return time + "\t" + getUtility() + "\t" + receipt	+ "\t" + cost + "\t" + penalty + "\t"
				+ format(users) + "\t" + format(providers);
	}

	/**
	 * @param map
	 * @return
	 */
	private static <K,V> String format(Map<K, V> map) {
		StringBuilder sb = new StringBuilder();
		for (Entry<K, V> entry : map.entrySet()) {
			sb.append(entry.getValue());
			sb.append('\t');
		}
		return sb.toString().substring(0, sb.length()-1);
	}
	
	/**
	 * @param map
	 * @return
	 */
	private static <T> String format(T[] map) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < map.length-1; i++) {
			sb.append(map[i]);
			sb.append('\t');
		}
		sb.append(map[map.length-1]);
		return sb.toString();
	}

}
