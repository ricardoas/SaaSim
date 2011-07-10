package commons.cloud;


public class Request{
	
	public String clientID;
	public String userID;
	public String reqID;
	public long time;//in millis
	public long size;//in bytes
	public boolean hasExpired;
	public String httpOperation;
	public String URL;
	
	public Request(String clientID, String userID, String reqID, long time,
			long size, boolean hasExpired, String httpOperation, String URL) {
		this.clientID = clientID;
		this.userID = userID;
		this.reqID = reqID;
		this.time = time;
		this.size = size;
		this.hasExpired = hasExpired;
		this.httpOperation = httpOperation;
		this.URL = URL;
	}
	

}
