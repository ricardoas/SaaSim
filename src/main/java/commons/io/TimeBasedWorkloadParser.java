package commons.io;

import java.util.ArrayList;
import java.util.List;

import commons.cloud.Request;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TimeBasedWorkloadParser implements WorkloadParser<List<Request>>{
	
	private final long tick;
	private long currentTick;

	private List<Request> leftOver;

	private WorkloadParser<Request>[] parsers;
	
	/**
	 * @param tick
	 * @param parser
	 */
	public TimeBasedWorkloadParser(long tick, WorkloadParser<Request>... parser) {
		this.parsers = parser;
		this.tick = tick;
		this.currentTick = 0;
		this.leftOver = new ArrayList<Request>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Request> next(){
		List<Request> requests = new ArrayList<Request>(leftOver);
		
		long time = (currentTick + 1) * tick;
		leftOver.clear();
		for (Request request : requests) {
			if(request.getArrivalTimeInMillis() >= time){
				leftOver.add(request);
			}
		}
		requests.removeAll(leftOver);
		
		for (WorkloadParser<Request> parser : parsers) {
			while(parser.hasNext()){
				Request request = parser.next();
				if(request.getArrivalTimeInMillis() < time){
					requests.add(request);
				}else{
					leftOver.add(request);
					break;
				}
			}
		}
		
		this.currentTick++;
		return requests;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		for (WorkloadParser<Request> parser: parsers) {
			if(parser.hasNext()){
				return true;
			}
		}
		return false;
	}

}
