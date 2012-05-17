package saasim.io;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import saasim.cloud.Request;
import saasim.util.TimeUnit;


/**
 * A different implementation of {@link WorkloadParser}, based on history.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class HistoryBasedWorkloadParser extends TimeBasedWorkloadParser {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1264664080670986778L;

	private static final int DEFAULT_WINDOW_SIZE = 2;
	
	private LinkedBlockingQueue<List<Request>> history;
	private boolean readNexPeriod = true;

	/**
	 * FIXME!
	 * Default constructor.
	 * @param parser a {@link WorkloadParser} of {@link Request}.
	 * @param tick a {@link TimeUnit} represents an actual time
	 */
	public HistoryBasedWorkloadParser(WorkloadParser<Request> parser, TimeUnit tick) {
		this(parser, tick, DEFAULT_WINDOW_SIZE);
	}
	
	/**
	 * Another constructor receiving a size of window.
	 * @param parser a {@link WorkloadParser} of {@link Request}.
	 * @param tick a {@link TimeUnit} represents an actual time
	 * @param windowSize a size of window
	 */
	@SuppressWarnings("unchecked")
	public HistoryBasedWorkloadParser(WorkloadParser<Request> parser, TimeUnit tick, int windowSize) {
		super(tick.getMillis(), parser);//FIXME remove null
		this.history = new LinkedBlockingQueue<List<Request>>(windowSize);
	}
	
	/**
	 * Gets the history of requests in this {@link HistoryBasedWorkloadParser}
	 * @return a queue containing lists of {@link Request} like a historic. 
	 */
	public Queue<List<Request>> getHistory(){
		return new LinkedBlockingQueue<List<Request>>(history);
	}
	
	/**
	 * Read a next period in the workload, and return a list of {@link Request} equivalent a this period.
	 */
	@Override
	public List<Request> next(){
		if(readNexPeriod){
			List<Request> next = super.next();
			if(!history.offer(next)){
				history.poll();
				history.add(next);
			}
			return next;
		}
		return this.history.peek();
	}

	/**
	 * Set the next period to read.
	 * @param readNextPeriod the next period to read.
	 */
	public void setReadNextPeriod(boolean readNextPeriod){
		this.readNexPeriod = readNextPeriod;
	}
	
}
