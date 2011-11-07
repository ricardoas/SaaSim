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
	private final long storageInMB;
	
	/**
	 * Default constructor.
	 * @param contract
	 */
	public User(int id, Contract contract, long storageInMB) {
		this.id = id;
		this.contract = contract;
		this.storageInMB = storageInMB;
		reset();
	}
	
	public long getNumberOfLostRequests() {
		return numberOfLostRequests;
	}

	public long getConsumedCpuInMillis() {
		return consumedCpuInMillis;
	}

	/**
	 * @return the user's id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the contract
	 */
	public Contract getContract() {
		return contract;
	}

	/**
	 * @return the consumedInTransferenceInBytes
	 */
	public long getConsumedInTransferenceInBytes() {
		return consumedInTransferenceInBytes;
	}

	/**
	 * @return the consumedOutTransferenceInBytes
	 */
	public long getConsumedOutTransferenceInBytes() {
		return consumedOutTransferenceInBytes;
	}

	/**
	 * @return the consumedStorageInBytes
	 */
	public long getStorageInMB() {
		return storageInMB;
	}

	private void reset(){
		this.consumedCpuInMillis = 0;
		this.consumedInTransferenceInBytes = 0;
		this.consumedOutTransferenceInBytes = 0;
		this.numberOfLostRequests = 0;
		this.numberOfFinishedRequests = 0;
	}
	
	private void update(long consumedCpuInMillis, long inTransferenceInBytes, long outTransferenceInBytes){
		this.consumedCpuInMillis += consumedCpuInMillis;
		this.consumedInTransferenceInBytes += inTransferenceInBytes;
		this.consumedOutTransferenceInBytes += outTransferenceInBytes;
	}
	
	public void calculatePartialReceipt(UtilityResultEntry entry) {
		double penalty = this.contract.calculatePenalty((1.0 * numberOfLostRequests) / (numberOfLostRequests+numberOfFinishedRequests));
		entry.addPenalty(penalty);
		
		this.contract.calculateReceipt(entry, id, consumedCpuInMillis, consumedInTransferenceInBytes, consumedOutTransferenceInBytes, storageInMB);
		this.reset();
	}

	public void calculateOneTimeFees(UtilityResult result) {
		result.addUserUniqueFee(id, this.contract.calculateOneTimeFees());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;//TODO return id;
	}

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "User [id=" + id + ", contract=" + contract.getName()
				+ ", consumedCpuInMillis=" + consumedCpuInMillis
				+ ", consumedInTransferenceInBytes="
				+ consumedInTransferenceInBytes
				+ ", consumedOutTransferenceInBytes="
				+ consumedOutTransferenceInBytes + ", consumedStorageInBytes="
				+ storageInMB + "]";
	}

	@Override
	public int compareTo(User o) {
		return this.contract.compareTo(o.contract);
	}

	/**
	 * @param request
	 */
	public void reportFinishedRequest(Request request) {
		this.numberOfFinishedRequests++;
		update(request.getTotalProcessed(), request.getRequestSizeInBytes(), request.getResponseSizeInBytes());
	}

	/**
	 * @param request
	 */
	public void reportLostRequest(Request request) {
		this.numberOfLostRequests++;
		update(request.getTotalProcessed(), request.getRequestSizeInBytes(), 0);
	}

	public double calculatePenalty(double totalLoss) {
		return this.contract.calculatePenalty(totalLoss);
	}
}
