package commons.cloud;


/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class Request{
	
	private final String clientID;
	private final String userID;
	private final String reqID;
	private final long timeInMillis;
	private final long demandInMillis;
	private final long sizeInBytes;
	private final long responseSizeInBytes;
	private final int requestOption;//Indicates whether the request is a non-SSL (expired or not) or a SSL one 
	private final String httpOperation;
	private final String URL;
	
	public long totalProcessed;
	
	/**
	 * @param clientID
	 * @param userID
	 * @param reqID
	 * @param time
	 * @param size
	 * @param requestOption
	 * @param httpOperation
	 * @param URL
	 * @param demand
	 */
	public Request(String clientID, String userID, String reqID, long time,
			long size, int requestOption, String httpOperation, String URL, long demand) {
		this.clientID = clientID;
		this.userID = userID;
		this.reqID = reqID;
		this.timeInMillis = time;
		this.demandInMillis = demand;
		this.sizeInBytes = size;
		this.responseSizeInBytes = 1000000;
		this.requestOption = requestOption;
		this.httpOperation = httpOperation;
		this.URL = URL;
		this.totalProcessed = 0;
	}
	
	/**
	 * Updates processed demand value.<p> 
	 * This method assumes processedDemand &leq; demandInMillis - totalProcessed
	 * @param processedDemand
	 */
	public void update(long processedDemand){
		if(processedDemand < 0){
			throw new RuntimeException("Invalid process amount: "+processedDemand+" in request "+this.reqID+" "+this.clientID);
		}
		this.totalProcessed += processedDemand;
	}
	
	public boolean isFinished(){
		return this.totalProcessed >= this.demandInMillis;//FIXME >= ??? shouldnt it be == ?
	}

	public long getTotalToProcess() {
		return this.demandInMillis - this.totalProcessed;
	}

	public long getDemand() {
		return demandInMillis;
	}

	/**
	 * @return the timeInMillis
	 */
	public long getTimeInMillis() {
		return timeInMillis;
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @return the sizeInBytes
	 */
	public long getSizeInBytes() {
		return sizeInBytes;
	}

	public String getRequestID(){
		return this.reqID;
	}

	/**
	 * 
	 */
	public void reset() {
		this.totalProcessed = 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((reqID == null) ? 0 : reqID.hashCode());
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
		if (reqID == null) {
			if (other.reqID != null)
				return false;
		} else if (!reqID.equals(other.reqID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Request [reqID=" + reqID + ", demandInMillis=" + demandInMillis
				+ ", totalProcessed=" + totalProcessed + "]";
	}

	
	public long getResponseSizeInBytes() {
		return responseSizeInBytes;
	}
}
