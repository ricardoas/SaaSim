package saasim.ext.saas;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import saasim.core.saas.Request;
import saasim.core.saas.ResponseListener;

/**
 * Web application {@link Request}. 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class MultiQueriesRequest implements Request {
	
	private final long arrivalTimeInMillis;
	private final long id;
	private final int tenantID;
	private final int userID;
	private final long requestSizeInBytes;
	private final long responseSizeInBytes;
	private final long[] demand;
	
	private long serviceTimeInMillis[];
	private long finishTimeInMillis;
	private Deque<ResponseListener> listeners;
	private int tier;
	private long arrival[];

	private int index;

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
	 * @param tokens 
	 */
	public MultiQueriesRequest(long id, int tenantID, int userID, long arrivalTimeInMillis, long requestSizeInBytes,
			long responseSizeInBytes, long[] demand) {
				this.id = id;
				this.tenantID = tenantID;
				this.userID = userID;
				this.arrivalTimeInMillis = arrivalTimeInMillis;
				this.requestSizeInBytes = requestSizeInBytes;
				this.responseSizeInBytes = responseSizeInBytes;
				this.demand = demand;
				this.index = 0;
				this.tier = 0;
				
				this.serviceTimeInMillis = new long[demand.length/2];
				this.finishTimeInMillis = 0;
				this.listeners = new LinkedList<>();
				this.arrival = new long[demand.length];
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
		return demand[2*index + 1] - serviceTimeInMillis[index];
	}

	@Override
	public void updateServiceTime(long cpuTimeDemandInMillis) {
		this.serviceTimeInMillis[index] += cpuTimeDemandInMillis;
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
		return listeners.pollFirst();
	}

	@Override
	public void setResponseListener(ResponseListener listener) {
		listeners.addFirst(listener);
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
		return 2*index < demand.length? (int) demand[2*index]: 0;
	}

	@Override
	public void forward() {
		index++;
	}

	@Override
	public void rollback() {
		index++;
	}

	@Override
	public void pushArrival(long arrival) {
		this.arrival[getCurrentTier()] = arrival;
	}

	@Override
	public long popArrival() {
		return this.arrival[getCurrentTier()];
	}
	
	@Override
	public int hashCode() {
		return (int)(id ^ (id >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return id == ((MultiQueriesRequest) obj).id;
	}
}
