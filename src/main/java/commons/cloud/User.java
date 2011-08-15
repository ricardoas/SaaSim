package commons.cloud;

import commons.io.TimeBasedWorkloadParser;

public class User implements Comparable<User>{
	
	private static int idGenerator = 0;
	
	private final int id;
	private final Contract contract;
	
	private long consumedCpuInMillis;
	private long consumedInTransferenceInBytes;
	private long consumedOutTransferenceInBytes;
	private long consumedStorageInBytes;

	public User(Contract contract) {
		this.id = idGenerator++;
		this.contract = contract;
		reset();
	}
	
	public void reset(){
		consumedCpuInMillis = 0;
		consumedInTransferenceInBytes = 0;
		consumedOutTransferenceInBytes = 0;
		consumedStorageInBytes = 0;
	}
	
	public void update(long consumedCpuInMillis, long inTransferenceInBytes, long outTransferenceInBytes, long storageInBytes){
		this.consumedCpuInMillis += consumedCpuInMillis;
		this.consumedInTransferenceInBytes += inTransferenceInBytes;
		this.consumedOutTransferenceInBytes += outTransferenceInBytes;
		this.consumedStorageInBytes += storageInBytes;
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
	 * @return the consumedCpu
	 */
	public long getConsumedCpu() {
		long consumedCpu = (long)Math.ceil(1.0 * consumedCpuInMillis / TimeBasedWorkloadParser.HOUR_IN_MILLIS);
		return Math.min(consumedCpu, contract.getCpuLimit());
	}

	/**
	 * @return the consumedCpu
	 */
	public long getExtraCpu() {
		long consumedCpu = (long)Math.ceil(1.0 * consumedCpuInMillis / TimeBasedWorkloadParser.HOUR_IN_MILLIS);
		return Math.max(consumedCpu-contract.getCpuLimit(), 0);
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

	@Override
	public String toString() {
		return "User [id=" + id + 
			 "\n      CPU used=" + consumedCpuInMillis + 
			 " (millis)\n      data transferred=" + consumedInTransferenceInBytes	+ 
			 " (B)\n      storage used=" + consumedStorageInBytes + " (B)]";
	}

	@Override
	public int compareTo(User o) {
		return this.contract.compareTo(o.contract);
	}

	public double calculateReceipt() {
		long consumedCpu = (long)Math.ceil(1.0 * consumedCpuInMillis / TimeBasedWorkloadParser.HOUR_IN_MILLIS);
		
		double receipt = this.contract.calculateReceipt(consumedCpu, consumedInTransferenceInBytes, consumedOutTransferenceInBytes, consumedStorageInBytes);
		this.reset();
		return receipt;
	}


	public double calculateUnicReceipt() {
		return this.contract.getSetupCost();
	}
}
