package commons.cloud;

import java.io.Serializable;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class UserEntry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6304129259620208175L;
	private final int userID;
	public String contractName;
	private long extraConsumedCPU;
	private double cpuCost;
	private long consumedTransference;
	private double transferenceCost;
	private double storageCost;
	private double penalty;
	private long numberOfFinishedRequests;
	private long totalNumberOfRequests;
	private double price;

	/**
	 * Default constructor.
	 * @param price TODO
	 */
	public UserEntry(int userID, String contractName,
			double price, long extraConsumedCPU,
			double cpuCost, long consumedTransference,
			double transferenceCost, double storageCost, double penalty, long numOfFinishedRequests, long numOfLostRequests) {
		this.userID = userID;
		this.contractName = contractName;
		this.price = price;
		this.extraConsumedCPU = extraConsumedCPU;
		this.cpuCost = cpuCost;
		this.consumedTransference = consumedTransference;
		this.transferenceCost = transferenceCost;
		this.storageCost = storageCost;
		this.penalty = penalty;
		this.numberOfFinishedRequests = numOfFinishedRequests;
		this.totalNumberOfRequests = numOfLostRequests + numOfFinishedRequests;
	}

	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public String toString() {
		return userID 
				+ UtilityResultEntry.STRING + contractName 
				+ UtilityResultEntry.STRING + getPenalty() 
				+ UtilityResultEntry.STRING + numberOfFinishedRequests 
				+ UtilityResultEntry.STRING + totalNumberOfRequests 
				+ UtilityResultEntry.STRING + getReceipt()
				+ UtilityResultEntry.STRING + extraConsumedCPU 
				+ UtilityResultEntry.STRING + cpuCost
				+ UtilityResultEntry.STRING + consumedTransference
				+ UtilityResultEntry.STRING + transferenceCost
				+ UtilityResultEntry.STRING + storageCost;
	}

	public double getReceipt() {
		return price + cpuCost + transferenceCost + storageCost;
	}

	public double getPenalty() {
		return penalty;
	}

	public void add(UserEntry entry) {
		this.price += entry.price;
		this.penalty += entry.penalty;
		this.numberOfFinishedRequests += entry.numberOfFinishedRequests;
		this.totalNumberOfRequests += entry.totalNumberOfRequests;
		this.extraConsumedCPU += entry.extraConsumedCPU;
		this.cpuCost += entry.cpuCost;
		this.consumedTransference += entry.consumedTransference;
		this.transferenceCost += entry.transferenceCost;
		this.storageCost += entry.storageCost;
	}
	
}