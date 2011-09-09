package commons.cloud;




/**
 * Class representing a SaaS user. For a user that generates request using an application see
 * {@link Request#getUserID()}.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class User implements Comparable<User>{
	
	private final String id;
	private final Contract contract;
	
	private long consumedCpuInMillis;
	private long consumedInTransferenceInBytes;
	private long consumedOutTransferenceInBytes;
	private final long consumedStorageInBytes;

	/**
	 * Default constructor.
	 * @param contract
	 */
	public User(String userID, Contract contract, long consumedStorageInBytes) {
		this.id = userID;
		this.contract = contract;
		this.consumedStorageInBytes = consumedStorageInBytes;
		reset();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the contract
	 */
	public Contract getContract() {
		return contract;
	}

	/**
	 * @return the consumedCpu
	 */
	public long getTotalConsumedCpuInMillis() {
		return consumedCpuInMillis;
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
	public long getConsumedStorageInBytes() {
		return consumedStorageInBytes;
	}

	private void reset(){
		consumedCpuInMillis = 0;
		consumedInTransferenceInBytes = 0;
		consumedOutTransferenceInBytes = 0;
	}
	
	private void update(long consumedCpuInMillis, long inTransferenceInBytes, long outTransferenceInBytes){
		this.consumedCpuInMillis += consumedCpuInMillis;
		this.consumedInTransferenceInBytes += inTransferenceInBytes;
		this.consumedOutTransferenceInBytes += outTransferenceInBytes;
	}
	
	public void calculatePartialReceipt(UtilityResultEntry entry) {
		this.contract.calculateReceipt(entry, getId(), consumedCpuInMillis, consumedInTransferenceInBytes, consumedOutTransferenceInBytes, consumedStorageInBytes);
		this.reset();
	}


	public void calculateOneTimeFees(UtilityResult result) {
		result.addUserUniqueFee(getId(), this.contract.calculateOneTimeFees());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
				+ consumedStorageInBytes + "]";
	}

	@Override
	public int compareTo(User o) {
		return this.contract.compareTo(o.contract);
	}

	/**
	 * @param request
	 */
	public void reportFinishedRequest(Request request) {
		update(request.getTotalProcessed(), request.getRequestSizeInBytes(), request.getResponseSizeInBytes());
	}

	/**
	 * @param request
	 */
	public void reportLostRequest(Request request) {
		update(request.getTotalProcessed(), request.getRequestSizeInBytes(), 0);
	}
}
