package commons.cloud;

import java.util.Arrays;


/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class Request{
	
	private final int saasClient;
	private final int userID;
	private final long reqID;
	private final long arrivalTimeInMillis;
	private final long[] cpuDemandInMillis;
	private final long requestSizeInBytes;
	private final long responseSizeInBytes;
	
	private long totalProcessed;
	private MachineType value;
	
	/**
	 * @param reqID
	 * @param saasClient
	 * @param userID
	 * @param arrivalTimeInMillis
	 * @param requestSizeInBytes
	 * @param responseSizeInBytes
	 * @param cpuDemandInMillis
	 */
	public Request(long reqID, int saasClient, int userID, long arrivalTimeInMillis,
			long requestSizeInBytes, long responseSizeInBytes, long[] cpuDemandInMillis) {
		this.saasClient = saasClient;
		this.reqID = reqID;
		this.userID = userID;
		this.arrivalTimeInMillis = arrivalTimeInMillis;
		this.requestSizeInBytes = requestSizeInBytes;
		this.responseSizeInBytes = responseSizeInBytes;
		this.cpuDemandInMillis = cpuDemandInMillis;
		this.totalProcessed = 0;
	}
	
	/**
	 * @return the saasClient
	 */
	public int getSaasClient() {
		return saasClient;
	}
	
	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * @return the arrivalTimeInMillis
	 */
	public long getArrivalTimeInMillis() {
		return arrivalTimeInMillis;
	}

	/**
	 * @return the cpuDemandInMillis
	 */
	public long[] getCpuDemandInMillis() {
		return cpuDemandInMillis;
	}

	/**
	 * @return the requestSizeInBytes
	 */
	public long getRequestSizeInBytes() {
		return requestSizeInBytes;
	}

	/**
	 * @return the responseSizeInBytes
	 */
	public long getResponseSizeInBytes() {
		return responseSizeInBytes;
	}

	/**
	 * @return the value
	 */
	public MachineType getValue() {
		return value;
	}

	/**
	 * @param value
	 */
	public void assignTo(MachineType value){
		this.value = value;
	}

	/**
	 * Updates processed demand value.
	 *  
	 * @param processedDemand
	 */
	public void update(long processedDemand){
		assert processedDemand >= 0 : "Invalid process amount: "+processedDemand+" in request "+this.reqID+" of "+this.saasClient;
		
		this.totalProcessed += Math.min(processedDemand, getTotalToProcess());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (arrivalTimeInMillis ^ (arrivalTimeInMillis >>> 32));
		result = prime * result + (userID ^ (userID >>> 32));
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
		Request other = (Request) obj;
		if (arrivalTimeInMillis != other.arrivalTimeInMillis)
			return false;
		if (userID != other.userID)
			return false;
		return true;
	}

	/**
	 * @return
	 */
	public boolean isFinished(){
		return getTotalToProcess() == 0;
	}

	/**
	 * @return
	 */
	public long getTotalToProcess() {
		return getDemand() - this.totalProcessed;
	}
	
	private long getDemand(){
		return cpuDemandInMillis[value.ordinal()];
	}

	/**
	 * 
	 */
	public void reset() {
		this.totalProcessed = 0;
		this.value = null;
	}

	/**
	 * @return the totalProcessed
	 */
	public long getTotalProcessed() {
		return totalProcessed;
	}

	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public String toString() {
		return "Request [saasClient=" + saasClient + ", reqID=" + reqID
				+ ", userID=" + userID + ", arrivalTime=" + arrivalTimeInMillis
				+ ", cpuDemandInMillis=" + Arrays.toString(cpuDemandInMillis)
				+ ", requestSizeInBytes=" + requestSizeInBytes
				+ ", responseSizeInBytes=" + responseSizeInBytes
				+ ", totalProcessed=" + totalProcessed + ", assignedTo=" + (value==null?"Nobody":value.toString())
				+ "]";
	}
	
}
