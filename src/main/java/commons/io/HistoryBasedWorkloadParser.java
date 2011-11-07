package commons.io;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import commons.cloud.Request;
import commons.util.TimeUnit;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class HistoryBasedWorkloadParser extends TimeBasedWorkloadParser{

	private static final int DEFAULT_WINDOW_SIZE = 2;
	
	private LinkedBlockingQueue<List<Request>> history;
	private boolean readNexPeriod = true;

	/**
	 * @param parser
	 * @param tick
	 */
	//FIXME!
	public HistoryBasedWorkloadParser(WorkloadParser<Request> parser, TimeUnit tick) {
		this(parser, tick, DEFAULT_WINDOW_SIZE);
	}
	
	/**
	 * @param parser
	 * @param tick
	 * @param windowSize
	 */
	@SuppressWarnings("unchecked")
	public HistoryBasedWorkloadParser(WorkloadParser<Request> parser, TimeUnit tick, int windowSize) {
		super(tick.getMillis(), parser);//FIXME remove null
		this.history = new LinkedBlockingQueue<List<Request>>(windowSize);
	}
	
	/**
	 * @return
	 */
	public Queue<List<Request>> getHistory(){
		return new LinkedBlockingQueue<List<Request>>(history);
	}
	
	/**
	 * {@inheritDoc}
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
	
	public void setReadNextPeriod(boolean readNextPeriod){
		this.readNexPeriod = readNextPeriod;
	}
	

}
