package saasim.ext.application;

import saasim.core.application.Request;

public class WebAppRequest implements Request {
	
	private final long arrivalTimeInMillis;
	private final long id;
	private final int tenantID;
	private final int userID;
	private final long requestSizeInBytes;
	private final long responseSizeInBytes;
	private final long[] demand;

	public WebAppRequest(long id, int tenantID, int userID, long arrivalTimeInMillis, long requestSizeInBytes,
			long responseSizeInBytes, long[] demand) {
				this.id = id;
				this.tenantID = tenantID;
				this.userID = userID;
				this.arrivalTimeInMillis = arrivalTimeInMillis;
				this.requestSizeInBytes = requestSizeInBytes;
				this.responseSizeInBytes = responseSizeInBytes;
				this.demand = demand;
	}

	@Override
	public long getArrivalTimeInMillis() {
		return arrivalTimeInMillis;
	}

}
