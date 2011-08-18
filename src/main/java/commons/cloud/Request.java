package commons.cloud;

import java.util.Arrays;


/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class Request{
	
	private final String saasClient;
	private final String reqID;
	private final String userID;
	private final long arrivalTime;
	private final long[] cpuDemandInMillis;
	private final long requestSizeInBytes;
	private final long responseSizeInBytes;
	
	public long totalProcessed;
	private MachineType value;
	
	/**
	 * @param reqID
	 * @param saasClient
	 * @param userID
	 * @param arrivalTime
	 * @param requestSizeInBytes
	 * @param responseSizeInBytes
	 * @param cpuDemandInMillis
	 */
	public Request(String reqID, String saasClient, String userID, long arrivalTime,
			long requestSizeInBytes, long responseSizeInBytes, long[] cpuDemandInMillis) {
		this.saasClient = saasClient;
		this.reqID = reqID;
		this.userID = userID;
		this.arrivalTime = arrivalTime;
		this.requestSizeInBytes = requestSizeInBytes;
		this.responseSizeInBytes = responseSizeInBytes;
		this.cpuDemandInMillis = cpuDemandInMillis;
		this.totalProcessed = 0;
	}
	
	/**
	 * @return the saasClient
	 */
	public String getSaasClient() {
		return saasClient;
	}

	/**
	 * @return the reqID
	 */
	public String getReqID() {
		return reqID;
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @return the arrivalTime
	 */
	public long getArrivalTime() {
		return arrivalTime;
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
		if(processedDemand < 0){
			throw new RuntimeException("Invalid process amount: "+processedDemand+" in request "+this.reqID+" of "+this.saasClient);
		}
		this.totalProcessed += Math.min(processedDemand, getTotalToProcess());
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
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((reqID == null) ? 0 : reqID.hashCode());
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
		Request other = (Request) obj;
		if (reqID == null) {
			if (other.reqID != null)
				return false;
		} else if (!reqID.equals(other.reqID))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public String toString() {
		return "Request [saasClient=" + saasClient + ", reqID=" + reqID
				+ ", userID=" + userID + ", arrivalTime=" + arrivalTime
				+ ", cpuDemandInMillis=" + Arrays.toString(cpuDemandInMillis)
				+ ", requestSizeInBytes=" + requestSizeInBytes
				+ ", responseSizeInBytes=" + responseSizeInBytes
				+ ", totalProcessed=" + totalProcessed + ", assignedTo=" + value==null?"Nobody":value
				+ "]";
	}
	
	
}
