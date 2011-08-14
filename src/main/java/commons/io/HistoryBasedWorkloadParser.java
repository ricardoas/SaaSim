package commons.io;

import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import commons.cloud.Request;

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
	public HistoryBasedWorkloadParser(WorkloadParser<Request> parser, long tick) {
		this(parser, tick, DEFAULT_WINDOW_SIZE);
	}
	
	/**
	 * @param parser
	 * @param tick
	 * @param windowSize
	 */
	public HistoryBasedWorkloadParser(WorkloadParser<Request> parser, long tick, int windowSize) {
		super(parser, tick);
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
	public List<Request> next() throws IOException {
		if(readNexPeriod){
			List<Request> next = super.next();
			if(!history.offer(next)){
				history.poll();
				history.add(next);
			}
			return next;
		}else{
			return this.history.peek();
		}
	}
	
	public void setReadNextPeriod(boolean readNextPeriod){
		this.readNexPeriod = readNextPeriod;
	}
	

}
