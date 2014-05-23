package saasim.ext.saas;

import java.util.Deque;
import java.util.LinkedList;

import saasim.core.saas.Request;
import saasim.core.saas.ResponseListener;

/**
 * Web application {@link Request}. 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
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
	private Deque<ResponseListener> listeners;
	private int tier;

	/**
	 * Default constructor.
	 * 
	 * @param id
	 * @param tenantID
	 * @param userID
	 * @param arrivalTimeInMillis
	 * @param requestSizeInBytes
	 * @param responseSizeInBytes
	 * @param demand
	 */
	public WebAppRequest(long id, int tenantID, int userID, long arrivalTimeInMillis, long requestSizeInBytes,
			long responseSizeInBytes, long[] demand) {
				this.id = id;
				this.tenantID = tenantID;
				this.userID = userID;
				this.arrivalTimeInMillis = arrivalTimeInMillis;
				this.requestSizeInBytes = requestSizeInBytes;
				this.responseSizeInBytes = responseSizeInBytes;
				this.demand = demand;
				this.tier = 0;
				
				this.serviceTimeInMillis = 0;
				this.finishTimeInMillis = 0;
				this.listeners = new LinkedList<>();
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
		return (tier == demand.length)? -1: demand[tier];
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

	@Override
	public ResponseListener getResponseListener() {
		return listeners.pop();
	}

	@Override
	public void setResponseListener(ResponseListener listener) {
		listeners.push(listener);
	}

	@Override
	public long getID() {
		return id;
	}

	@Override
	public int getTenantID() {
		return tenantID;
	}

	@Override
	public int getCurrentTier() {
		return tier;
	}

	@Override
	public void forward() {
		tier++;
	}

	@Override
	public void rollback() {
		tier--;
	}

}
