package commons.io;

import java.util.ArrayList;
import java.util.List;

import commons.cloud.Request;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TimeBasedWorkloadParser implements WorkloadParser<List<Request>>{
	
	protected final long tick;
	protected long currentTick;

	protected Request[] leftOver;
	protected WorkloadParser<Request>[] parsers;
	
	/**
	 * @param tick
	 * @param parser
	 */
	public TimeBasedWorkloadParser(long tick, WorkloadParser<Request>... parser) {
		if(parser.length == 0){
			throw new RuntimeException("Invalid TimeBasedWorkloadParser: no parsers!");
		}
		this.parsers = parser;
		this.tick = tick;
		this.currentTick = Checkpointer.loadSimulationInfo().getCurrentDayInMillis() + tick;
		this.leftOver = new Request[parsers.length];
	}
	
	@Override
	public void clear() {
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Request> next(){
		List<Request> requests = new ArrayList<Request>();
		
		for (int i = 0; i < leftOver.length; i++) {
			Request left = leftOver[i];
			if(left != null){
				if(left.getArrivalTimeInMillis() < currentTick){
					requests.add(left);
					leftOver[i] = null;
				}
			}
		}
		
		for (int i = 0; i < parsers.length; i++) {
			if(leftOver[i] == null){
				WorkloadParser<Request> parser = parsers[i];
				while(parser.hasNext()){
					Request next = parser.next();
					if(next.getArrivalTimeInMillis() < currentTick){
						requests.add(next);
					}else{
						leftOver[i] = next;
						break;
					}
				}
			}
		}
		
		this.currentTick += tick;
		return requests;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasNext() {
		for (int i = 0; i < parsers.length; i++) {
			if(leftOver[i] != null || parsers[i].hasNext()){
				return true;
			}
		}
		return false;
	}

	@Override
	public void setDaysAlreadyRead(int simulatedDays) {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public void close() {
		for(WorkloadParser<Request> parser : parsers){
			parser.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return parsers.length;
	}
}
