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
		private long consumedCPU;
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
		public void add(String contractName, long consumedCPU, double cpuCost, long consumedTransference, double transferenceCost, double storageCost, double total) {
			this.contractName = contractName;
			this.consumedCPU = consumedCPU;
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
					+ "," + contractName 
					+ "," + totalReceipt
					+ "," + consumedCPU 
					+ "," + cpuCost
					+ "," + consumedTransference
					+ "," + transferenceCost
					+ "," + storageCost;
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
					+ "," + onDemandCPUHours 
					+ "," + onDemandCost
					+ "," + reservedCPUHours
					+ "," + reservedCost;
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
			this.cost += (onDemandCost + reservedCost);
			this.types.get(type).update(onDemandCPUHours, onDemandCost,
				reservedCPUHours, reservedCost);
		}

		/**
		 * {@inheritDoc}
		 */
		
		@Override
		public String toString() {
			return name 
					+ "," + cost
					+ "," + inCost 
					+ "," + outCost
					+ "," + onDemandCost 
					+ "," + reservedCost 
					+ "," + inTransference
					+ "," + outTransference
					+ "," + onDemandCPUHours
					+ "," + reservedCPUHours 
					+ "," + monitoringCost
					+ "," + format(types);
		}
	}

	private final long time;
	private double receipt;
	private double cost;
	private double penalty;
	
	private Map<Integer, UserEntry> users;
	private Map<String, ProviderEntry> providers;
	
	/**
	 * Default constructor
	 * @param time 
	 * @param providers2 
	 * @param users 
	 */
	public UtilityResultEntry(long time, Map<Integer, User> users, Map<String, Provider> providers) {
		this.time = time;
		this.receipt = 0;
		this.cost = 0;
		this.penalty = 0;
		this.users = new TreeMap<Integer, UserEntry>();
		for (Entry<Integer, User> entry : users.entrySet()) {
			this.users.put(entry.getKey(), new UserEntry(entry.getKey()));
		}
		this.providers = new TreeMap<String, ProviderEntry>();
		for (Entry<String, Provider> entry : providers.entrySet()) {
			this.providers.put(entry.getKey(), new ProviderEntry(entry.getKey(), entry.getValue().getAvailableTypes()));
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public int compareTo(UtilityResultEntry o) {
		return this.time < o.time? -1: 1;
	}

	/**
	 * @param contractName
	 * @param total
	 * @param transferenceCost 
	 * @param consumedTransference 
	 * @param storageCost 
	 * @param costOfCPU 
	 */
	public void addToReceipt(int userID, String contractName, long consumedCPU, double cpuCost, long consumedTransference, double transferenceCost, double storageCost) {
		double total = cpuCost + transferenceCost + storageCost;
		users.get(userID).add(contractName, consumedCPU, cpuCost, consumedTransference, transferenceCost, storageCost, total);
		receipt += total;
	}

	public void addTransferenceToCost(String provider, long inTransference, double inCost, long outTransference, double outCost) {
		providers.get(provider).addTransference(inTransference, inCost, outTransference, outCost);
		this.cost += (inCost + outCost);
	}
	
	public void addUsageToCost(String provider, MachineType type, long onDemandCPUHours, double onDemandCost, long reservedCPUHours, double reservedCost, double monitoringCost) {
		providers.get(provider).addUsage(type, onDemandCPUHours, onDemandCost, reservedCPUHours, reservedCost, monitoringCost);
		this.cost += (onDemandCost + reservedCost);
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
		return time + "," + getUtility() + "," + receipt	+ "," + cost + "," + penalty + ","
				+ format(users) + "," + format(providers);
	}

	/**
	 * @param users2
	 * @return
	 */
	private static <K,V> String format(Map<K, V> map) {
		StringBuilder sb = new StringBuilder();
		for (Entry<K, V> entry : map.entrySet()) {
			sb.append(entry.getValue());
			sb.append(',');
		}
		return sb.toString().substring(0, sb.length()-1);
	}
	
	
}
