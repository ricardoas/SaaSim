package commons.cloud;

import java.io.Serializable;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class UtilityResult implements Serializable{
	
	private static final char FIELD_SEPARATOR = '\t';
	/**
	 * 
	 */
	private static final long serialVersionUID = -7954582098382863519L;
	
	private double uniqueReceipt;
	private double receipt;
	private double uniqueCost;
	private double cost;
	private double penalty;
	
	/**
	 * Default constructor.
	 * @param numberOfUsers 
	 * @param numberOfProviders 
	 */
	public UtilityResult(int numberOfUsers, int numberOfProviders) {
		uniqueCost = 0;
		uniqueReceipt = 0;
	}

	public UtilityResult(User[] users, Provider[] providers) {
		for (User user : users) {
			uniqueReceipt += user.calculateOneTimeFees();
		}
		for (Provider provider : providers) {
			uniqueCost += provider.calculateUniqueCost();
		}
		receipt = 0;
		cost = 0;
		penalty = 0;
	}

	/**
	 * @param entry
	 * @return 
	 */
	public void account(UtilityResultEntry entry) {
		receipt += entry.getReceipt();
		cost += entry.getCost();
		penalty += entry.getPenalty();
	}

	/**
	 * @return The total utility value.
	 */
	public double getUtility() {
		return uniqueReceipt + receipt - uniqueCost - cost - penalty;
	}
	
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getUtility());
		sb.append(FIELD_SEPARATOR);
		sb.append(uniqueReceipt);
		sb.append(FIELD_SEPARATOR);
		sb.append(receipt);
		sb.append(FIELD_SEPARATOR);
		sb.append(-uniqueCost);
		sb.append(FIELD_SEPARATOR);
		sb.append(-cost);
		sb.append(FIELD_SEPARATOR);
		sb.append(-penalty);
		
		return sb.toString();
	}
}
