package commons.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import commons.cloud.Request;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TimeBasedWorkloadParser implements WorkloadParser<List<Request>>{
	
	public static final long SECOND_IN_MILLIS = 1000;
	
	public static final long MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS;
	
	public static final long HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS;
	
	public static final long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;

	public static final long MONTH_IN_MILLIS = 31 * DAY_IN_MILLIS;
	
	private final WorkloadParser<Request> parser;
	private final long tick;
	private long currentTick;

	private Request leftOver;
	
	/**
	 * @param parser
	 * @param tick
	 */
	public TimeBasedWorkloadParser(WorkloadParser<Request> parser, long tick) {
		this.parser = parser;
		this.tick = tick;
		this.currentTick = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Request> next() throws IOException {
		List<Request> requests = new ArrayList<Request>();
		Request r = leftOver != null? leftOver: parser.next();
		long time = leftOver != null? Math.min(leftOver.getTimeInMillis()/tick, currentTick): currentTick;
		while(hasNext() && time == currentTick){
			requests.add(r);
			r = parser.next();
			time = r.getTimeInMillis()/tick;
		}
		currentTick++;
		if(r != null){
			leftOver = r;
		}
		return requests;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		return parser.hasNext();
	}

}
