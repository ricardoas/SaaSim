package saasim.cloud.utility;

import java.io.Serializable;

import saasim.cloud.Provider;
import saasim.cloud.User;

/**
 * Abstraction to represent a calculated utility for the application. 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class UtilityResult implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7954582098382863519L;
	private static final char FIELD_SEPARATOR = '\t';
	private double uniqueReceipt;
	private double receipt;
	private double uniqueCost;
	private double cost;
	private double penalty;
	
	/**
	 * Default constructor.
	 * @param numberOfUsers the number of users in the application 
	 * @param numberOfProviders the number of provides in the application
	 */
	public UtilityResult(int numberOfUsers, int numberOfProviders) {
		uniqueCost = 0;
		uniqueReceipt = 0;
	}

	/**
	 * Another constructor.
	 * @param users an array containing the users in the application
	 * @param providers an array containing the providers in the application
	 */
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
	 * Update the values of receipt, cost and penalty in this {@link UtilityResul} with entry in the parameter.
	 * @param entry the entry to be added to values of receipt, cost and penalty, see {@link UtilityResultEntry}.
	 */
	public void account(UtilityResultEntry entry) {
		receipt += entry.getReceipt();
		cost += entry.getCost();
		penalty += entry.getPenalty();
	}

	/**
	 * Gets the value of utility.
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
		StringBuilder sb = new StringBuilder("UTILITY");
		
		sb.append(FIELD_SEPARATOR);
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
