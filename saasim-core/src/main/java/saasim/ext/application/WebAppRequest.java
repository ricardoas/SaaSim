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
	
	private long serviceTimeInMillis;
	private long finishTimeInMillis;

	public WebAppRequest(long id, int tenantID, int userID, long arrivalTimeInMillis, long requestSizeInBytes,
			long responseSizeInBytes, long[] demand) {
				this.id = id;
				this.tenantID = tenantID;
				this.userID = userID;
				this.arrivalTimeInMillis = arrivalTimeInMillis;
				this.requestSizeInBytes = requestSizeInBytes;
				this.responseSizeInBytes = responseSizeInBytes;
				this.demand = demand;
				
				this.serviceTimeInMillis = 0;
				this.finishTimeInMillis = 0;
	}

	@Override
	public long getArrivalTimeInMillis() {
		return arrivalTimeInMillis;
	}

	@Override
	public long getArrivalTimeInSeconds() {
		return arrivalTimeInMillis/1000;
	}

	@Override
	public long getCPUTimeDemandInMillis() {
		return demand[0];
	}

	@Override
	public void updateServiceTime(long cpuTimeDemandInMillis) {
		this.serviceTimeInMillis += cpuTimeDemandInMillis;
	}

	@Override
	public void setFinishTime(long finishTimeInMillis) {
		this.finishTimeInMillis = finishTimeInMillis;
	}

	@Override
	public long getResponseTimeInMillis() {
		return finishTimeInMillis - arrivalTimeInMillis;
	}

}
