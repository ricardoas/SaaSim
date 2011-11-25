package commons.cloud;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class UtilityResultEntry implements Comparable<UtilityResultEntry>, Serializable {

	private static final long serialVersionUID = -4927865959874743247L;

	static final String STRING = "\t";
	
	private final long time;
	private double receipt;
	private double cost;
	private double penalty;
	
	private StringBuilder usersBuilder;
	private StringBuilder providersBuilder;
	private int numberOfUsers;
	private int numberOfProviders;
	
	private Map<String, UserEntry> contractEntries;
	
	public UtilityResultEntry(long time, User[] users, Provider[] providers) {
		assert users != null;
		assert providers != null;
		
		this.time = time;
		this.receipt = 0;
		this.cost = 0;
		this.penalty = 0;
		
		numberOfUsers = users.length;
		numberOfProviders = providers.length;
		
		usersBuilder = new StringBuilder();
		providersBuilder = new StringBuilder();
		
		contractEntries = new TreeMap<String, UserEntry>();
		
		for (int i = 0; i < numberOfUsers; i++) {
			UserEntry entry = users[i].calculatePartialReceipt();
			receipt += entry.getReceipt();
			penalty += entry.getPenalty();
			
			if(!contractEntries.containsKey(entry.contractName)) {
				contractEntries.put(entry.contractName, entry);
			} else {
				UserEntry contractEntry = contractEntries.get(entry.contractName);
				contractEntry.add(entry);
			}
		}
		
		for (Entry<String, UserEntry> entry : contractEntries.entrySet()) {
			usersBuilder.append(entry.getValue());
			usersBuilder.append(STRING);
		}
		
		for (int i = 0; i < numberOfProviders; i++) {
			ProviderEntry entry = providers[i].calculateCost(time);
			cost += entry.getCost();
			providersBuilder.append(entry);
			providersBuilder.append(STRING);
		}
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
		StringBuilder sb = new StringBuilder("ACCOUNTING");
		sb.append('\t');
		sb.append(time);
		sb.append('\t');
		sb.append(getUtility());
		sb.append('\t');
		sb.append(receipt);
		sb.append('\t');
		sb.append(cost);
		sb.append('\t');
		sb.append(penalty);
		sb.append('\t');
		sb.append(usersBuilder);
		sb.append(providersBuilder);
		return sb.toString().substring(0, sb.length()-1);
		
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(UtilityResultEntry o) {
		return this.time < o.time? -1: (this.time == o.time? 0: 1);
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
		assert (obj != null);
		assert (getClass() == obj.getClass());
		
		if (this == obj)
			return true;
		UtilityResultEntry other = (UtilityResultEntry) obj;
		if (time != other.time)
			return false;
		return true;
	}

	public String getEntryDescriptor() {
		StringBuilder sb = new StringBuilder();
//	sb.append("time\tutility\treceipt\tcost\tpenalty\t");
//	for (int i = 0; i < numberOfUsers; i++) {
//		sb.append("userID\tpenalty\tfinished\ttotal\tcontract\treceipt\textraCPU\tcpuCost\ttrans\ttransCost\tstorageCost\t");
//	}
//	for (ProviderEntry provider : providers) {
//		sb.append("name\tcost\tonDemand\tonDCost\treserv\tresCost\tinTrans\tinCost\toutTrans\toutCost\tmon\t");
//		sb.append(provider.getDescriptor());
//		}
		return sb.toString();
	}
	
}
	
	