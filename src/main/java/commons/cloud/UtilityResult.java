package commons.cloud;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class UtilityResult {
	
	private SortedSet<UtilityResultEntry> entries;
	private Map<Integer, Double> usersUniqueFee;
	private Map<String, Map<MachineType, Double>> providersUniqueFee;
	
	private double uniqueReceipt;
	private double uniqueCost;
	
	/**
	 * Default constructor.
	 */
	public UtilityResult() {
		entries = new TreeSet<UtilityResultEntry>();
		usersUniqueFee = new HashMap<Integer, Double>();
		providersUniqueFee = new HashMap<String, Map<MachineType,Double>>();
		uniqueCost = 0;
		uniqueReceipt = 0;
	}

	/**
	 * @return The total utility value.
	 */
	public double getUtility() {
		double result = 0;
		for ( UtilityResultEntry entry : entries) {
			result += entry.getUtility();
		}
		result += (uniqueReceipt - uniqueCost);
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (UtilityResultEntry entry : entries) {
			sb.append(entry);
			sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * @param entry
	 */
	public void addEntry(UtilityResultEntry entry) {
		entries.add(entry);
	}
	
	/**
	 * @param entry
	 */
	public void addUserUniqueFee(int userID, double fee) {
		usersUniqueFee.put(userID, fee);
		uniqueReceipt += fee;
	}

	/**
	 * @param name
	 * @param type
	 * @param cost
	 */
	public void addProviderUniqueCost(String name, MachineType type, double cost) {
		if(!providersUniqueFee.containsKey(name)){
			providersUniqueFee.put(name, new HashMap<MachineType, Double>());
		}
		providersUniqueFee.get(name).put(type, cost);
		uniqueCost += cost;
	}
}
