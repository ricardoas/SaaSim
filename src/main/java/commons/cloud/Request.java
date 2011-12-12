package commons.cloud;

import java.io.Serializable;
import java.util.Arrays;


/**
 * Represents a request to be processed in the system.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 * @version 1.0
 */
public class Request implements Serializable{
	
	/**
	 * Versino 1.0
	 */
	private static final long serialVersionUID = -6648523605288936095L;
	private int saasClient;
	private int userID;
	private long reqID;
	private long arrivalTimeInMillis;
	private long[] cpuDemandInMillis;
	private long requestSizeInBytes;
	private long responseSizeInBytes;
	
	private long totalProcessed;
	private MachineType value;
	private long finishTimeInMillis;
	
	/**
	 * Default constructor.
	 * @param reqID a integer to represent each {@link Request}.
	 * @param saasClient the client of saas
	 * @param userID the id of the user sending this {@link Request}.
	 * @param arrivalTimeInMillis the arrival time in millis of the request
	 * @param requestSizeInBytes the size in bytes of the request 
	 * @param responseSizeInBytes the response size in bytes
	 * @param cpuDemandInMillis the value of cpu demand in millis
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
	 * Gets the id of this {@link Request}.
	 * @return The id of this request.
	 */
	public long getReqID() {
		return reqID;
	}

	/**
	 * Gets the value of saas client.
	 * @return The value of saas client
	 */
	public int getSaasClient() {
		return saasClient;
	}
	
	/**
	 * Gets the id of the user sending this {@link Request}.
	 * @return The id of the user sending this {@link Request}
	 */
	public int getUserID() {
		return userID;
	}
	
	/**
	 * Gets the value represents for {@link MachineType}
	 * @return The type of the machine this {@link Request} was assigned.
	 */
	public MachineType getValue() {
		return value;
	}

	/**
	 * Gets the arrival time in millis of this {@link Request}.
	 * @return The arrival time in millis of this {@link Request} 
	 */
	public long getArrivalTimeInMillis() {
		return arrivalTimeInMillis;
	}

	/**
	 * Gets the demand in millis of cpu.
	 * @return The demand in millis of cpu
	 */
	public long[] getCpuDemandInMillis() {
		return cpuDemandInMillis;
	}

	/**
	 * Gets the request size in bytes.
	 * @return The request size in bytes
	 */
	public long getRequestSizeInBytes() {
		return requestSizeInBytes;
	}

	/**
	 * Gets the response size in bytes.
	 * @return The response size in bytes
	 */
	public long getResponseSizeInBytes() {
		return responseSizeInBytes;
	}
	
	/**
	 * Gets the value of demand for this {@link Request}.
	 * @return The value of demand for this {@link Request}.
	 */
	private long getDemand(){
		return cpuDemandInMillis[value.ordinal()];
	}
	
	/**
	 * Gets the value of total to process for this {@link Request}, based in the values of demand and total processed.
	 * @return The value of total to process for this {@link Request}.
	 */
	public long getTotalToProcess() {
		return getDemand() - this.totalProcessed;
	}
	
	/**
	 * Gets the value of total mean to process for this {@link Request}.
	 * @return The value of total mean to process for this {@link Request}.
	 */
	public long getTotalMeanToProcess() {
		long total = 0;
		for(long demand : this.cpuDemandInMillis){
			total += demand;
		}
		return total / this.cpuDemandInMillis.length;
	}
	
	/**
	 * Gets the total processed for this {@link Request}.
	 * @return The total processed for this {@link Request}
	 */
	public long getTotalProcessed() {
		return totalProcessed;
	}
	
	/**
	 * Gets the response time in millis. 
	 * @return The response time in millis.
	 */
	public long getResponseTimeInMillis(){
		return finishTimeInMillis - arrivalTimeInMillis;
	}
	
	/**
	 * Set the finish time to a new value.
	 * @param finishTimeInMillis The value set the actual finish time.
	 */
	public void setFinishTime(long finishTimeInMillis) {
		this.finishTimeInMillis = finishTimeInMillis;
	}

	/**
	 * Assign this {@link Request} for one {@link MachineType}.
	 * @param value the {@link MachineType} of this request will be assigned.
	 */
	public void assignTo(MachineType value){
		this.value = value;
	}

	/**
	 * Updates processed demand value.
	 * @param processedDemand the value of processed demand
	 */
	public void update(long processedDemand){
		assert processedDemand >= 0 : "Invalid process amount: "+processedDemand+" in request "+this.reqID+" of "+this.saasClient;
		
		this.totalProcessed += Math.min(processedDemand, getTotalToProcess());
	}

	/**
	 * Returns a value about the status of this {@link Request}, in this case, if it's finished.
	 * @return <code>true</code> if the total to process of this {@link Request} is equals zero, <code>false</code> otherwise.
	 */
	public boolean isFinished(){
		return getTotalToProcess() == 0;
	}

	/**
	 * Reset this {@link Request}, set the value of total processed to 0 (zero) and the type of the machine 
	 * this {@link Request} was assigned to <code>null</code>.
	 */
	public void reset() {
		this.totalProcessed = 0;
		this.value = null;
	}	
	
	/**
	 * Compare two requests.
	 * Return <code>true</code> if them saasClient and id are equals, <code>false</code> otherwise. 
	 */
	@Override
	public boolean equals(Object obj) {
		assert obj != null: "Comparing with a null object, check code.";
		assert obj.getClass() == getClass(): "Comparing with an object of another class, check code."; 
		
		Request other = (Request) obj;
		if (saasClient != other.saasClient)
			return false;
		return reqID == other.reqID;
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
}
