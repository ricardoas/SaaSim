package commons.io;

import commons.cloud.Request;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class TimeBasedWorkloadParserWithError extends TimeBasedWorkloadParser{
	
	/**
	 * @param tick
	 * @param parser
	 */
	public TimeBasedWorkloadParserWithError(long tick, WorkloadParser<Request>... parser) {
		super(tick, parser);
	}
	
	@Override
	public WorkloadParser<Request> clone() {
		return null;
	}
	
	@Override
	public void applyError(double error) {
		if(error == 0.0){
			return;
		}
		
		int totalParsers = (int)Math.round(this.parsers.length * (1+error));
		WorkloadParser<Request>[] newParsers = new WorkloadParser[totalParsers];
		if(totalParsers > this.parsers.length){//Adding already existed parsers
			int difference = totalParsers - this.parsers.length;
			for(int i = 0; i < this.parsers.length; i++){
				newParsers[i] = this.parsers[i];
			}
			int index = this.parsers.length;
			for(int i = 0; i < difference; i++){
				newParsers[index++] = this.parsers[i].clone();
			}
		}else{//Removing some parsers
			for(int i = 0; i < totalParsers; i++){
				newParsers[i] = this.parsers[i];
			}
		}
		
		this.parsers = newParsers;
	}
}
