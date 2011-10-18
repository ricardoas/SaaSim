package commons.cloud;

import java.io.Serializable;
import java.util.Arrays;


/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class Request implements Serializable{
	
	private int saasClient;
	private int userID;
	private long reqID;
	private long arrivalTimeInMillis;
	private long[] cpuDemandInMillis;
	private long requestSizeInBytes;
	private long responseSizeInBytes;
	
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

	public long getReqID() {
		return reqID;
	}

	public void setReqID(long reqID) {
		this.reqID = reqID;
	}

	public void setSaasClient(int saasClient) {
		this.saasClient = saasClient;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public void setArrivalTimeInMillis(long arrivalTimeInMillis) {
		this.arrivalTimeInMillis = arrivalTimeInMillis;
	}

	public void setCpuDemandInMillis(long[] cpuDemandInMillis) {
		this.cpuDemandInMillis = cpuDemandInMillis;
	}

	public void setRequestSizeInBytes(long requestSizeInBytes) {
		this.requestSizeInBytes = requestSizeInBytes;
	}

	public void setResponseSizeInBytes(long responseSizeInBytes) {
		this.responseSizeInBytes = responseSizeInBytes;
	}

	public void setTotalProcessed(long totalProcessed) {
		this.totalProcessed = totalProcessed;
	}

	public void setValue(MachineType value) {
		this.value = value;
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (reqID ^ (reqID >>> 32));
		result = prime * result + saasClient;
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		assert obj != null: "Comparing with a null object, check code.";
		assert obj.getClass() == getClass(): "Comparing with an object of another class, check code."; 
		
		Request other = (Request) obj;
		if (saasClient != other.saasClient)
			return false;
		if (reqID != other.reqID)
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
	
	public long getTotalMeanToProcess() {
		long total = 0;
		for(long demand : this.cpuDemandInMillis){
			total += demand;
		}
		
		return total / this.cpuDemandInMillis.length;
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
