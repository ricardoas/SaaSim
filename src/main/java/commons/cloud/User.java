package commons.cloud;

import java.io.Serializable;


/**
 * Class representing a SaaS client. For a user that generates request using an application see
 * {@link Request#getUserID()}.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.0
 */
public class User implements Comparable<User>, Serializable{
	
	/**
	 * Version 1.0
	 */
	private static final long serialVersionUID = 7315070558750123127L;
	private final int id;
	private final Contract contract;
	
	private long numberOfLostRequests;
	private int numberOfFinishedRequests;
	private long consumedCpuInMillis;
	private long consumedInTransferenceInBytes;
	private long consumedOutTransferenceInBytes;
	private final long storageInBytes;
	private int numberOfFinishedRequestsAfterSLA;
	
	/**
	 * Default constructor.
	 * @param id an integer represents each user.
	 * @param contract the {@link Contract} assigned to this {@link User}.
	 * @param storageInBytes a value represents storage in bytes for this user.
	 */
	public User(int id, Contract contract, long storageInBytes) {
		this.id = id;
		this.contract = contract;
		this.storageInBytes = storageInBytes;
		reset();
	}
	
	/**
	 * Gets the user's id.
	 * @return The user's id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the {@link Contract} assigned for this {@link User}
	 * @return The contract of this user.
	 */
	public Contract getContract() {
		return contract;
	}
	
	/**
	 * Gets the number of lost requests of this {@link User}.
	 * @return The number of lost requests of this {@link User}
	 */
	public long getNumberOfLostRequests() {
		return numberOfLostRequests;
	}

	/**
	 * Gets the consumed of cpu in millis.
	 * @return A long represents the consumed of cpu in millis.
	 */
	public long getConsumedCpuInMillis() {
		return consumedCpuInMillis;
	}

	/**
	 * Gets the value of consume in bytes in transference.
	 * @return The consume in transference in bytes.
	 */
	public long getConsumedInTransferenceInBytes() {
		return consumedInTransferenceInBytes;
	}

	/**
	 * Gets the value of consume in bytes out transference.
	 * @return The consume out transference in bytes.
	 */
	public long getConsumedOutTransferenceInBytes() {
		return consumedOutTransferenceInBytes;
	}

	/**
	 * Gets the value of storage in bytes.
	 * @return The value of storage in bytes.
	 */
	public long getStorageInBytes() {
		return storageInBytes;
	}

	/**
	 * Reset the values about this {@link User}.
	 */
	private void reset(){
		this.consumedCpuInMillis = 0;
		this.consumedInTransferenceInBytes = 0;
		this.consumedOutTransferenceInBytes = 0;
		this.numberOfLostRequests = 0;
		this.numberOfFinishedRequests = 0;
		this.numberOfFinishedRequestsAfterSLA = 0;
	}
	
	/**
	 * Update the values of consumed cpu, input and output transference.
	 * @param consumedCpuInMillis the consumed cpu in millis to be added to actual consumed cpu. 
	 * @param inTransferenceInBytes the input transference in bytes to be added to actual input transference.
	 * @param outTransferenceInBytes the output transference in bytes to be added to actual output transference.
	 */
	private void update(long consumedCpuInMillis, long inTransferenceInBytes, long outTransferenceInBytes){
		this.consumedCpuInMillis += consumedCpuInMillis;
		this.consumedInTransferenceInBytes += inTransferenceInBytes;
		this.consumedOutTransferenceInBytes += outTransferenceInBytes;
	}
	
	/**
	 * Gets the value of setup cost for the {@link Contract} of this {@link User}.
	 * @return The value of setup cost. 
	 */
	public double calculateOneTimeFees() {
		return contract.calculateOneTimeFees();
	}
	
	/**
	 * Calculate the penalty of this {@link User}.
	 * @param totalLoss a double represents the total percent of requests loss.
	 * @return The penalty calculated.
	 */
	public double calculatePenalty(double totalLoss) {
		return this.contract.calculatePenalty(totalLoss);
	}
	
	/**
	 * Calculate the partial receipt of this {@link User}.
	 * @return A {@link UserEntry} encapsulating the calculated receipt.
	 */
	public UserEntry calculatePartialReceipt() {
		UserEntry userEntry = contract.calculateReceipt(id, consumedCpuInMillis, consumedInTransferenceInBytes, consumedOutTransferenceInBytes, storageInBytes, numberOfFinishedRequests, numberOfLostRequests, numberOfFinishedRequestsAfterSLA);
		reset();
		return userEntry;
	}

	/**
	 * Report when a specific request has been finished.
	 * @param request The {@link Request} finished.
	 */
	public void reportFinishedRequest(Request request) {
		this.numberOfFinishedRequests++;
		update(request.getTotalProcessed(), request.getRequestSizeInBytes(), request.getResponseSizeInBytes());
	}

	/**
	 * Report when a specific request has been lost.
	 * @param request The {@link Request} lost.
	 */
	public void reportLostRequest(Request request) {
		this.numberOfLostRequests++;
		update(request.getTotalProcessed(), request.getRequestSizeInBytes(), 0);
	}

	/**
	 * Report when a specific request has been finished after SLA defines.
	 * @param request The {@link Request} finished.
	 */
	public void reportFinishedRequestAfterSLA(Request request) {
		this.numberOfFinishedRequestsAfterSLA++;
		update(request.getTotalProcessed(), request.getRequestSizeInBytes(), request.getResponseSizeInBytes());
	}

	/**
	 * Compare two users.
	 * <code>true</code> if them id is equals, <code>false</code> otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		assert obj != null: "Comparing with a null object, check code.";
		assert obj.getClass() == getClass(): "Comparing with an object of another class, check code."; 

		if (this == obj)
			return true;
		User other = (User) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	@Override
	public int compareTo(User o) {
		return this.contract.compareTo(o.contract);
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", contract=" + contract
				+ ", consumedCpuInMillis=" + consumedCpuInMillis
				+ ", consumedInTransferenceInBytes="
				+ consumedInTransferenceInBytes
				+ ", consumedOutTransferenceInBytes="
				+ consumedOutTransferenceInBytes + ", consumedStorageInBytes="
				+ storageInBytes + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;//TODO return id;
	}

}
