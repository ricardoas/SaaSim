package commons.cloud;

import commons.cloud.UtilityResult.UtilityResultEntry;


/**
 * Class representing a SaaS user. For a user that generates request using an application see
 * {@link Request#getUserID()}.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class User implements Comparable<User>{
	
	private static int idGenerator = 0;
	
	private final int id;
	private final Contract contract;
	
	private long consumedCpuInMillis;
	private long consumedInTransferenceInBytes;
	private long consumedOutTransferenceInBytes;
	private final long consumedStorageInBytes;

	/**
	 * Default constructor.
	 * @param contract
	 */
	public User(Contract contract, long consumedStorageInBytes) {
		this.id = idGenerator++;
		this.contract = contract;
		this.consumedStorageInBytes = consumedStorageInBytes;
		reset();
	}
	
	/**
	 * @return the id
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
	
	public void update(long consumedCpuInMillis, long inTransferenceInBytes, long outTransferenceInBytes){
		this.consumedCpuInMillis += consumedCpuInMillis;
		this.consumedInTransferenceInBytes += inTransferenceInBytes;
		this.consumedOutTransferenceInBytes += outTransferenceInBytes;
	}
	
	public void calculatePartialReceipt(UtilityResultEntry entry) {
		entry.addUser(getId());
		this.contract.calculateReceipt(entry, consumedCpuInMillis, consumedInTransferenceInBytes, consumedOutTransferenceInBytes, consumedStorageInBytes);
		this.reset();
	}


	public double calculateOneTimeFees() {
		return this.contract.calculateOneTimeFees();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
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
				+ consumedStorageInBytes + "]";
	}

	@Override
	public int compareTo(User o) {
		return this.contract.compareTo(o.contract);
	}
}
