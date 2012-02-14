package saasim.cloud;

import java.io.Serializable;

/**
 * Abstraction used to represent a entry of one {@link User}.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class UserEntry implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6304129259620208175L;
	private int userID;
	public String contractName;
	private long extraConsumedCPU;
	private double cpuCost;
	private long consumedTransference;
	private double transferenceCost;
	private double storageCost;
	private double penalty;
	private long numberOfFinishedRequests;
	private long numberOfFinishedRequestsAfterSLA;
	private long totalNumberOfRequests;
	private double price;
	private long numberOfLostRequests;

	/**
	 * Default constructor.
	 * @param userID an integer represents the specific {@link User}.
	 * @param contractName the name of {@link Contract} assigned to {@link User} represented for this {@link UserEntry}.
	 * @param price TODO the price of {@link Contract} identified for contractName in parameter
	 * @param extraConsumedCPU value of extra consumed in cpu 
	 * @param cpuCost the cost of cpu
	 * @param consumedTransference value of consumed transference
	 * @param transferenceCost value of transference cost
	 * @param storageCost value of storage cost
	 * @param penalty value of penalty in the specified {@link Contract}
	 * @param numOfFinishedRequests number represents finished requests
	 * @param numOfLostRequests number represents lost requests
	 * @param numOfFinishedRequestsAfterSLA TODO number represents finished requests after the specified in the SLA
	 */
	public UserEntry(int userID, String contractName, double price, long extraConsumedCPU,
			         double cpuCost, long consumedTransference, double transferenceCost, 
			         double storageCost, double penalty, long numOfFinishedRequests, 
			         long numOfLostRequests, long numOfFinishedRequestsAfterSLA) {
		this.userID = 1;
		this.contractName = contractName;
		this.price = price;
		this.extraConsumedCPU = extraConsumedCPU;
		this.cpuCost = cpuCost;
		this.consumedTransference = consumedTransference;
		this.transferenceCost = transferenceCost;
		this.storageCost = storageCost;
		this.penalty = penalty;
		this.numberOfFinishedRequests = numOfFinishedRequests;
		this.numberOfFinishedRequestsAfterSLA = numOfFinishedRequestsAfterSLA;
		this.numberOfLostRequests = numOfLostRequests;
		this.totalNumberOfRequests = numOfLostRequests + numOfFinishedRequests + numOfFinishedRequestsAfterSLA;
	}

	/**
	 * Gets the receipt calculated by the price of {@link Contract} added to cpu cost and transference cost
	 * and storage cost.
	 * @return The receipt
	 */
	public double getReceipt() {
		return price + cpuCost + transferenceCost + storageCost;
	}

	/**
	 * Gets the value of penalty in the specified {@link Contract}
	 * @return The value of penalty 
	 */
	public double getPenalty() {
		return penalty;
	}

	/**
	 * Add updating the values of this {@link UserEntry} with the entry in parameter. 
	 * @param entry The entry to be added with the values of this {@link UserEntry}.
	 */
	public void add(UserEntry entry) {
		this.userID += entry.userID;
		this.price += entry.price;
		this.extraConsumedCPU += entry.extraConsumedCPU;
		this.cpuCost += entry.cpuCost;
		this.consumedTransference += entry.consumedTransference;
		this.transferenceCost += entry.transferenceCost;
		this.storageCost += entry.storageCost;
		this.penalty += entry.penalty;
		this.numberOfFinishedRequests += entry.numberOfFinishedRequests;
		this.numberOfFinishedRequestsAfterSLA += entry.numberOfFinishedRequestsAfterSLA;
		this.numberOfLostRequests += entry.numberOfLostRequests;
		this.totalNumberOfRequests += entry.totalNumberOfRequests;
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
				+ UtilityResultEntry.STRING + numberOfFinishedRequestsAfterSLA 
				+ UtilityResultEntry.STRING + numberOfLostRequests 
				+ UtilityResultEntry.STRING + totalNumberOfRequests 
				+ UtilityResultEntry.STRING + getReceipt()
				+ UtilityResultEntry.STRING + extraConsumedCPU 
				+ UtilityResultEntry.STRING + cpuCost
				+ UtilityResultEntry.STRING + consumedTransference
				+ UtilityResultEntry.STRING + transferenceCost
				+ UtilityResultEntry.STRING + storageCost;
	}
}