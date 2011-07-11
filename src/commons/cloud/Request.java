package commons.cloud;


public class Request{
	
	public String clientID;
	public String userID;
	public String reqID;
	public long time;//in millis
	public final long demand;//in millis
	public long totalProcessed;
	public long size;//in bytes
	public boolean hasExpired;
	public String httpOperation;
	public String URL;
	
	public Request(String clientID, String userID, String reqID, long time,
			long size, boolean hasExpired, String httpOperation, String URL, long demand) {
		this.clientID = clientID;
		this.userID = userID;
		this.reqID = reqID;
		this.time = time;
		this.demand = demand;
		this.totalProcessed = 0;
		this.size = size;
		this.hasExpired = hasExpired;
		this.httpOperation = httpOperation;
		this.URL = URL;
	}
	
	public void process(long time){
		if(time < 0){
			throw new RuntimeException("Invalid process amount: "+time+" in request "+this.reqID+" "+this.clientID);
		}
		this.totalProcessed += Math.min(time, this.demand - this.totalProcessed);
	}
	
	public boolean isFinished(){
		return this.totalProcessed >= this.demand;
	}
}
